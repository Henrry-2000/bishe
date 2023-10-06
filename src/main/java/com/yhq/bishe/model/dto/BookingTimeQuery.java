package com.yhq.bishe.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yhq.bishe.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: 封装BookingTimeQuery类的目的是去掉不必要的请求参数
 * @author: HenryYang
 * @create: 2023-03-19 17:27
 **/


@EqualsAndHashCode(callSuper = true)
//todo tableName注解什么意思？ bookingtime ??
@TableName(value ="bookingtime")
@Data
public class BookingTimeQuery extends PageRequest {


    /**
     * 订单类型 0 - 包场 1 - 散客
     */
    private Integer type;


    /**
     * 场地id
     */
    private Long placeId;

    /**
     *  体育项目名称
     */
    private String sport;
    /**
     * 预定日期
     */
    private Date beginTime;
    /**
     * 预定日期
     */
    private Date endTime;


}