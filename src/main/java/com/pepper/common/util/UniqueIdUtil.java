package com.pepper.common.util;

import java.util.Date;
import java.util.Random;

/**
 * @Author: pei.nie
 * @Date:2019/9/23
 * @Description:生成唯一id,基于snowflake改造
 */
public class UniqueIdUtil {

    private static Random random = new Random();

    /**
     * 根据时间获取唯一id
     *
     * @param dateTime 时间
     * @return
     */
    public static String getUniqueIdByTime(Date dateTime) {
        long uniqueId = UniqueId.makeFrom(0, dateTime.getTime(), random.nextInt(256), 1, 0, 2);
        return String.valueOf(uniqueId);
    }

    /**
     * 获取唯一id
     *
     * @return
     */
    public static String getUniqueId() {
        return getUniqueIdByTime(new Date());
    }

    /**
     * 解析唯一id的组成
     *
     * @param uniqueId
     * @return
     */
    public static UniqueId decoderUniqueId(long uniqueId) {
        return UniqueId.getInstance(uniqueId);
    }


    public static class UniqueId {

        public static final int MAX_SEQ = 255;
        public static final int MAX_BATCH = 100;    //should be less than half of MAX_SEQ

        public static final long BASE_TIME = 1420041600000L;        //2015-01-01 GMT-8

        private final int version;
        private final long timestamp;
        private final int sequence;
        private final int idc;
        private final int server;
        private final int biz;

        private final long value;

        private UniqueId(long value) {
            int version = (int) (value >> 63) & 0x01;
            long timestamp = (value >> 23) & 0x0FFFFFFFFFFL;
            int sequence = (int) (value >> 15) & 0x0FF;
            int idc = (int) (value >> 12) & 0x07;
            int server = (int) (value >> 8) & 0x0F;
            int biz = (int) (value >> 2) & 0x3F;

            this.value = value;
            this.version = version;
            this.timestamp = timestamp;
            this.sequence = sequence;
            this.idc = idc;
            this.server = server;
            this.biz = biz;
        }

        private UniqueId(int version, long unixTimestamp, int sequence, int idc, int server, int biz) {
            long value = makeFrom(version, unixTimestamp, sequence, idc, server, biz);
            this.value = value;
            this.version = version;
            this.timestamp = unixTimestamp - BASE_TIME;
            this.sequence = sequence;
            this.idc = idc;
            this.server = server;
            this.biz = biz;
        }

        public static UniqueId getInstance(long value) {
            return new UniqueId(value);
        }

        public static UniqueId getInstance(int version, long unixTimestamp, int sequence, int idc, int server, int biz) {
            return new UniqueId(version, unixTimestamp, sequence, idc, server, biz);
        }

        public int getVersion() {
            return version;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getUnixTimestamp() {
            return BASE_TIME + timestamp;
        }

        public String getTime() {
            return String.format("%1$TF %1$TT", getUnixTimestamp());
        }

        public int getSequence() {
            return sequence;
        }

        public int getIdc() {
            return idc;
        }

        public int getServer() {
            return server;
        }

        public int getBiz() {
            return biz;
        }

        public long getValue() {
            return value;
        }

        public static long makeFrom(int version, long unixTimestamp, int sequence, int idc, int server, int biz) {
            long value = 0;
            value |= version & 0x01;                                                    //1位版本号
            value = (value << 40) | ((unixTimestamp - BASE_TIME) & 0x0FFFFFFFFFFL);     //40位时间戳
            value = (value << 8) | (sequence & 0x0FF);                                  //8位sequence
            value = (value << 3) | (idc & 0x07);                                        //3位数据中心号
            value = (value << 4) | (server & 0x0F);                                     //4位服务器号
            value = (value << 6) | (biz & 0x3F);                                        //6位业务编号
            value <<= 2;                                                                //2位扩展
            return value;
        }

        public static long getVersion(long uniqueId) {
            return (uniqueId >> 63) & 0x01;
        }

        public static long getTimestamp(long uniqueId) {
            return (uniqueId >> 23) & 0x0FFFFFFFFFFL;
        }

        public static long getUnixTimestamp(long uniqueId) {
            return BASE_TIME + ((uniqueId >> 23) & 0x0FFFFFFFFFFL);
        }

        public static long getSequence(long uniqueId) {
            return (uniqueId >> 15) & 0x0FF;
        }

        public static long getIdc(long uniqueId) {
            return (uniqueId >> 12) & 0x07;
        }

        public static long getServer(long uniqueId) {
            return (uniqueId >> 8) & 0x0F;
        }

        public static long getBiz(long uniqueId) {
            return (uniqueId >> 8) & 0x3F;
        }
    }

}
