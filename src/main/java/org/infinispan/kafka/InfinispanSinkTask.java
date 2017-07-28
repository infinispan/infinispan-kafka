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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InfinispanSinkTask extends SinkTask {
   private static Logger log = LoggerFactory.getLogger(InfinispanSinkTask.class);
   private RemoteCacheManager cacheManager;
   private RemoteCache<Object, Object> cache;
   private InfinispanSinkConnectorConfig config;

   @Override
   public String version() {
      return VersionUtil.getVersion();
   }

   @Override
   public void start(Map<String, String> map) {
      config = new InfinispanSinkConnectorConfig(map);
      setupRemoteCache();
   }

   @Override
   public void put(Collection<SinkRecord> collection) {
      if (collection.isEmpty()) {
         return;
      }
      boolean useProto = config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_USE_PROTO_CONF);
      final int recordsCount = collection.size();
      log.info("Received {} records", recordsCount);
      Iterator it = collection.iterator();
      while (it.hasNext()) {
         SinkRecord record = (SinkRecord) it.next();
         log.info("Record kafka coordinates:({}-{}-{}). Writing it to Infinispan...", record.topic(), record.key(),
               record.value());
         storeEntry(useProto, record);
      }
   }

   @Override
   public void flush(Map<TopicPartition, OffsetAndMetadata> map) {
   }

   @Override
   public void stop() {
      cacheManager.stop();
   }

   private void setupRemoteCache() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(config.getString(InfinispanSinkConnectorConfig.INFINISPAN_CONNECTION_HOSTS_CONF))
            .port(config.getInt(InfinispanSinkConnectorConfig.INFINISPAN_CONNECTION_HOTROD_PORT_CONF));
      boolean useProto = config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_USE_PROTO_CONF);
      if (useProto) {
         log.info("Adding protostream");
         builder.marshaller(new ProtoStreamMarshaller());
      }
      builder.forceReturnValues(
            config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_CACHE_FORCE_RETURN_VALUES_CONF));

      cacheManager = new RemoteCacheManager(builder.build());
      if (useProto) {
         SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
         ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
         Class<?> marshaller = config.getClass(InfinispanSinkConnectorConfig.INFINISPAN_PROTO_MARSHALLER_CLASS_CONF);
         String memoSchemaFile = null;
         try {
            memoSchemaFile = protoSchemaBuilder.fileName("file.proto").packageName("test").addClass(marshaller)
                  .build(serCtx);
         } catch (ProtoSchemaBuilderException | IOException e) {
            log.error("Error during building of Protostream Schema {}", e.getMessage());
            e.printStackTrace();
         }

         RemoteCache<String, String> metadataCache = cacheManager
               .getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
         metadataCache.put("file.proto", memoSchemaFile);
      }
      cache = cacheManager
            .getCache(config.getString(InfinispanSinkConnectorConfig.INFINISPAN_CONNECTION_CACHE_NAME_CONF));
   }
   
   private void storeEntry(boolean useProto, SinkRecord record) {
       ObjectMapper objectMapper = new ObjectMapper();
       Class<?> marshaller = config.getClass(InfinispanSinkConnectorConfig.INFINISPAN_PROTO_MARSHALLER_CLASS_CONF);
       Object p = null;
       if (useProto) {
           try {
              p = objectMapper.readValue((String) record.value(), marshaller);
           } catch (IOException e) {
              log.error("Error during Deserialization of value {}", e.getMessage());
              e.printStackTrace();
           }
           Object returnValue = cache.put(record.key(), p);
           if (returnValue != null) {
              log.info("The put operation returned the following result: {}", returnValue);
           }
        } else {
           Object returnValue = cache.put(record.key(), record.value());
           if (returnValue != null) {
              log.info("The put operation returned the following result: {}", returnValue);
           }
        }
   }

}
