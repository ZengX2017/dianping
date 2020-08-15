package com.example.dianping.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

/**
 * @author Adward_Z
 * @date 2020/8/15
 */
@Configuration
public class ElasticsearchRestClient {

    @Value("${elasticsearch.ip}")
    private String ipAddress;

    @Bean(name="highLevelClient")
    public RestHighLevelClient highLevelClient(){
        String[] address = ipAddress.split(":");
        String ip = address[0];
        int port = Integer.valueOf(address[1]);
        HttpHost httpHost = new HttpHost(ip, port, "http");

        return new RestHighLevelClient(RestClient.builder(new HttpHost[]{httpHost}));
    }

}
