package once;

import com.yhq.bishe.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @program: bisheBackend-master
 * @description: 用于插入用户信息
 * @author: HenryYang
 * @create: 2023-03-17 15:53
 **/

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

//    @Scheduled(fixedDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInsertUsers(){
        final int INSERT_NUM = 100000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println("hello");
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("faker");
//            user.setUserAccount("fake");
//            user.setAvatarUrl("https://img10.360buyimg.com/img/jfs/t1/192028/25/33459/5661/63fc2af2F1f6ae1b6/d0e4fdc2f126cbf5.png");
//            user.setGender(0);
//            user.setUserPassword("yydsyyds");
//            user.setPhone("19923004001");
//            user.setEmail("164@163.com");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setPlanetCode("999");
//            user.setTags("[\"Go\",\"chat-GPT\"]");
//            user.setProfile("我是faker");
//            userMapper.insert(user);
//        }
        stopWatch.stop();
        stopWatch.getTotalTimeMillis();
    }

    public static void main(String[] args) {
        new InsertUsers().doInsertUsers();
    }
}