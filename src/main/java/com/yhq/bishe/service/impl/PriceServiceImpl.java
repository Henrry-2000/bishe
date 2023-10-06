package com.yhq.bishe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.Price;
import com.yhq.bishe.mapper.PriceMapper;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.model.dto.PriceQuery;
import com.yhq.bishe.service.PlaceService;
import com.yhq.bishe.service.PriceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* @author Henry
* @description 针对表【price(价目表)】的数据库操作Service实现
* @createDate 2023-03-31 17:20:33
*/
@Service
public class PriceServiceImpl extends ServiceImpl<PriceMapper, Price>
    implements PriceService{
    @Resource
    private PlaceService placeService;
    @Override
    public Long addPrice(Price price) {
        String sport = price.getSport();
        if(StringUtils.isBlank(sport)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long placeId = price.getPlaceId();
        Place place = placeService.getById(placeId);
        if(place == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"placeId不存在，添加失败");
        }
        String offers = place.getOffers();
        if(StringUtils.isBlank(offers)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"场地不提供的体育项目");
        }
        boolean contains = StringUtils.contains(offers, sport);
        if(!contains){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"场地不提供的体育项目");
        }
        String courtName = price.getCourtName();
        if(StringUtils.isBlank(courtName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"体育设施名称不能为空");
        }
        Integer type = price.getType();
        if(type == null || type > 1 || type < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Date beginTime = price.getBeginTime();
        Date endTime = price.getEndTime();
        if((beginTime == null && endTime != null)|| (beginTime != null && endTime ==null)){
//             如果有一个为空另一个不为空那么错误
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (beginTime!= null && endTime != null){
            if(beginTime.after(endTime)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        BigDecimal unitPrice = price.getUnitPrice();
//        todo 判断unitPrice不能为负数
        if(unitPrice == null ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean save = this.save(price);
        if(!save){
            return null;
        }
        Long priceId = price.getPriceId();
        if(priceId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建价目失败");
        }
        return priceId;
    }

    @Override
    public List<Price> getPrice(PriceQuery priceQuery) {
        QueryWrapper<Price> queryWrapper = new QueryWrapper<>();
        Long placeId = priceQuery.getPlaceId();
        if(placeId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        queryWrapper.eq("placeId",placeId);
        String sport = priceQuery.getSport();
        if(StringUtils.isNotBlank(sport)){
            queryWrapper.eq("sport",sport);
        }
        Integer type = priceQuery.getType();
        if(type != null){
            queryWrapper.eq("type",type);
        }
        String courtName = priceQuery.getCourtName();
        if(StringUtils.isNotBlank(courtName)){
            queryWrapper.eq("courtName",courtName);
        }
        Date beginTime = priceQuery.getBeginTime();
        Date endTime = priceQuery.getEndTime();
        //todo 时间比较逻辑
        List<Price> priceList = this.list(queryWrapper);
        return priceList;
    }
}




