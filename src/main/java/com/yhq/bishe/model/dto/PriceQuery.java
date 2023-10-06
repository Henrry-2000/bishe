package com.yhq.bishe.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yhq.bishe.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: 封装priceQuery类的目的是去掉不必要的请求参数
 * @author: HenryYang
 * @create: 2023-03-19 17:27
 **/


@EqualsAndHashCode(callSuper = true)
@TableName(value ="price")
@Data
public class PriceQuery extends PageRequest {
    /**
     * priceId
     */
    private Long priceId;
    /**
     * placeId
     */
    private Long placeId;

    /**
     * 体育项目名称
     */
    private String sport;

    /**
     * 订单类型
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
     * 查询字符串
     */
    private String searchText;
    /**
     * 体育设施名称
     *
     */
    private String courtName;

}