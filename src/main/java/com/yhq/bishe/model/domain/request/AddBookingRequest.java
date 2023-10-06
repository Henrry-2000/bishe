package com.yhq.bishe.model.domain.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: bisheBackend
 * @description: 用于封装添加订单请求类
 * @author: HenryYang
 * @create: 2023-03-29 18:02
 **/
@Data
public class AddBookingRequest implements Serializable {
    /**
     * 添加bookingTime请求类
     */
    List<AddBookingTimeRequest> addBookingTimeRequestList;
    /**
     * 订单类型 0 - 包场 1 - 散客
     */
    private Integer type;

    /**
     * 总金额
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
     * 体育项目类型
     */
    private String sport;

    private static final long serialVersionUID = 1653033555502521399L;

}