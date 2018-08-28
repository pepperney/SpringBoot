package com.pepper.web.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Auther: pei.nie
 * @Date:2018/8/26
 * @Description:
 */
@PropertySource("classpath:config/config.properties")
@Configuration
public class EsConfig {

    @Value("${spring.elasticsearch.clustername}")
    private String clustername;

    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name",clustername)
                .build();
        InetSocketTransportAddress node = new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300);
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        client.addTransportAddress(node);
        // 如果配置集群，此处增加node并add即可
        return client;
    }
}
