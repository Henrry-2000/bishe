package com.yhq.bishe.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: bisheBackend-master
 * @description: 测试Redisson
 * @author: HenryYang
 * @create: 2023-03-19 10:46
 **/
@SpringBootTest
@Slf4j
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add("yupi");
        System.out.println(list.get(0));
        list.remove(0);
//       操作redisson和操作java基础数据结构一样
        RList<String> rlist = redissonClient.getList("test-list");
        rlist.add("yupi");
        System.out.println(rlist.get(0));
        rlist.remove(0);
    }
    @Test
    void watchDog(){
        //只有一个线程能抢到锁
        RLock lock = redissonClient.getLock("yupao:preCacheJob:docache:lock");
        try {
            if(lock.tryLock(0,30000, TimeUnit.MILLISECONDS)) {
                System.out.println("lock:"+Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println("error:"+e.getMessage());
        }
        finally {
            //判断一下是否为自己加的锁 不能unlock别人的锁
//            一定要写进finally里面 不然如果程序执行过程中报错了也可以执行。
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("unlock:"+Thread.currentThread().getId());
            }
        }
    }
}