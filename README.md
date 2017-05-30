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
