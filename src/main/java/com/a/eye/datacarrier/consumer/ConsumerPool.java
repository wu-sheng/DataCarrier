package com.a.eye.datacarrier.consumer;

import com.a.eye.datacarrier.buffer.Buffer;
import com.a.eye.datacarrier.buffer.Channels;

import java.util.ArrayList;

/**
 * Pool of consumers
 * <p>
 * Created by wusheng on 2016/10/25.
 */
public class ConsumerPool<T> {
    private Object           lock;
    private boolean          running;
    private ConsumerThread[] consumerThreads;
    private Channels<T>      channels;

    public ConsumerPool(Channels<T> channels, IConsumer<T> prototype, int num, boolean usePrototypeCopies) {
        running = false;
        this.channels = channels;
        consumerThreads = new ConsumerThread[num];
        for (int i = 0; i < num; i++) {
            consumerThreads[i] = new ConsumerThread("DataCarrier.Consumser." + i + ".Thread", usePrototypeCopies ? getNewConsumerInstance(prototype) : prototype);
        }
        lock = new Object();
    }

    private IConsumer<T> getNewConsumerInstance(IConsumer<T> prototype) {
        try {
            return prototype.getClass().newInstance();
        } catch (InstantiationException e) {
            return prototype;
        } catch (IllegalAccessException e) {
            return prototype;
        }
    }

    public void begin() {
        if (running) {
            return;
        }
        synchronized (lock) {
            if (!running) {
                synchronized (lock) {
                    this.allocateBuffer2Thread();
                    for (ConsumerThread consumerThread : consumerThreads) {
                        consumerThread.start();
                    }
                    running = true;
                }
            }
        }
    }

    private void allocateBuffer2Thread() {
        int channelSize = this.channels.getChannelSize();
        if (channelSize < consumerThreads.length) {
            /**
             * if consumerThreads.length > channelSize
             * each channel will be process by several consumers.
             */
            ArrayList<Integer>[] threadAllocation = new ArrayList[channelSize];
            for (int threadIndex = 0; threadIndex < consumerThreads.length; threadIndex++) {
                int index = threadIndex % channelSize;
                if(threadAllocation[index] == null){
                    threadAllocation[index] = new ArrayList<Integer>();
                }
                threadAllocation[index].add(threadIndex);
            }

            for (int channelIndex = 0; channelIndex < channelSize; channelIndex++) {
                ArrayList<Integer> threadAllocationPerChannel = threadAllocation[channelIndex];
                Buffer<T> channel = this.channels.getBuffer(channelIndex);
                int bufferSize = channel.getBufferSize();
                int step = bufferSize / threadAllocationPerChannel.size();
                for (int i = 0; i < threadAllocationPerChannel.size(); i++) {
                    int threadIndex = threadAllocationPerChannel.get(i);
                    int start = i * step;
                    int end = i == threadAllocationPerChannel.size()-1? bufferSize : (i + 1) * step;
                    consumerThreads[threadIndex].addDataSource(channel, start, end);
                }
            }
        }else {
            /**
             * if consumerThreads.length < channelSize
             * each consumer will process several channels.
             *
             * if consumerThreads.length == channelSize
             * each consumer will process one channel.
             */
            for(int channelIndex = 0; channelIndex < channelSize; channelIndex++){
                int consumerIndex = channelIndex % consumerThreads.length;
                consumerThreads[consumerIndex].addDataSource(channels.getBuffer(channelIndex));
            }
        }

    }

    public void close() {
        synchronized (lock) {
            if (running) {
                synchronized (lock) {
                    for (ConsumerThread consumerThread : consumerThreads) {
                        consumerThread.shutdown();
                    }
                }
            }
        }
    }
}
