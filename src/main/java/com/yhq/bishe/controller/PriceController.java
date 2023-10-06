package com.yhq.bishe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yhq.bishe.common.BaseResponse;
import com.yhq.bishe.common.DeleteRequest;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.common.ResultUtils;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.Price;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.model.domain.request.AddPriceRequest;
import com.yhq.bishe.model.domain.request.UpdatePriceRequest;
import com.yhq.bishe.model.dto.PriceQuery;
import com.yhq.bishe.service.PlaceService;
import com.yhq.bishe.service.PriceService;
import com.yhq.bishe.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: bisheBackend
 * @description: 用于处理订单相关的逻辑
 * @author: HenryYang
 * @create: 2023-03-30 08:16
 **/
@RestController
@RequestMapping("/price")
@CrossOrigin(allowCredentials = "true", origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class PriceController {
    @Resource
    private UserService userService;
    @Resource
    private PlaceService placeService;
    @Resource
    private PriceService priceService;

    /**
     * 新增加一个价目
     *
     * @param addPriceRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPrice(@RequestBody AddPriceRequest addPriceRequest, HttpServletRequest httpServletRequest) {
        if (addPriceRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean isAdmin = userService.isAdmin(loginUser);
        if(!isAdmin){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Price price = new Price();
        try {
            BeanUtils.copyProperties(price, addPriceRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 查找场地是否存在
        Long placeId = price.getPlaceId();
        if(placeId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long priceId = priceService.addPrice(price);
        return ResultUtils.success(priceId);
    }



    /**
     * 条件查询所有价目表 管理员
     *
     * @param priceQuery
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/admin/list")
    public BaseResponse<List<Price>> listPrice(PriceQuery priceQuery, HttpServletRequest httpServletRequest) {
        if (priceQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        boolean isAdmin = userService.isAdmin(httpServletRequest);
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<Price> queryWrapper = new QueryWrapper<>();
        Long priceId = priceQuery.getPriceId();
        if(priceId!= null && priceId > 0){
            queryWrapper.eq("priceId",priceId);
        }
        Long placeId = priceQuery.getPlaceId();
        if(placeId!=null && placeId > 0){
            queryWrapper.eq("placeId",placeId);
        }
        String sport = priceQuery.getSport();
        if(StringUtils.isNotBlank(sport)){
            queryWrapper.eq("sport",sport);
        }
        Integer type = priceQuery.getType();
        if(type!= null ){
            queryWrapper.eq("type",type);
        }
        String courtName = priceQuery.getCourtName();
        if(StringUtils.isNotBlank(courtName)){
            queryWrapper.eq("courtName",courtName);
        }
        Date beginTime = priceQuery.getBeginTime();
        Date endTime = priceQuery.getEndTime();
        if(beginTime!= null && endTime !=null){
//            todo 优化时间比较逻辑
//            queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        }
        List<Price> priceList = priceService.list(queryWrapper);

        return ResultUtils.success(priceList);
    }

    /**
     * 条件查询所有价目表 普通用户
     *
     * @param priceQuery
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<Price>> getPrice(PriceQuery priceQuery, HttpServletRequest httpServletRequest) {
        if (priceQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        //判断用户是否登录
        userService.getLoginUser(httpServletRequest);
        List<Price> priceList = priceService.getPrice(priceQuery);
        return ResultUtils.success(priceList);
//      todo 查询空闲余量人数以及对应的时间
    }

    /**
     * 删除 价目
     * @param deleteRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePrice(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest){
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(httpServletRequest);
        if(!isAdmin){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<Price> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("priceId",deleteRequest.getId());
        boolean remove = priceService.remove(queryWrapper);
        if(!remove){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新价目
     * @param updatePriceRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePrice(@RequestBody UpdatePriceRequest updatePriceRequest, HttpServletRequest httpServletRequest){
        if(updatePriceRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(httpServletRequest);
        if(!isAdmin){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // priceId必须
        Integer priceId = updatePriceRequest.getPriceId();
        if(priceId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer type = updatePriceRequest.getType();
        if(type != null &&( type > 1 || type < 0)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Date beginTime = updatePriceRequest.getBeginTime();
        Date endTime = updatePriceRequest.getEndTime();
        if((beginTime == null && endTime != null)|| (beginTime != null && endTime ==null)){
//             如果有一个为空另一个不为空那么错误
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (beginTime!= null && endTime != null){
            if(beginTime.after(endTime)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        BigDecimal unitPrice = updatePriceRequest.getUnitPrice();
//        todo 判断unitPrice不能为负数
        if(unitPrice == null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Price price = new Price();
        try {
            BeanUtils.copyProperties(price,updatePriceRequest);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = priceService.updateById(price);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);

    }
}