package com.yhq.bishe.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: bisheBackend-master
 * @description: 分页请求类
 * @author: HenryYang
 * @create: 2023-03-20 08:51
 **/
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -3282448911038105625L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;
    /**
     * 当前是第几页
     */
    protected int pageNum = 1;


}