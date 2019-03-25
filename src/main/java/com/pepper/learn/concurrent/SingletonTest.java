package com.pepper.learn.concurrent;

/**
 * @Author: pei.nie
 * @Date:2019/1/16
 * @Description: 饿汉式单例
 */
public class SingletonTest {
    private static final SingletonTest single = new SingletonTest();

    private SingletonTest() {
    }

    public static final SingletonTest getInstance() {
        return single;
    }

}

/**
 * 懒汉式--错误示例：
 * 这种方式的实现虽然延迟加载减小了内存占用，但在多线程时无法保证线程安全性，假设第一个线程执行到步骤（2）时，
 * 但是尚未完成对象的初始化过程，第二个线程执行到步骤（1）则if返回的结果为true，会导致内存中存在不唯一的single对象。
 */
class Singleton {
    private static Singleton single = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (single == null) {  // (1)
            single = new Singleton();  //(2)
        }
        return single;
    }
}

/**
 * 懒汉式--改进方式1：
 * 使getInstance()方法成为同步方法或者使用同步代码块。但是这样方法的效率就会很低下。
 */
class Singleton1 {
    private static Singleton1 single = null;

    private Singleton1() {
    }

    public static Singleton1 getInstance() {
        synchronized (Singleton1.class) {
            if (single == null) {
                single = new Singleton1();
            }
            return single;
        }
    }
}


/**
 * 懒汉式--改进方式2：DCL-双重检测锁。
 * 双重检测锁在方式1的基础上提高了效率，只在第一次创建的时候会加锁，但是需要注意的是如果single对象没有
 * 使用 volatile关键字修饰依然是错误的
 */
class Singleton2 {
    private volatile static Singleton2 single = null;

    private Singleton2() {
    }

    public static Singleton2 getInstance() {
        if (single == null) {
            synchronized (Singleton2.class) {
                if (single == null) {
                    single = new Singleton2();
                }
            }
        }
        return single;
    }
}