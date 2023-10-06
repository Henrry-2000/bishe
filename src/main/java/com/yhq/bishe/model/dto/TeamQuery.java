package com.yhq.bishe.model.dto;

import com.baomidou.mybatisplus.annotation.*;

import java.util.List;

import com.yhq.bishe.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: bisheBackend-master
 * @description: 封装teamQuery类的目的是去掉不必要的请求参数
 * @author: HenryYang
 * @create: 2023-03-19 17:27
 **/


@EqualsAndHashCode(callSuper = true)
@TableName(value ="team")
@Data
public class TeamQuery extends PageRequest {
    /**
     * 队伍id
     */
    private Long id;
    /**
     * 队伍id列表
     */
    private List<Long> idList;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
    /**
     * 搜索关键词 同时对队伍名称和描述有效
     */
    private String searchText;

    /**
     * 最大人数
     */
    private Integer maxNum;


    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}