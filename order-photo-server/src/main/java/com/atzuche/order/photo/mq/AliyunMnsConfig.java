package com.atzuche.order.photo.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * AliyunMnsConfig
 *
 * @author shisong
 * @date 2020/1/13
 */
@Configuration
public class AliyunMnsConfig {

    @Value("${mns.access.id}")
    private String accessId;
    @Value("${mns.access.key}")
    private String accessKey;
    @Value("${mns.access.endpoint}")
    private String accountEndPoint;


    @Bean
    public AliyunMnsService aliyunMnsService(){
        AliyunMnsService aliyunMnsService = new AliyunMnsService();
        aliyunMnsService.setAccessId(accessId);
        aliyunMnsService.setAccessKey(accessKey);
        aliyunMnsService.setAccountEndPoint(accountEndPoint);
        aliyunMnsService.init();
        return aliyunMnsService;
    }

    @Bean
    public AliyunMnsListenerContainerFactory aliyunMnsListenerContainerFactory(AliyunMnsService aliyunMnsService){
        AliyunMnsListenerContainerFactory aliyunMnsListenerContainerFactory =new AliyunMnsListenerContainerFactory();
        aliyunMnsListenerContainerFactory.setAliyunMnsService(aliyunMnsService);
        aliyunMnsListenerContainerFactory.setTaskExecutor((Executors.newFixedThreadPool(10)));
        return aliyunMnsListenerContainerFactory;
    }
}
