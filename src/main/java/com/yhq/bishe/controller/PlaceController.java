package com.yhq.bishe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhq.bishe.common.BaseResponse;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.common.ResultUtils;
import com.yhq.bishe.constant.PlaceConstant;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.model.domain.request.AddPlaceRequest;
import com.yhq.bishe.model.domain.request.UpdatePlaceRequest;
import com.yhq.bishe.model.dto.PlaceQuery;
import com.yhq.bishe.service.PlaceService;
import com.yhq.bishe.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: bisheBackend
 * @description: 用于处理场地相关的接口
 * @author: HenryYang
 * @create: 2023-03-29 17:56
 **/
@RestController
@RequestMapping("/place")
@CrossOrigin(allowCredentials = "true", origins = {"http://localhost:5173","http://127.0.0.1:5173"} )
public class PlaceController {
    @Resource  UserService userService;
    @Resource
    private PlaceService placeService;

    /**
     * 添加一个场地
     * @param addPlaceRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPlace(@RequestBody AddPlaceRequest addPlaceRequest, HttpServletRequest request){
        Place place = new Place();
        try {
            BeanUtils.copyProperties(place,addPlaceRequest);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if(place == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean admin = userService.isAdmin(loginUser);
        if(!admin){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        long placeId = placeService.addPlace(place);
        return ResultUtils.success(placeId);
    }

    /**
     * 删除一个场地
     * @param placeId
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePlace(Long placeId, HttpServletRequest request){
        if(placeId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean admin = userService.isAdmin(loginUser);
        if(!admin) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        boolean result = placeService.deletePlace(placeId);
        if(!result){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该场地不存在");
        }
        return ResultUtils.success(true);
    }
    /**
     * 更新场地消息
     * @param updatePlaceRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePlace(@RequestBody UpdatePlaceRequest updatePlaceRequest, HttpServletRequest request){
        //传入参数是否为空
//        todo 这个语句始终为false如何修改？ 如果前端参数为空 会在 @requestBody处爆错
        if(updatePlaceRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean admin = userService.isAdmin(loginUser);
        if(!admin){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean result = placeService.updateById(updatePlaceRequest);
        if(!result){
            throw new BusinessException(
                    ErrorCode.SYSTEM_ERROR,"更新失败"
            );
        }
        return ResultUtils.success(true);

    }

    /**
     * 通过id获取场地信息
     * @param placeId
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Place> getPlaceById(long placeId){
//        long 类型不用判空
        if(placeId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Place> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("placeId",placeId);
        Place result = placeService.getOne(queryWrapper);
//        Place result = placeService.getById(placeId);
        if(result == null){
            throw new BusinessException(
                    ErrorCode.NULL_ERROR,"获取队伍信息失败"
            );
        }
        return ResultUtils.success(result);
    }
    @GetMapping("/list")
    public BaseResponse<List<Place>> getPlaceList (PlaceQuery placeQuery , HttpServletRequest httpServletRequest){
        if(placeQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean isAdmin = userService.isAdmin(loginUser);
        if(!isAdmin){
            placeQuery.setPlaceStatus(PlaceConstant.OPEN);
        }
        List<Place> placeList = placeService.listPlace(placeQuery);
        return ResultUtils.success(placeList);
    }
    @GetMapping("/page")
    public BaseResponse<Page<Place>> getPlacePage(PlaceQuery placeQuery,HttpServletRequest httpServletRequest ){
        if(placeQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Place place = new Place();
        try{

            BeanUtils.copyProperties(place,placeQuery);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"拷贝失败");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean isAdmin = userService.isAdmin(loginUser);
        if(!isAdmin){
            place.setPlaceStatus(PlaceConstant.OPEN);
        }

        QueryWrapper<Place> queryWrapper = new QueryWrapper<>();
        Page<Place> page = new Page<>(placeQuery.getPageNum(), placeQuery.getPageSize());
        Page<Place> PlacePage = placeService.page( page,queryWrapper);
        return ResultUtils.success(PlacePage);
    }
    @GetMapping("/recommend")
    public BaseResponse<Page<Place>> recommendPlace(long pageSize, long pageNum, HttpServletRequest httpServletRequest ){
        Place place = new Place();
        try {
            User loginUser = userService.getLoginUser(httpServletRequest);
            boolean isAdmin = userService.isAdmin(loginUser);
            if(!isAdmin){
                place.setPlaceStatus(PlaceConstant.OPEN);
            }
        }catch (Exception e){
//            用户未登录 只能查看open的场地
            place.setPlaceStatus(PlaceConstant.OPEN);
        }
//       展示所有场地信息
//        todo 推荐场地和用户tags结合起来
        QueryWrapper<Place> queryWrapper = new QueryWrapper<>(place);
        Page<Place> page = new Page<>(pageNum, pageSize);
        Page<Place> PlacePage = placeService.page( page,queryWrapper);
        return ResultUtils.success(PlacePage);
    }
    @GetMapping("/search/tags")
    public BaseResponse<List<Place>> searchPlaceByTags(@RequestParam(required = false) List<String> tagList){
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Place> placeList = placeService.searchPlaceByTags(tagList);
        return ResultUtils.success(placeList);
    }


}