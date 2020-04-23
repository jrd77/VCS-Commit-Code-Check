package com.atzuche.order.sms.config;

import com.autoyol.aliyunmq.AliyunMnsListenerContainerFactory;
import com.autoyol.aliyunmq.AliyunMnsService;
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

    @Value("${com.aliyun.mns.accessId}")
    private String accessId;
    @Value("${com.aliyun.mns.accessKey}")
    private String accessKey;
    @Value("${com.aliyun.mns.accountEndPoint}")
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
