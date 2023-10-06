package com.yhq.bishe.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 价目表
 * @TableName price
 */
@TableName(value ="price")
@Data
public class Price implements Serializable {
    /**
     * priceId
     */
    @TableId(type = IdType.AUTO)
    private Long priceId;

    /**
     * 场地id
     */
    private Long placeId;

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
     * 单价
     */
    private BigDecimal unitPrice;

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
     * 体育设施名称，如篮球场1
     */
    private String courtName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}