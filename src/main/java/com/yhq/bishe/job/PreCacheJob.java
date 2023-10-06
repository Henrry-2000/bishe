package com.yhq.bishe.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhq.bishe.model.domain.User;
import com.yhq.bishe.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: bisheBackend-master
 * @description: 使用定期执行的方式预加载缓存，用于解决用户第一次加载首页等待时间长的问题
 * @author: HenryYang
 * @create: 2023-03-18 11:04
 **/
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);
    @Scheduled(cron = "0 0 11 * * *")
    public void doRecommendUsers(){
//        只有一个线程能抢到锁
        RLock lock = redissonClient.getLock("yupao:preCacheJob:docache:lock");
        try {
            if(lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
                ValueOperations<String, Object> valueOperations  = redisTemplate.opsForValue();
                for (int i = 0; i < mainUserList.size(); i++) {
                    Long userId = mainUserList.get(i);
                    String redisKey = String.format("yupao:user:recommend:%s",userId);
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    IPage userPage = userService.page(new Page<>(1,20),queryWrapper);
                    try {
                        valueOperations.set(redisKey,userPage,10, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            //判断一下是否为自己加的锁 不能unlock别人的锁
//            一定要写进finally里面 不然如果程序执行过程中报错了也可以执行。
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}