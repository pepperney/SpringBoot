package com.pepper.web.datasource;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();


    public static void setWrite(){
        contextHolder.set(DataSourceType.WRITE.getValue());
    }

    public static void setRead(){
        contextHolder.set(DataSourceType.READ.getValue());
    }

    public static String getDataSourceType(){
        return contextHolder.get();
    }

    public static void clearDataSourceType() {

        contextHolder.remove();
    }
}
