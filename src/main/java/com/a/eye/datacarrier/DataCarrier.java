package com.a.eye.datacarrier;

import com.a.eye.datacarrier.buffer.BufferStrategy;
import com.a.eye.datacarrier.buffer.Channels;
import com.a.eye.datacarrier.partition.IDataPartitioner;
import com.a.eye.datacarrier.partition.SimpleRollingPartitioner;

/**
 * DataCarrier main class.
 * use this instance to set Producer/Consumer Model
 * <p>
 * Created by wusheng on 2016/10/25.
 */
public class DataCarrier<T> {
    private final int         bufferSize;
    private final int         channelSize;
    private       Channels<T> channels;

    public DataCarrier(int channelSize, int bufferSize) {
        this.bufferSize = bufferSize;
        this.channelSize = channelSize;
        channels = new Channels<T>(channelSize, bufferSize, new SimpleRollingPartitioner<T>(), BufferStrategy.BLOCKING);
    }

    /**
     * set a new IDataPartitioner.
     * It will cover the current one or default one.(Default is {@link SimpleRollingPartitioner)}
     *
     * @param dataPartitioner
     * @return
     */
    public DataCarrier setPartitioner(IDataPartitioner<T> dataPartitioner) {
        this.channels.setPartitioner(dataPartitioner);
        return this;
    }

    /**
     * override the strategy at runtime.
     * Notice, {@Link com.a.eye.datacarrier.buffer.Channels<T>} will override several channels one by one.
     *
     * @param strategy
     */
    public void setBufferStrategy(BufferStrategy strategy){
        this.channels.setStrategy(strategy);
    }

    /**
     * produce data to buffer, using the givven {@Link BufferStrategy}.
     * @param data
     * @return
     */
    public boolean produce(T data) {
        return this.channels.save(data);
    }
}
