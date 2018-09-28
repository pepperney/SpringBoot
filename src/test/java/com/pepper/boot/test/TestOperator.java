package com.pepper.boot.test;

/**
 * 与（&）
 * 非（~）
 * 或（|）
 * 异或（^）
 */
public class TestOperator {


    /**
     * 十进制转二进制
     *
     * @param num
     * @return
     */
    public static String toBinary(int num) {
        String str = "";
        while (num != 0) {
            str = num % 2 + str;
            num = num / 2;
        }
        return str;
    }

    /**
     * 二进制转十进制
     *
     * @param str
     * @return
     */
    public static Integer toDecimal(String str) {
        return Integer.parseInt(str, 2);
    }

    /**
     * 测试位运算符
     * 以129和128为例
     */
    public static void testOperator() {
        //位异或运算（^）
        //运算规则是：两个数转为二进制，然后从高位开始比较，如果相同则为0，不相同则为1。
        //比如：129&^128.
        //  10000001
        //  10000000
        //  00000001
        System.out.println(129 ^ 128);


        //位与运算符（&）
        //运算规则：两个数都转为二进制，然后从高位开始比较，如果两个数都为1则为1，否则为0。
        //比如：129&128.
        //  10000001
        //  10000000
        //  10000000
        System.out.println(129 & 128);

        //位或运算符（|）
        //运算规则：两个数都转为二进制，然后从高位开始比较，两个数只要有一个为1则为1，否则就为0。
        //比如：129|128.
        //  10000001
        //  10000000
        //  10000001
        System.out.println(129 | 128);

        //位非运算符（~）
        //运算规则：如果位为0，结果是1，如果位为1，结果是0.
        //比如：~37, 在Java中，所有数据的表示方法都是以补码的形式表示，如果没有特殊说明，Java中的数据类型默认是int,int数据类型的长度是8位，一位是四个字节，就是32bit.
        //8转为二进制是100101.补码后为：
        //  00000000 00000000 00000000 00100101取反为：
        //  11111111 11111111 11111111 11011010
        //因为高位是1，所以原码为负数，负数的补码是其绝对值的原码取反，末尾再加1。
        //因此，我们可将这个二进制数的补码进行还原： 首先，末尾减1得反码：
        //  11111111 11111111 11111111 11011001 其次，将各位取反得原码：
        //  00000000 00000000 00000000 00100110，此时二进制转原码为38
        //所以~37 = -38.
        System.out.println(~38);
    }


    public static void testTransforOpearator() {
        //java中有三种移位运算符
        //   << : 左移运算符，num << 1,相当于num乘以2
        //   >> : 右移运算符，num >> 1,相当于num除以2
        //   >>>: 无符号右移，忽略符号位，空位都以0补齐
        System.out.println(7<<1);
        System.out.println(7>>1);
        System.out.println(7>>>1);
    }

    public static void main(String[] args) {
//        int num = 15;
//        String binNum = toBinary(num);
//        System.out.println(binNum);
//        System.out.println(toDecimal(binNum));
        testOperator();
        testTransforOpearator();


    }

}


