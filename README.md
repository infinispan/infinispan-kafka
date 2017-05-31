# Kafka Connector for Infinispan

## Introduction

This is a Kafka Connector to connect to an Infinispan instance (domain or standalone). For more information about Kafka Connect, take a look [here](http://kafka.apache.org/documentation/#connect)

## Running

```
mvn clean package
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
$KAFKA_HOME/bin/connect-standalone $KAFKA_HOME/config/connect-standalone.properties config/InfinispanSinkConnector.properties
```

## Status

- Actually only the Sink Connector (from Kafka to Infinispan) has been developed and still need work.

## Sink Connector Properties

| Name                                 | Description                                                                                                            | Type    | Default   | Importance |
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------|-------- |-----------|------------|
| infinispan.connection.hosts          | List of comma separated Infinispan hosts                                                                               | string  | localhost | high       |
| infinispan.connection.hotrod.port    | Infinispan Hot Rod port                                                                                                | int     | 11222     | high       |
| infinispan.connection.cache.name     | Infinispan Cache name of use                                                                                           | String  | default   | medium     |
| infinispan.use.proto                 | If true, the Remote Cache Manager will be configured to use protostream schemas                                        | boolean | false     | medium     |
| infinispan.proto.marshaller.class    | If infinispan.use.proto is true, this option has to contain an annotated protostream class to be used                  | boolean | false     | medium     |
| infinispan.cache.force.return.values | By default, previously existing values for Map operations are not returned, if set to true the values will be returned | boolean | false     | low        |

## Configuration example

Suppose you want to store in your Infinispan cache object of kind Author. Here is Protostream annotated Author class:

```java
package org.infinispan.kafka;

import java.io.Serializable;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class Author implements Serializable {

   private String name;

   @ProtoField(number = 1, required = true)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return "Author [name=" + name + "]";
   }
}
```

You can then define the following configuration for you Infinispan Sink Connector

```txt
name=InfinispanSinkConnector
topics=mytopic
tasks.max=1
connector.class=org.infinispan.kafka.InfinispanSinkConnector
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=org.apache.kafka.connect.storage.StringConverter

infinispan.connection.hosts=127.0.0.1
infinispan.connection.hotrod.port=11222
infinispan.connection.cache.name=default
infinispan.cache.force.return.values=false
infinispan.use.proto=true
infinispan.proto.marshaller.class=org.infinispan.kafka.Author
```

At this point you will be able to run your connector. You'll need a running Infinispan server and a running Kafka server. In your Infinispan Kafka working directory run:

```
mvn clean package
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
$KAFKA_HOME/bin/connect-standalone $KAFKA_HOME/config/connect-standalone.properties config/InfinispanSinkConnector.properties
```

At this point you can try to send some messages to Kafka:

```java
package org.infinispan.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleProducer {

    public static void main(String[] args) throws JsonProcessingException {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> prod = new KafkaProducer<String, String>(props);
        Author author = new Author();
        author.setName("oscerd");
        
        ObjectMapper mapper = new ObjectMapper();

        prod.send(new ProducerRecord<String, String>("test", "key1", mapper.writeValueAsString(author)));

        prod.close();
    }
}
```

In your Infinispan default cache you should now have a key/value pair with key1 as key and an Author object as value.
