package com.github.oscerd;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

import java.util.Map;


public class InfinispanSinkConnectorConfig extends AbstractConfig {

    public static final String INFINISPAN_CONNECTION_HOSTS_DEFAULT = "127.0.0.1";
    public static final int INFINISPAN_CONNECTION_HOTROD_PORT_DEFAULT = ConfigurationProperties.DEFAULT_HOTROD_PORT;
    public static final String INFINISPAN_CONNECTION_CACHE_NAME_DEFAULT = "default";

    public static final String INFINISPAN_CONNECTION_HOSTS_CONF = "infinispan.connection.hosts";
    private static final String INFINISPAN_CONNECTION_HOSTS_DOC = "The infinispan connection hosts";
    
    public static final String INFINISPAN_CONNECTION_HOTROD_PORT_CONF = "infinispan.connection.hotrod.port";
    private static final String INFINISPAN_CONNECTION_HOTROD_PORT_DOC = "The infinispan connection hotrod port";
    
    public static final String INFINISPAN_CONNECTION_CACHE_NAME_CONF = "infinispan.connection.cache.name";
    private static final String INFINISPAN_CONNECTION_CACHE_NAME_DOC = "The infinispan connection cache name";


  public InfinispanSinkConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);
  }

  public InfinispanSinkConnectorConfig(Map<String, String> parsedConfig) {
    this(conf(), parsedConfig);
  }

  public static ConfigDef conf() {
    return new ConfigDef()
    	  .define(INFINISPAN_CONNECTION_HOSTS_CONF, Type.STRING, INFINISPAN_CONNECTION_HOSTS_DEFAULT, Importance.HIGH, INFINISPAN_CONNECTION_HOSTS_DOC)
          .define(INFINISPAN_CONNECTION_HOTROD_PORT_CONF, Type.INT, INFINISPAN_CONNECTION_HOTROD_PORT_DEFAULT, Importance.HIGH, INFINISPAN_CONNECTION_HOTROD_PORT_DOC)
          .define(INFINISPAN_CONNECTION_CACHE_NAME_CONF, Type.STRING, INFINISPAN_CONNECTION_CACHE_NAME_DEFAULT, Importance.MEDIUM, INFINISPAN_CONNECTION_CACHE_NAME_DOC);
  }
}
