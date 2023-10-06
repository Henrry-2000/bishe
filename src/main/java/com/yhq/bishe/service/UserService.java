package com.yhq.bishe.service;

import com.yhq.bishe.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author HenryYang
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);


    /**
     * 根据标签搜索用户
     *
     * @param tagList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagList);

    /**
     * 更新用户信息
     *
     * @param user
     * @param loginUser
     * @return int 返回值为数据库更改的列数
     */
    int updateUser(User user, User loginUser);


    /**
     * 获取当前登录用户的信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */

    boolean isAdmin(HttpServletRequest request) ;

    /**
     * 是否为管理员 重载方法
     * @param user
     * @return boolean是否为管理员
     */

    boolean isAdmin(User user);

    List<User> match(long num, User loginUser);
}
