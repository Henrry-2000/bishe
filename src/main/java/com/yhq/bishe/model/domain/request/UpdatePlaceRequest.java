package com.yhq.bishe.model.domain.request;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: updatePlace方法封装请求类
 * @author: HenryYang
 * @create: 2023-03-20 11:48
 **/
@Data
public class UpdatePlaceRequest implements Serializable {
    private static final long serialVersionUID = 1653033555502521399L;
    /**
     * id
     */
    private Long placeId;
    /**
     *  场地名称
     */
    private String placeName;

    /**
     * 0 - 开放，1 - 不开放
     */
    private Integer placeStatus;
    /**
     * 场地头像
     */
    private String avatarUrl;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 标签 json 列表
     */
    private String tags;
    /**
     * 提供体育项目服务 json列表
     */
    private String offers;

    /**
     * 地址
     */
    private String placeAddress;

    /**
     * 场地描述
     */
    private String descriptions;

    /**
     * 交通信息
     */
    private String trafficInfo;



}