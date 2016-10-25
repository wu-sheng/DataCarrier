package com.a.eye.datacarrier.buffer;

import com.a.eye.datacarrier.common.AtomicRangeInteger;

/**
 * Created by wusheng on 2016/10/25.
 */
class Buffer<T> {
    private final Object[]           buffer;
    private       BufferStrategy     strategy;
    private       AtomicRangeInteger index;

    Buffer(int bufferSize, BufferStrategy strategy) {
        buffer = new Object[bufferSize];
        this.strategy = strategy;
        index = new AtomicRangeInteger(0, bufferSize);
    }

    void setStrategy(BufferStrategy strategy) {
        this.strategy = strategy;
    }

    boolean save(T data) {
        int i = index.getAndIncrement();
        if (buffer[i] != null) {
            switch (strategy) {
                case BLOCKING:
                    while (buffer[i] != null) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // do nothing but blocking
                        }
                    }
                    break;
                case IF_POSSIBLE:
                    return false;
                case OVERRIDE:
                default:
            }
        }
        buffer[i] = data;
        return true;
    }

}
