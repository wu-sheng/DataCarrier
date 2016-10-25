package com.a.eye.datacarrier.partition;

import com.a.eye.datacarrier.SampleData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wusheng on 2016/10/25.
 */
public class SimpleRollingPartitionerTest {
    @Test
    public void testPartition(){
        SimpleRollingPartitioner<SampleData> partitioner = new SimpleRollingPartitioner<SampleData>();
        Assert.assertEquals(partitioner.partition(10, new SampleData()), 0);
        Assert.assertEquals(partitioner.partition(10, new SampleData()), 1);
        Assert.assertEquals(partitioner.partition(10, new SampleData()), 2);
    }
}
