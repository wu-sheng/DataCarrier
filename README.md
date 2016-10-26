# DataCarrier
DataCarrier is a light, embed, high-throughput, publish-subscribe MQ.

[![Build Status](https://travis-ci.org/wu-sheng/DataCarrier.svg?branch=master)](https://travis-ci.org/wu-sheng/DataCarrier)
[![Coverage Status](https://coveralls.io/repos/github/wu-sheng/DataCarrier/badge.svg?branch=master&q=2)](https://coveralls.io/github/wu-sheng/DataCarrier?branch=master&q=2)
[ ![Download](https://api.bintray.com/packages/wu-sheng/DataCarrier/com.a.eye.data-carrier/images/download.svg) ](https://bintray.com/wu-sheng/DataCarrier/com.a.eye.data-carrier/_latestVersion)

## Why need DataCarrier
- Publish-Subscribe MQ. Support multi Producers and Consumer.
- Light and Embed. A mini java lib in jdk1.6.
- High-throughput. Used in [Sky-Walking APM](https://github.com/wu-sheng/sky-walking).
- produce data asynchronous.
- Easy to use. Simple API.

## How to use
- create a new DataCarrier instance
```java
/**
 * channelSize = 5, bufferSize per channel = 5000
 */
DataCarrier<SampleData> carrier = new DataCarrier<SampleData>(5, 5000);
```

- set message buffer strategy (optional)
```java
/**
 * default is BLOCKING
 * BLOCKING, waiting to set value to buffer, return when finished.
 * OVERRIDE, force to set value to buffer, return true forever.
 * IF_POSSIBLE, try to set value to buffer, return true when set successfully.
 */
carrier.setBufferStrategy(BufferStrategy.IF_POSSIBLE);
```

- set partitioner (optional)
```java
/**
 * default is SimpleRollingPartitioner
 * provided: ProducerThreadPartitioner, SimpleRollingPartitioner
 * you can create any partitioner, only need to implements IDataPartitioner interface
 */
carrier.setPartitioner(new ProducerThreadPartitioner<SampleData>());
```
ref to [partitioner implements](src/main/java/com/a/eye/datacarrier/partition)

- set consumer and waiting to consume data
```java
/**
   * set consumers to this Carrier.
   * consumer begin to run when {@link DataCarrier<T>#produce(T)} begin to work.
   *
   * @param prototype
   * @param num                number of consumers, which consumer will run as a independent thread
   * @param usePrototypeCopies use new instance of prototype for consumer, it will work only when prototype class have default constructor
   */
carrier.consume(consumer, 5, true);
```

- create a consumer (sample)
```java
public class SampleConsumer implements IConsumer<SampleData> {
    public int i = 1;

    @Override
    public void consume(List<SampleData> data) {
        for(SampleData one : data) {
            one.setIntValue(this.hashCode());
            ConsumerTest.buffer.offer(one);
        }
    }

    @Override
    public void onError(List<SampleData> data, Throwable t) {

    }
}
```

- produce messages as you need (sample)
```java
for (int i = 0; i < 200; i++) {
    carrier.produce(new SampleData());
}
```
