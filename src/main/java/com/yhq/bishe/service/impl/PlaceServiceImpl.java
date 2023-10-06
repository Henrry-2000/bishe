package com.yhq.bishe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yhq.bishe.common.ErrorCode;
import com.yhq.bishe.exception.BusinessException;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.mapper.PlaceMapper;
import com.yhq.bishe.model.domain.request.UpdatePlaceRequest;
import com.yhq.bishe.model.dto.PlaceQuery;
import com.yhq.bishe.model.vo.PlaceVO;
import com.yhq.bishe.service.PlaceService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Henry
* @description 针对表【place(场地)】的数据库操作Service实现
* @createDate 2023-03-29 17:45:52
*/
@Service
public class PlaceServiceImpl extends ServiceImpl<PlaceMapper, Place>
    implements PlaceService{
    @Resource
    private PlaceMapper placeMapper;

    @Override
    public long addPlace(Place place) {
        String placeName = place.getPlaceName();
        if(StringUtils.isBlank(placeName)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"名称不能为空");
        }
        // 账户不能重复
        QueryWrapper<Place> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("placeName", placeName);
        long count = placeMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "场地名称重复");
        }
        place.setPlaceId(null);
        boolean result = this.save(place);
        if(!result){
            return -1;
        }
        return place.getPlaceId();
    }

    @Override
    public boolean deletePlace(long placeId) {
        //  删除场地信息
        QueryWrapper<Place> teamqueryWrapper = new QueryWrapper<>();
        teamqueryWrapper.eq("placeId", placeId);
        boolean remove = this.remove(teamqueryWrapper);
        return remove;
    }

    @Override
    public boolean updateById(UpdatePlaceRequest updatePlaceRequest) {
        Long placeId = updatePlaceRequest.getPlaceId();
        QueryWrapper<Place> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("placeId",placeId);
        long count = this.count(queryWrapper);
        if(count <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Place place = new Place();
        try {
            BeanUtils.copyProperties(place, updatePlaceRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        boolean result = this.updateById(place);
        return result;
    }
    @Override
    public Place getSafetyPlace(Place originPlace){
        if(originPlace == null){
            return null;
        }
        Place place = new Place();
        place.setPlaceId(originPlace.getPlaceId());
        place.setPlaceAddress(originPlace.getPlaceAddress());
        place.setPlaceName(originPlace.getPlaceName());
        place.setPlaceStatus(originPlace.getPlaceStatus());
        place.setEmail(originPlace.getEmail());
        place.setTags(originPlace.getTags());
        place.setPhone(originPlace.getPhone());
        place.setAvatarUrl(originPlace.getAvatarUrl());
        place.setDescriptions(originPlace.getDescriptions());
        place.setTrafficInfo(originPlace.getTrafficInfo());
        place.setOffers(originPlace.getOffers());
        return place;

    }

    @Override
    public List<Place> listPlace(PlaceQuery placeQuery) {
        QueryWrapper<Place> placeQueryWrapper = new QueryWrapper<>();
        Long placeId = placeQuery.getPlaceId();
        if(placeId != null){
            placeQueryWrapper.eq("placeId",placeId);
        }
        Integer placeStatus = placeQuery.getPlaceStatus();
        if(placeStatus != null){
            placeQueryWrapper.eq("placeStatus",placeStatus);
        }
        String placeName = placeQuery.getPlaceName();
        if(StringUtils.isNotBlank(placeName)){
            placeQueryWrapper.like("placeName",placeName);
        }
        String tags = placeQuery.getTags();
        if(StringUtils.isNotBlank(tags)){
            placeQueryWrapper.like("tags",tags);
        }
        String placeAddress = placeQuery.getPlaceAddress();
        if(StringUtils.isNotBlank(placeAddress)){
            placeQueryWrapper.like("placeAddress",placeAddress);
        }
        String descriptions = placeQuery.getDescriptions();
        if(StringUtils.isNotBlank(descriptions)){
            placeQueryWrapper.like("descriptions",descriptions);
        }

        String searchText = placeQuery.getSearchText();
        if(StringUtils.isNotBlank(searchText)){
            placeQueryWrapper.and(qw -> qw.like("placeName", searchText).or().like("tags", searchText).or().like("descriptions",searchText));
        }
        List<Place> placeList = this.list(placeQueryWrapper);
        List<Place> result = new ArrayList<>();
        for (Place place:
             placeList) {
            Place safetyPlace = this.getSafetyPlace(place);
            result.add(safetyPlace);
        }
        return result;
    }

    @Override
    public List<Place> searchPlaceByTags(List<String> tagList) {
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Place> placeQueryWrapper = new QueryWrapper<>();
        List<Place> placeList = placeMapper.selectList(placeQueryWrapper);
        Gson gson = new Gson();
        return placeList.stream().filter(place -> {
            String tagsStr = place.getTags();
            if(StringUtils.isBlank(tagsStr)){
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr,new TypeToken<Set<String>>(){}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagList){
                if(!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyPlace).collect(Collectors.toList());
    }

}




