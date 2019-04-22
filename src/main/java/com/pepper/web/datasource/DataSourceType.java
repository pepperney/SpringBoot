package com.pepper.web.datasource;

public enum DataSourceType {
    READ("READ"),
    WRITE("WRITE"),
    ;

    DataSourceType(String value){
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
