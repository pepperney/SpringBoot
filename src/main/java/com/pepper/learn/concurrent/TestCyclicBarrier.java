package com.pepper.learn.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;

/**
 * @Auther: pei.nie
 * @Date:2018/8/4
 * @Description:演示关卡的使用
 */
public class TestCyclicBarrier {
    public void test() {

        CyclicBarrier barrier = new CyclicBarrier(1);

        Collections.synchronizedCollection(new ArrayList<>());
    }
}
