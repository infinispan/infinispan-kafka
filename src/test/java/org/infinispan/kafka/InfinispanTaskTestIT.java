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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.sink.SinkRecord;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.kafka.InfinispanSinkTask;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InfinispanTaskTestIT {
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanTaskTestIT.class);
	private RemoteCacheManager cacheManager;
	
	@Before
	public void setUp() {
		ConfigurationBuilder builder = new ConfigurationBuilder().addServer().host("localhost").port(11222)
				.marshaller(new ProtoStreamMarshaller());

		cacheManager = new RemoteCacheManager(builder.build());

		SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
		ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
		String memoSchemaFile = null;
		try {
			memoSchemaFile = protoSchemaBuilder.fileName("file.proto").packageName("test").addClass(Author.class)
					.build(serCtx);
		} catch (ProtoSchemaBuilderException | IOException e) {
			e.printStackTrace();
		}

		// register the schemas with the server too
		RemoteCache<String, String> metadataCache = cacheManager
				.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
		metadataCache.put("file.proto", memoSchemaFile);
	}

	@After
	public void tearDown() {
		cacheManager.stop();
	}

	@Test
	public void testUseProto() throws JsonProcessingException {
		Map<String, String> props = new HashMap<>();
		props.put("infinispan.connection.hosts", "127.0.0.1");
		props.put("infinispan.connection.hotrod.port", "11222");
		props.put("infinispan.connection.cache.name", "default");
		props.put("infinispan.cache.force.return.values", "true");
		props.put("infinispan.cache.maxidle.default", "false");
		props.put("infinispan.cache.lifespan.default", "false");
		props.put("infinispan.use.proto", "true");
		props.put("infinispan.proto.marshaller.class", "org.infinispan.kafka.Author");

		InfinispanSinkTask infinispanSinkTask = new InfinispanSinkTask();
		infinispanSinkTask.start(props);

		final String topic = "atopic";

		Author author = new Author();
		author.setName("author");

		ObjectMapper mapper = new ObjectMapper();

		infinispanSinkTask.put(Collections
				.singleton(new SinkRecord(topic, 1, null, "author", null, mapper.writeValueAsString(author), 42)));

		RemoteCache<Object, Object> cache = cacheManager.getCache("default");
		    
		assertEquals(cache.get("author").toString(), author.toString());
	}
	
	@Test
	public void testNoProto() throws JsonProcessingException {
		Map<String, String> props = new HashMap<>();
		props.put("infinispan.connection.hosts", "127.0.0.1");
		props.put("infinispan.connection.hotrod.port", "11222");
		props.put("infinispan.connection.cache.name", "default");
		props.put("infinispan.cache.force.return.values", "true");
		props.put("infinispan.cache.maxidle.default", "false");
		props.put("infinispan.cache.lifespan.default", "false");
		props.put("infinispan.use.proto", "false");

		InfinispanSinkTask infinispanSinkTask = new InfinispanSinkTask();
		infinispanSinkTask.start(props);

		final String topic = "atopic";

		Author author = new Author();
		author.setName("author");

		ObjectMapper mapper = new ObjectMapper();

		infinispanSinkTask.put(Collections
				.singleton(new SinkRecord(topic, 1, null, "author", null, mapper.writeValueAsString(author), 42)));

		RemoteCache<Object, Object> cache = cacheManager.getCache("default");
		    
		assertEquals(cache.get("author").toString(), author.toString());
	}
}
