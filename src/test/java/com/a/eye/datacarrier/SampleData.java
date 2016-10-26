package com.a.eye.datacarrier;

/**
 * Created by wusheng on 2016/10/25.
 */
public class SampleData {
    private long intValue;

    private String name;

    public long getIntValue() {
        return intValue;
    }

    public String getName() {
        return name;
    }

    public SampleData setIntValue(long intValue) {
        this.intValue = intValue;
        return this;
    }

    public SampleData setName(String name) {
        this.name = name;
        return this;
    }
}
