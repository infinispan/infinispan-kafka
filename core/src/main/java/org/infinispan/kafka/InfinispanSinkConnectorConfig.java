/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infinispan.kafka;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

public class InfinispanSinkConnectorConfig extends AbstractConfig {

   public static final String INFINISPAN_CONNECTION_HOSTS_DEFAULT = "127.0.0.1";
   public static final int INFINISPAN_CONNECTION_HOTROD_PORT_DEFAULT = ConfigurationProperties.DEFAULT_HOTROD_PORT;
   public static final String INFINISPAN_CONNECTION_CACHE_NAME_DEFAULT = "default";
   public static final Boolean INFINISPAN_CACHE_FORCE_RETURN_VALUES_DEFAULT = false;
   public static final Boolean INFINISPAN_USE_PROTO_DEFAULT = false;
   public static final Class INFINISPAN_PROTO_MARSHALLER_CLASS_DEFAULT = String.class;
   public static final Boolean INFINISPAN_USE_LIFESPAN_DEFAULT = false;
   public static final Boolean INFINISPAN_USE_MAX_IDLE_DEFAULT = false;
   public static final long INFINISPAN_LIFESPAN_ENTRY_DEFAULT = 0L;
   public static final long INFINISPAN_MAX_IDLE_ENTRY_DEFAULT = 0L;
   public static final String INFINISPAN_HOTROD_PROTOCOL_VERSION_DEFAULT = ProtocolVersion.DEFAULT_PROTOCOL_VERSION.name();

   public static final String INFINISPAN_CONNECTION_HOSTS_CONF = "infinispan.connection.hosts";
   private static final String INFINISPAN_CONNECTION_HOSTS_DOC = "The infinispan connection hosts";

   public static final String INFINISPAN_CONNECTION_HOTROD_PORT_CONF = "infinispan.connection.hotrod.port";
   private static final String INFINISPAN_CONNECTION_HOTROD_PORT_DOC = "The infinispan connection hotrod port";

   public static final String INFINISPAN_CONNECTION_CACHE_NAME_CONF = "infinispan.connection.cache.name";
   private static final String INFINISPAN_CONNECTION_CACHE_NAME_DOC = "The infinispan connection cache name";

   public static final String INFINISPAN_CACHE_FORCE_RETURN_VALUES_CONF = "infinispan.cache.force.return.values";
   private static final String INFINISPAN_CACHE_FORCE_RETURN_VALUES_DOC = "By default, previously existing values for Map operations are not returned, if set to true the values will be returned";

   public static final String INFINISPAN_USE_PROTO_CONF = "infinispan.use.proto";
   private static final String INFINISPAN_USE_PROTO_DOC = "If true, the Remote Cache Manager will be configured to use protostream schemas";

   public static final String INFINISPAN_PROTO_MARSHALLER_CLASS_CONF = "infinispan.proto.marshaller.class";
   private static final String INFINISPAN_PROTO_MARSHALLER_CLASS_DOC = "If infinispan.use.proto is true, this option has to contain an annotated protostream class";
   
   public static final String INFINISPAN_USE_LIFESPAN_CONF = "infinispan.use.lifespan";
   private static final String INFINISPAN_USE_LIFESPAN_DOC = "If true, the Remote Cache Manager will be configured to use Lifespan associated to cache entries";
   
   public static final String INFINISPAN_LIFESPAN_ENTRY_CONF = "infinispan.cache.lifespan.entry";
   private static final String INFINISPAN_LIFESPAN_ENTRY_DOC = "If infinispan.use.lifespan is true, this option has to be the lifespan associated with the entries to be stored (in seconds)";
   
   public static final String INFINISPAN_USE_MAX_IDLE_CONF = "infinispan.use.maxidle";
   private static final String INFINISPAN_USE_MAX_IDLE_DOC = "If true, the Remote Cache Manager will be configured to use Max idle value associated to cache entries";
   
