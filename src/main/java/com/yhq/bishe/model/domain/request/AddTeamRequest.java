package com.yhq.bishe.model.domain.request;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: addTeam方法封装请求类
 * @author: HenryYang
 * @create: 2023-03-20 11:48
 **/
@Data
public class AddTeamRequest implements Serializable {
    private static final long serialVersionUID = 1653033555502521399L;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;


}