package com.yhq.bishe.model.domain.request;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: updateTeam方法封装请求类
 * @author: HenryYang
 * @create: 2023-03-20 11:48
 **/
@Data
public class UpdateTeamRequest implements Serializable {
    private static final long serialVersionUID = 1653033555502521399L;
    /**
     * id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;


}