package com.yhq.bishe.service;

import com.yhq.bishe.model.domain.User;
import once.InsertUsers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: bisheBackend-master
 * @description: 用于插入假数据
 * @author: HenryYang
 * @create: 2023-03-17 16:36
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserInsertTest {
    @Resource
    private UserService userService;

    @Test
    public void doInsertUsers(){
        final int INSERT_NUM = 100000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println("hello");
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("faker");
            user.setUserAccount("fake");
            user.setAvatarUrl("https://img10.360buyimg.com/img/jfs/t1/192028/25/33459/5661/63fc2af2F1f6ae1b6/d0e4fdc2f126cbf5.png");
            user.setGender(0);
            user.setUserPassword("yydsyyds");
            user.setPhone("19923004001");
            user.setEmail("164@163.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setTags("[\"Go\",\"chat-GPT\"]");
            user.setProfile("我是faker");
            userList.add(user);
        }
        userService.saveBatch(userList,100);
        stopWatch.stop();
        stopWatch.getTotalTimeMillis();
    }

    public static void main(String[] args) {
        new InsertUsers().doInsertUsers();
    }

}