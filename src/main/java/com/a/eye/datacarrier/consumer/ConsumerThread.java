package com.a.eye.datacarrier.consumer;

import com.a.eye.datacarrier.buffer.Buffer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wusheng on 2016/10/25.
 */
public class ConsumerThread<T> extends Thread {
    private volatile boolean          running;
    private          IConsumer<T>     consumer;
    private          List<DataSource> dataSources;

    ConsumerThread(String threadName, IConsumer<T> consumer) {
        super((threadName));
        this.consumer = consumer;
        running = false;
        dataSources = new LinkedList<DataSource>();
    }

    /**
     * add partition of buffer to consume
     *
     * @param sourceBuffer
     * @param start
     * @param end
     */
    void addDataSource(Buffer<T> sourceBuffer, int start, int end) {
        this.dataSources.add(new DataSource(sourceBuffer, start, end));
    }

    /**
     * add whole buffer to consume
     *
     * @param sourceBuffer
     */
    void addDataSource(Buffer<T> sourceBuffer) {
        this.dataSources.add(new DataSource(sourceBuffer, 0, sourceBuffer.getBufferSize()));
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            boolean hasData = false;
            for (DataSource dataSource : dataSources) {
                List<T> data = dataSource.obtain();
                if(data.size() == 0){
                    continue;
                }
                hasData = true;
                try {
                    consumer.consume(data);
                } catch (Throwable t) {
                    consumer.onError(data, t);
                }
            }
            if(!hasData){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }

        }
    }

    void shutdown() {
        running = false;
    }

    /**
     * DataSource is a refer to {@link Buffer}.
     */
    class DataSource {
        private Buffer<T> sourceBuffer;
        private int       start;
        private int       end;

        DataSource(Buffer<T> sourceBuffer, int start, int end) {
            this.sourceBuffer = sourceBuffer;
            this.start = start;
            this.end = end;
        }

        List<T> obtain() {
            return sourceBuffer.obtain(start, end);
        }
    }
}
