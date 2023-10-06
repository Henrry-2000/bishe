package com.yhq.bishe.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: bisheBackend
 * @description: 用于封装添加队伍请求类
 * @author: HenryYang
 * @create: 2023-03-29 18:02
 **/
@Data
public class AddBookingTimeRequest implements Serializable {

    /**
     * 场地id
     */
    private Long placeId;

    /**
     * 体育项目类型
     */
    private String sport;
    /**
     * 订单类型 0 - 包场 1 - 散客
     */
    private Integer type;
    /**
     * 体育设施名称
     */
    private String courtName;
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

    private static final long serialVersionUID = 1653033555502521399L;

}