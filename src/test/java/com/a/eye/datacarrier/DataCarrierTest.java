package com.a.eye.datacarrier;

import com.a.eye.datacarrier.buffer.Buffer;
import com.a.eye.datacarrier.buffer.BufferStrategy;
import com.a.eye.datacarrier.buffer.Channels;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.support.membermodification.MemberModifier;

/**
 * Created by wusheng on 2016/10/25.
 */
public class DataCarrierTest {
    @Test
    public void testCreateDataCarrier() throws IllegalAccessException {
        DataCarrier<SampleData> carrier = new DataCarrier<SampleData>(5, 100);
        Assert.assertEquals(((Integer) (MemberModifier.field(DataCarrier.class, "bufferSize").get(carrier))).intValue(), 100);
        Assert.assertEquals(((Integer) (MemberModifier.field(DataCarrier.class, "channelSize").get(carrier))).intValue(), 5);

        Channels<SampleData> channels = (Channels<SampleData>) (MemberModifier.field(DataCarrier.class, "channels").get(carrier));
        Assert.assertEquals(channels.getChannelSize(), 5);

        Buffer<SampleData> buffer = channels.getBuffer(0);
        Assert.assertEquals(buffer.getBufferSize(), 100);

        Assert.assertEquals(MemberModifier.field(Buffer.class, "strategy").get(buffer), BufferStrategy.BLOCKING);
    }
}
