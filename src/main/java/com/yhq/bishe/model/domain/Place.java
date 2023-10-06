package com.yhq.bishe.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 场地
 * @TableName place
 */
@TableName(value ="place")
@Data
public class Place implements Serializable {
    /**
     * placeId
     */
    @TableId(type = IdType.AUTO)
    private Long placeId;

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
     * 状态 0 - 正常
     */
    private Integer placeStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 标签 json 列表
     */
    private String tags;
    /**
     * 提供体育项目 json字符串
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}