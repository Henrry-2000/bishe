package com.yhq.bishe.model.domain.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bisheBackend
 * @description: 用于封装添加队伍请求类
 * @author: HenryYang
 * @create: 2023-03-29 18:02
 **/
@Data
public class AddPlaceRequest implements Serializable {

    /**
     * 场地名称
     */
    private String placeName;

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
     * 提供体育项目服务 json 列表
     */
    private String offers;

    /**
     * 场地地址
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

    private static final long serialVersionUID = 1653033555502521399L;

}