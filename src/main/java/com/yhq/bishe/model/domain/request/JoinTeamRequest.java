package com.yhq.bishe.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bisheBackend-master
 * @description: 用户加入队伍请求类
 * @author: HenryYang
 * @create: 2023-03-21 10:59
 **/
@Data
public class JoinTeamRequest implements Serializable {
    private static final long serialVersionUID = -8372380925408812939L;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 队伍密码
     */
    private String password;
}