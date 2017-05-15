package com.github.oscerd.kafka.connect.infinispan;

import org.junit.Test;

import com.github.oscerd.kafka.connect.infinispan.InfinispanSinkConnectorConfig;

public class InfinispanConnectorConfigTest {
	@Test
	public void doc() {
		System.out.println(InfinispanSinkConnectorConfig.conf().toRst());
	}
}
