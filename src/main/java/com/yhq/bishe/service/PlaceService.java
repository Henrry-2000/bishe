package com.yhq.bishe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.request.UpdatePlaceRequest;
import com.yhq.bishe.model.dto.PlaceQuery;

import java.util.List;

/**
* @author Henry
* @description 针对表【place(场地)】的数据库操作Service
* @createDate 2023-03-29 17:45:52
*/
public interface PlaceService extends IService<Place> {

    /**
     * 新增一个场地信息
     * @param place
     * @return 返回placeId
     */
    long addPlace(Place place);

    /**
     * 删除场地
     * @param placeId
     * @return
     */
    boolean deletePlace(long placeId);

    /**
     * 更新场地信息
     * @param updatePlaceRequest
     * @return
     */
    boolean updateById(UpdatePlaceRequest updatePlaceRequest);

    /**
     * 场地信息脱敏
     * @param originPlace
     * @return
     */
    Place getSafetyPlace(Place originPlace);

    /**
     * 搜索场地
     * @param placeQuery
     * @return
     */
    List<Place> listPlace(PlaceQuery placeQuery);

    /**
     * 通过标签搜索场地
     * @param tagList
     * @return
     */
    List<Place> searchPlaceByTags(List<String> tagList);
}
