package com.yhq.bishe.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: bisheBackend-master
 * @description: 通用删除请求类
 * @author: HenryYang
 * @create: 2023-03-20 08:51
 **/
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = -3282448911038105625L;

    /**
     * id
     */
    protected long id;


}