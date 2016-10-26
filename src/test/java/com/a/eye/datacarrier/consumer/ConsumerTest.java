package com.a.eye.datacarrier.consumer;

import com.a.eye.datacarrier.DataCarrier;
import com.a.eye.datacarrier.SampleData;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.support.membermodification.MemberModifier;

import java.util.List;

/**
 * Created by wusheng on 2016/10/26.
 */
public class ConsumerTest {
    @Test
    public void testConsumer() throws IllegalAccessException {
        final DataCarrier<SampleData> carrier = new DataCarrier<SampleData>(2, 100);

        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(carrier.produce(new SampleData().setName("data" + i)));
        }
        SampleConsumer consumer = new SampleConsumer();

        consumer.i = 100;
        carrier.consume(consumer, 1, true);
        Assert.assertEquals(1, ((SampleConsumer)getConsumer(carrier)).i);

        SampleConsumer2 consumer2 = new SampleConsumer2();
        consumer2.i = 100;
        carrier.consume(consumer2, 1, true);
        Assert.assertEquals(100, ((SampleConsumer2)getConsumer(carrier)).i);
    }

    class SampleConsumer2 implements IConsumer<SampleData> {
        public int i = 1;

        @Override
        public void consume(List<SampleData> data) {

        }

        @Override
        public void onError(List<SampleData> data, Throwable t) {

        }
    }

    private IConsumer getConsumer(DataCarrier<SampleData> carrier) throws IllegalAccessException {
        ConsumerPool pool = ((ConsumerPool)MemberModifier.field(DataCarrier.class, "consumerPool").get(carrier));
        ConsumerThread[] threads = (ConsumerThread[])MemberModifier.field(ConsumerPool.class, "consumerThreads").get(pool);

        return (IConsumer)MemberModifier.field(ConsumerThread.class, "consumer").get(threads[0]);
    }
}
