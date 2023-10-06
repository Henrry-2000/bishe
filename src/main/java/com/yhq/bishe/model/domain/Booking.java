package com.yhq.bishe.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 * @TableName booking
 */
@TableName(value ="booking")
@Data
public class Booking implements Serializable {
    /**
     * bookingId
     */
    @TableId(type = IdType.AUTO)
    private Long bookingId;

    /**
     * 状态0 - 待支付 1 - 支付成功 2 - 取消（未支付）3 - 退款
     */
    private Integer bookingStatus;

    /**
     * 订单类型 0 - 包场 1 - 散客
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal fee;

    /**
     * 体育项目名称
     */
    private String sport;

    /**
     * 场地id
     */
    private Long placeId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

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