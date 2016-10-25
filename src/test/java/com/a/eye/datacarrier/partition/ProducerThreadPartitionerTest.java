package com.a.eye.datacarrier.partition;

import com.a.eye.datacarrier.SampleData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wusheng on 2016/10/25.
 */
public class ProducerThreadPartitionerTest {
    @Test
    public void testPartition(){
        int partitionNum = (int)Thread.currentThread().getId() % 10;
        ProducerThreadPartitioner<SampleData> partitioner = new ProducerThreadPartitioner<SampleData>();
        Assert.assertEquals(partitioner.partition(10, new SampleData()), partitionNum);
        Assert.assertEquals(partitioner.partition(10, new SampleData()), partitionNum);
        Assert.assertEquals(partitioner.partition(10, new SampleData()), partitionNum);
    }
}
