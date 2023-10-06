package com.yhq.bishe.service;

import com.yhq.bishe.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @program: bisheBackend-master
 * @description: 用于测试编辑距离算法
 * @author: HenryYang
 * @create: 2023-03-22 19:52
 **/
@SpringBootTest
public class AlgorithmTest {

    @Test
    void test(){
        String str1 = "我是伞兵";
        String str2 = "我是特种兵";

        System.out.println(AlgorithmUtils.minDistance(str1,str2));
    }

    @Test
    void testTags(){
        List<String> list1 = Arrays.asList("java", "大二", "c++");
        List<String> list2 = Arrays.asList("java", "大一", "c++");

        System.out.println( AlgorithmUtils.minTagsDistance(list1,list2));
    }


}