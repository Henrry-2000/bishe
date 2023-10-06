package com.yhq.bishe.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: bisheBackend
 * @description: 用于封装添修改价目请求类
 * @author: HenryYang
 * @create: 2023-03-29 18:02
 **/
@Data
public class UpdatePriceRequest implements Serializable {

    /**
     * 价格id
     */
    private Integer priceId;
    /**
     * 场地id
     */
    private Integer placeId;

    /**
     * 体育项目名称
     */
    private String sport;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 开始时间点
     */
    private Date beginTime;
    /**
     * 结束时间点
     */
    private Date endTime;
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    /**
     * 体育设施名称，如篮球场1
     */
    private String courtName;

    private static final long serialVersionUID = 1653033555502521399L;

}