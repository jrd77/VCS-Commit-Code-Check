package com.atzuche.delivery.config;

import com.autoyol.aliyunmq.AliyunMnsAnnotationBeanPostProcessor;
import com.autoyol.aliyunmq.AliyunMnsListenerContainerFactory;
import com.autoyol.aliyunmq.AliyunMnsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * Created by andy on 16/6/16.
 */
@ConfigurationProperties(prefix = "com.aliyun.mns")
@Configuration
public class AliyunMnsServiceConfig {
    private final static Logger logger = LoggerFactory.getLogger(AliyunMnsServiceConfig.class);
    
    private String accessKey;
    private String accessId;
    private String accountEndPoint;

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public void setAccountEndPoint(String accountEndPoint) {
        this.accountEndPoint = accountEndPoint;
    }

    @Bean
    public AliyunMnsService mnsClient(){
        logger.info("accessKey is {}",accessKey);
        AliyunMnsService aliyunMnsService = new AliyunMnsService();

        aliyunMnsService.setAccessId(accessId);
        aliyunMnsService.setAccessKey(accessKey);
        aliyunMnsService.setAccountEndPoint(accountEndPoint);

        aliyunMnsService.init();

        return aliyunMnsService;
    }

    @Bean
    public AliyunMnsAnnotationBeanPostProcessor aliyunMnsAnnotationBeanPostProcessor(){
        return new AliyunMnsAnnotationBeanPostProcessor();
    }

    @Bean
    @Autowired
    public AliyunMnsListenerContainerFactory aliyunMnsListenerContainerFactory(AliyunMnsService service){
        AliyunMnsListenerContainerFactory factory = new AliyunMnsListenerContainerFactory();
        factory.setTaskExecutor(Executors.newFixedThreadPool(10));
        factory.setAliyunMnsService(service);
        return factory;
    }
}
