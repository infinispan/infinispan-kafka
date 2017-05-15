Kafka Connector Infinispan

# Introduction

This is a Kafka Connector to connect to an Infinispan instance (domain or standalone). For more information about Kafka Connect, take a look [here](http://kafka.apache.org/documentation/#connect)

# Running

```
mvn clean package
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
$KAFKA_HOME/bin/connect-standalone $KAFKA_HOME/config/connect-standalone.properties config/InfinispanSinkConnector.properties
```

# Status

- Actually only the Sink Connector (from Kafka to Infinispan) has been developed and still need work
- The Source Connector is in the roadmap too by the way
