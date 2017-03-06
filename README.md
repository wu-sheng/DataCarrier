# DataCarrier
DataCarrier is a light, embed, high-throughput, publish-subscribe MQ.

**Not Updated**, [Sky-Walking APM](https://github.com/wu-sheng/sky-walking) uses [disruptor](https://github.com/LMAX-Exchange/disruptor) instead of this, only for easy usage. For the record, DataCarrier has no performance issue, one implementation version is used in OneAPM commercial edition.

[![Coverage Status](https://coveralls.io/repos/github/wu-sheng/DataCarrier/badge.svg?branch=master&q=2)](https://coveralls.io/github/wu-sheng/DataCarrier?branch=master&q=3)
[ ![Download](https://api.bintray.com/packages/wu-sheng/DataCarrier/com.a.eye.data-carrier/images/download.svg) ](https://bintray.com/wu-sheng/DataCarrier/com.a.eye.data-carrier/_latestVersion)

## Why need DataCarrier
- Publish-Subscribe In-Memory MQ. Support multi Producers and Consumer.
- Light and Embed. A mini java lib, less than 20k, no other dependences.
- High-throughput. Used in [Sky-Walking APM](https://github.com/wu-sheng/sky-walking).
- **No lock mechanism**.
- Easy to use. Simple API
- Only need jdk1.6

## Download
- [Download](https://bintray.com/wu-sheng/DataCarrier/com.a.eye.data-carrier/_latestVersion) latest version
- Use Maven, Gradle, Ivy, SBT, etc. [set JCenter Center Repository](https://bintray.com/bintray/jcenter?filterByPkgName=com.a.eye.data-carrier)
- maven
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
          xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
    
    <profiles>
        <profile>
            <repositories>
                <repository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>central</id>
                    <name>bintray</name>
                    <url>http://jcenter.bintray.com</url>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>central</id>
                    <name>bintray-plugins</name>
                    <url>http://jcenter.bintray.com</url>
                </pluginRepository>
            </pluginRepositories>
            <id>bintray</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>bintray</activeProfile>
    </activeProfiles>
</settings>
```
```
<dependency>
  <groupId>com.a.eye</groupId>
  <artifactId>data-carrier</artifactId>
  <version>1.0</version>
</dependency>
```
- gradle
```
repositories {
    maven {
        url  "http://jcenter.bintray.com" 
    }
}
```



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

- set consumer and start to consume data
```java
    /**
     * set consumers to this Carrier.
     * consumer begin to run when {@link DataCarrier<T>#produce(T)} begin to work.
     *
     * @param consumerClass class of consumer
     * @param num           number of consumer threads
     */
    carrier.consume(SampleConsumer.class, 10);

or

    /**
     * set consumers to this Carrier.
     * consumer begin to run when {@link DataCarrier<T>#produce(T)} begin to work.
     *
     * @param consumer single instance of consumer, all consumer threads will all use this instance.
     * @param num      number of consumer threads
     * @return
     */
    carrier.consume(consumer, 10);
```

- create a consumer (sample)
```java
public class SampleConsumer implements IConsumer<SampleData> {
    public int i = 1;

    @Override
    public void init() {

    }

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
    
    @Override
    public void onExit() {

    }
}
```

- produce messages as you need (sample)
```java
for (int i = 0; i < 200; i++) {
    carrier.produce(new SampleData());
}
```

## Doc
[(中文)SkyWalking子项目--DataCarrier 1.0 解读  ](http://wu-sheng.iteye.com/blog/2334404)
