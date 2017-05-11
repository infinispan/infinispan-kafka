Welcome to your new Kafka Connect connector!

# Running in development

```
mvn clean package
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
$KAFKA_HOME/bin/connect-standalone $KAFKA_HOME/config/connect-standalone.properties config/InfinispanSinkConnector.properties
```
