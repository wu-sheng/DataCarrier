package com.a.eye.datacarrier;

import com.a.eye.datacarrier.buffer.BufferStrategy;
import com.a.eye.datacarrier.buffer.Channels;
import com.a.eye.datacarrier.consumer.ConsumerPool;
import com.a.eye.datacarrier.consumer.IConsumer;
import com.a.eye.datacarrier.partition.IDataPartitioner;
import com.a.eye.datacarrier.partition.SimpleRollingPartitioner;

/**
 * DataCarrier main class.
 * use this instance to set Producer/Consumer Model
 * <p>
 * Created by wusheng on 2016/10/25.
 */
public class DataCarrier<T> {
    private final    int             bufferSize;
    private final    int             channelSize;
    private          Channels<T>     channels;
    private          ConsumerPool<T> consumerPool;

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
     * Notice, {@link com.a.eye.datacarrier.buffer.Channels<T>} will override several channels one by one.
     *
     * @param strategy
     */
    public DataCarrier setBufferStrategy(BufferStrategy strategy) {
        this.channels.setStrategy(strategy);
        return this;
    }

    /**
     * produce data to buffer, using the givven {@link BufferStrategy}.
     *
     * @param data
     * @return false means produce data failure. The data will not be consumed.
     */
    public boolean produce(T data) {
        if (consumerPool != null) {
            if(!consumerPool.isRunning()){
                return false;
            }
        }

        return this.channels.save(data);
    }

    /**
     * set consumers to this Carrier.
     * consumer begin to run when {@link DataCarrier<T>#produce(T)} begin to work.
     *
     * @param prototype
     * @param num                number of consumers, which consumer will run as a independent thread
     * @param usePrototypeCopies use new instance of prototype for consumer, it will work only when prototype class have default constructor
     */
    public DataCarrier consume(IConsumer<T> prototype, int num, boolean usePrototypeCopies) {
        if (consumerPool != null) {
            consumerPool.close();
        }
        consumerPool = new ConsumerPool<T>(this.channels, prototype, num, usePrototypeCopies);
        consumerPool.begin();
        return this;
    }

    /**
     * shutdown all consumer threads, if consumer threads are running.
     * Notice {@link BufferStrategy}:
     * if {@link BufferStrategy} == {@link BufferStrategy#BLOCKING}, shutdown consumers maybe cause blocking when producing.
     * Better way to change consumers are use {@link DataCarrier#consume(IConsumer, int, boolean)}
     */
    public void shutdownConsumers() {
        if (consumerPool != null) {
            consumerPool.close();
        }
    }
}
