package com.yhq.bishe.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: bisheBackend-master
 * @description: 支付订单请求类
 * @author: HenryYang
 * @create: 2023-03-21 13:49
 **/
@Data
public class PayBookingRequest implements Serializable {
    private static final long serialVersionUID = 4293420544208268679L;
    /**
     * bookingId
     */
    private Long bookingId;
    /**
     * 密码
     */
    private String userPassword;


}