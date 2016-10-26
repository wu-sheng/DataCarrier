package com.a.eye.datacarrier.consumer;

import com.a.eye.datacarrier.SampleData;
import com.a.eye.datacarrier.buffer.BufferStrategy;
import com.a.eye.datacarrier.buffer.Channels;
import com.a.eye.datacarrier.partition.SimpleRollingPartitioner;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.support.membermodification.MemberModifier;

/**
 * Created by wusheng on 2016/10/26.
 */
public class ConsumerPoolTest {
    @Test
    public void testBeginConsumerPool() throws IllegalAccessException {
        Channels<SampleData> channels = new Channels<SampleData>(2, 100, new SimpleRollingPartitioner<SampleData>(), BufferStrategy.BLOCKING);
        ConsumerPool<SampleData> pool = new ConsumerPool<SampleData>(channels, new SampleConsumer(), 2, true);
        pool.begin();

        ConsumerThread[] threads = (ConsumerThread[]) MemberModifier.field(ConsumerPool.class, "consumerThreads").get(pool);
        Assert.assertEquals(2, threads.length);
        Assert.assertTrue(threads[0].isAlive());
        Assert.assertTrue(threads[1].isAlive());
    }

    @Test
    public void testCloseConsumerPool() throws InterruptedException, IllegalAccessException {
        Channels<SampleData> channels = new Channels<SampleData>(2, 100, new SimpleRollingPartitioner<SampleData>(), BufferStrategy.BLOCKING);
        ConsumerPool<SampleData> pool = new ConsumerPool<SampleData>(channels, new SampleConsumer(), 2, true);
        pool.begin();

        pool.close();
        Thread.sleep(2000);
        ConsumerThread[] threads = (ConsumerThread[]) MemberModifier.field(ConsumerPool.class, "consumerThreads").get(pool);
        Assert.assertEquals(2, threads.length);
        Assert.assertFalse(threads[0].isAlive());
        Assert.assertFalse(threads[1].isAlive());
    }
}
