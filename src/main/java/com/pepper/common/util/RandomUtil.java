package com.pepper.common.util;

import java.util.Random;
import java.util.UUID;

/**
 * @Auther: pei.nie
 * @Date:2018/8/28
 * @Description:
 */
public class RandomUtil {

    private static final Random random = new Random();

    static final String SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 获取指定长度的随机字符串:由数字，小写字母，大写字母构成
     * @param length
     * @return
     */
    public static String getRandomString(int length){

        return getRandomString(length,SOURCE);
    }

    /**
     * 根据指定的字符串范围获取指定长度的随机字符串
     * @param length
     * @param source
     * @return
     */
    public static String getRandomString(int length,String source){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(source.length());
            sb.append(source.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取指定范围内的随机整数
     * @param bound
     * @return
     */
    public static Integer getRandomInt(int bound){
        return random.nextInt(bound);
    }

    /**
     * 获取uuid
     * @return
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
