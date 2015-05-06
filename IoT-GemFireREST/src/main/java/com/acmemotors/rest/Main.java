/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acmemotors.rest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

/**
 * Spring Boot bootstrap class to configure the Spring Data Repositories in
 * IoT-GemFireCommons to be exposed as Spring Data REST endpoints.
 *
 * @author Michael Minella
 */
@Configuration
@EnableGemfireRepositories(basePackages = "com.acmemotors.rest")
@SpringBootApplication
public class Main {

	@Bean
	PoolFactoryBean poolFactoryBean(@Value("${gf.server.port}") int serverPort,
			@Value("${gf.server.host}") String serverHost) throws Exception {
		PoolFactoryBean factoryBean = new PoolFactoryBean();
		factoryBean.setName("my-pool");
		factoryBean.setServers(
				Collections.singletonList(new InetSocketAddress(serverHost, serverPort)));
		factoryBean.afterPropertiesSet();
		return factoryBean;
	}

	@Bean
	ClientCache cache() {
		return new ClientCacheFactory().create();
	}

	@Bean
	@SuppressWarnings("rawtypes")
	Region journeyRegion(ClientCache cache) {
		return cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
				.create("journeys");
	}

	@Bean
	@SuppressWarnings("rawtypes")
	Region carPositionRegion(ClientCache cache) {
		return cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
				.create("car-position");
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(Main.class, args);
	}
}
