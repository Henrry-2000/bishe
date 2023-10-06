package com.yhq.bishe.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: 队伍和用户信息封装类(脱敏)
 * @author: HenryYang
 * @create: 2023-03-20 19:32
 **/
@Data
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = 1085681544963975777L;
    /**
     * 队伍id
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人用户信息
     */
    private UserVO createUser;
    /**
     * 当前用户是否加入该队伍
     */
    private boolean hasJoined = false;

    /**
     * 加入用户的数目
     */
    private Integer hasJoinNum ;
}