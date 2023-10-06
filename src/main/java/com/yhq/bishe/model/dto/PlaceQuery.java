package com.yhq.bishe.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yhq.bishe.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @program: bisheBackend-master
 * @description: 封装placeQuery类的目的是去掉不必要的请求参数
 * @author: HenryYang
 * @create: 2023-03-19 17:27
 **/


@EqualsAndHashCode(callSuper = true)
@TableName(value ="place")
@Data
public class PlaceQuery extends PageRequest {
    /**
     * placeId
     */
    private Long placeId;

    /**
     * 场地名称
     */
    private String placeName;

    /**
     * 状态 0 - 正常
     */
    private Integer placeStatus;

    /**
     * 标签 json 列表
     */
    private String tags;
    /**
     * 提供运动项目 json列表
     */
    private  String offers;
    /**
     * 地址
     */
    private String placeAddress;
    /**
     * 场地描述
     */
    private String descriptions;

    /**
     * 查询字符串
     */
    private String searchText;

}