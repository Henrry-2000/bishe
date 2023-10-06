package com.yhq.bishe.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已预订时间表
 * @TableName bookingtime
 */
@TableName(value ="bookingtime")
@Data
public class Bookingtime implements Serializable {
    /**
     * bookingTimeId
     */
    @TableId(type = IdType.AUTO)
    private Long bookingTimeId;

    /**
     * 场地id
     */
    private Long placeId;

    /**
     * 订单id
     */
    private Long bookingId;

    /**
     * 体育项目名称
     */
    private String sport;

    /**
     * 订单类型 0 - 单买 1 - 团购
     */
    private Integer type;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 体育设施名称，如篮球场1
     */
    private String courtName;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}