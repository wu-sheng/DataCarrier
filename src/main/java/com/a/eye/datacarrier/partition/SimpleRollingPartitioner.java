package com.a.eye.datacarrier.partition;

/**
 * use normal int to rolling.
 *
 *
 * Created by wusheng on 2016/10/25.
 */
public class SimpleRollingPartitioner <T> implements IDataPartitioner<T> {
    private int i = 0;

    @Override
    public int partition(int total, T data) {
        return Math.abs(i++ % total);
    }
}
