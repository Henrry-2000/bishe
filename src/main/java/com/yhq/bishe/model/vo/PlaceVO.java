package com.yhq.bishe.model.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类
 * @TableName Place包装类
 */
@TableName(value ="place")
@Data
public class PlaceVO implements Serializable {
    /**
     * id
     */
    private long id;
//    改为非包装类不用判空。
    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;
    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;


    /**
     * 标签 tags
     */
    private  String tags;
    /**
     * 运动项目 json列表
     */
    private  String offers;
    /**
     *  个人简介
     */
    private String profile;

}