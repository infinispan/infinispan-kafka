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
package com.github.oscerd.kafka.connect.infinispan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.BaseMarshaller;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		// Create a configuration for a locally-running server
		log.info(AuthorMarshaller.class.getName());
		config = new InfinispanSinkConnectorConfig(map);
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.addServer()
		        .host(config.getString(InfinispanSinkConnectorConfig.INFINISPAN_CONNECTION_HOSTS_CONF))
				.port(config.getInt(InfinispanSinkConnectorConfig.INFINISPAN_CONNECTION_HOTROD_PORT_CONF));	
		boolean useProto = config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_USE_PROTO_CONF);
		if (useProto) {
            builder.marshaller(new ProtoStreamMarshaller());
		}
				
		// Connect to the server
		cacheManager = new RemoteCacheManager(builder.build());
		cache = cacheManager.getCache(config.getString(InfinispanSinkConnectorConfig.INFINISPAN_CONNECTION_CACHE_NAME_CONF));
		if (useProto) {
		    SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
		    String protobufSchemaFiles = config.getString(InfinispanSinkConnectorConfig.INFINISPAN_PROTO_FILES_CONF);
		    String[] list = protobufSchemaFiles.split(",");
		    FileDescriptorSource fds = new FileDescriptorSource();
		    try {
				fds.addProtoFiles(list);
			    serCtx.registerProtoFiles(fds);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    Class<?> marshallerClass = config.getClass(InfinispanSinkConnectorConfig.INFINISPAN_PROTO_MARSHALLERS_CONF);
		    try {
			    serCtx.registerMarshaller((BaseMarshaller) marshallerClass.newInstance());
			} catch (IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			} 
		}
	}

	@Override
	public void put(Collection<SinkRecord> collection) {
		if (collection.isEmpty()) {
			return;
		}
		final int recordsCount = collection.size();
		log.info("Received {} records", recordsCount);
		Iterator it = collection.iterator();
		while (it.hasNext()) {
			SinkRecord record = (SinkRecord) it.next();
			log.info("Record kafka coordinates:({}-{}-{}). Writing it to Infinispan...", record.topic(), record.key(), record.value());
			defineCacheFlags();
			Object returnValue = cache.put(record.key(), record.value());
			log.info("Cache : " + cache.get(record.key()));
			if (returnValue != null) {
			    log.info("The put operation returned the following result: {}", returnValue);
			}
		}
	}

	@Override
	public void flush(Map<TopicPartition, OffsetAndMetadata> map) {

	}

	@Override
	public void stop() {
		cacheManager.stop();
	}
	
	private void defineCacheFlags() {
		if (config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_CACHE_FORCE_RETURN_VALUES_CONF)) {
           cache = cache.withFlags(Flag.FORCE_RETURN_VALUE);
		}
		if (config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_CACHE_MAXIDLE_CONF)) {
	       cache = cache.withFlags(Flag.DEFAULT_MAXIDLE);
        }
		if (config.getBoolean(InfinispanSinkConnectorConfig.INFINISPAN_CACHE_LIFESPAN_CONF)) {
		   cache = cache.withFlags(Flag.DEFAULT_LIFESPAN);
	    }
	}

}