   public static final String INFINISPAN_MAX_IDLE_ENTRY_CONF = "infinispan.cache.maxidle.entry";
   private static final String INFINISPAN_MAX_IDLE_ENTRY_DOC = "If infinispan.use.maxidle is true, this option has to the max idle associated with the entries to be stored (in seconds)";
   
   public static final String INFINISPAN_HOTROD_PROTOCOL_VERSION_CONF = "infinispan.hotrod.protocol.version";
   private static final String INFINISPAN_HOTROD_PROTOCOL_VERSION_DOC = "The infinispan hotrod client protocol version to use";

   public InfinispanSinkConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
      super(config, parsedConfig);
   }

   public InfinispanSinkConnectorConfig(Map<String, String> parsedConfig) {
      this(conf(), parsedConfig);
   }

   public static ConfigDef conf() {
      return new ConfigDef()
            .define(INFINISPAN_CONNECTION_HOSTS_CONF, Type.STRING, INFINISPAN_CONNECTION_HOSTS_DEFAULT, Importance.HIGH,
                  INFINISPAN_CONNECTION_HOSTS_DOC)
            .define(INFINISPAN_CONNECTION_HOTROD_PORT_CONF, Type.INT, INFINISPAN_CONNECTION_HOTROD_PORT_DEFAULT,
                  Importance.HIGH, INFINISPAN_CONNECTION_HOTROD_PORT_DOC)
            .define(INFINISPAN_CONNECTION_CACHE_NAME_CONF, Type.STRING, INFINISPAN_CONNECTION_CACHE_NAME_DEFAULT,
                  Importance.MEDIUM, INFINISPAN_CONNECTION_CACHE_NAME_DOC)
            .define(INFINISPAN_USE_PROTO_CONF, Type.BOOLEAN, INFINISPAN_USE_PROTO_DEFAULT, Importance.MEDIUM,
                  INFINISPAN_USE_PROTO_DOC)
            .define(INFINISPAN_PROTO_MARSHALLER_CLASS_CONF, Type.CLASS, INFINISPAN_PROTO_MARSHALLER_CLASS_DEFAULT,
                  Importance.MEDIUM, INFINISPAN_PROTO_MARSHALLER_CLASS_DOC)
            .define(INFINISPAN_CACHE_FORCE_RETURN_VALUES_CONF, Type.BOOLEAN,
                  INFINISPAN_CACHE_FORCE_RETURN_VALUES_DEFAULT, Importance.LOW,
                  INFINISPAN_CACHE_FORCE_RETURN_VALUES_DOC)
            .define(INFINISPAN_USE_LIFESPAN_CONF, Type.BOOLEAN, INFINISPAN_USE_LIFESPAN_DEFAULT,
                    Importance.LOW, INFINISPAN_USE_LIFESPAN_DOC)
            .define(INFINISPAN_USE_MAX_IDLE_CONF, Type.BOOLEAN, INFINISPAN_USE_MAX_IDLE_DEFAULT,
                    Importance.LOW, INFINISPAN_USE_MAX_IDLE_DOC)
            .define(INFINISPAN_LIFESPAN_ENTRY_CONF, Type.LONG, INFINISPAN_LIFESPAN_ENTRY_DEFAULT,
                    Importance.LOW, INFINISPAN_LIFESPAN_ENTRY_DOC)
            .define(INFINISPAN_MAX_IDLE_ENTRY_CONF, Type.LONG, INFINISPAN_MAX_IDLE_ENTRY_DEFAULT,
                    Importance.LOW, INFINISPAN_MAX_IDLE_ENTRY_DOC)
            .define(INFINISPAN_HOTROD_PROTOCOL_VERSION_CONF, Type.STRING, INFINISPAN_HOTROD_PROTOCOL_VERSION_DEFAULT,
                    Importance.LOW, INFINISPAN_HOTROD_PROTOCOL_VERSION_DOC);
   }
}
