package com.yhq.bishe.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yhq.bishe.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: bisheBackend-master
 * @description: 封装BookingQuery类的目的是去掉不必要的请求参数
 * @author: HenryYang
 * @create: 2023-03-19 17:27
 **/


@EqualsAndHashCode(callSuper = true)
//todo tableName注解什么意思？
@TableName(value ="booking")
@Data
public class BookingQuery extends PageRequest {
    /**
     * bookingId
     */
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
     *  体育项目名称
     */
    private String sport;

    /**
     * 结束时间
     */
    private Date endTime;
}