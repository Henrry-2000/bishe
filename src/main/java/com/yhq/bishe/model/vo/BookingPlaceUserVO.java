package com.yhq.bishe.model.vo;

import com.yhq.bishe.model.domain.Place;
import com.yhq.bishe.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: bisheBackend
 * @description: 用于展示订单所关联的用户信息和场地信息
 * @author: HenryYang
 * @create: 2023-03-30 11:11
 **/
@Data
public class BookingPlaceUserVO implements Serializable {
    private static final long serialVersionUID = 1085681544963975777L;
    /**
     * bookingId
     */
    private Long bookingId;

    /**
     * 状态 0 - 待支付 1 - 支付成功 2 - 取消（未支付）3 - 退款
     */
    private Integer bookingStatus;

    /**
     * 订单类型 0 - 包场 1 - 散客
     */
    private Integer type;
    /**
     *  体育项目名称
     */
    private String sport;

    /**
     * 金额
     */
    private BigDecimal fee;

    /**
     * 场地id
     */
    private Long placeId;
    /**
     * 场地信息
     */
    private Place place;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 创建人用户信息
     */
//    todo 修改为UserVO
    private User createUser;


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
     * 更新时间
     */
    private Date updateTime;
}