package com.atzuche.order.sms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * @author 胡春林
 * 主函数入口
 */
@EnableHystrix
@EnableFeignClients
@EnableEurekaClient
@MapperScan("com.atzuche.order.sms")
@SpringBootApplication(scanBasePackages = {"com.atzuche.order"})
public class ShortMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortMessageApplication.class, args);
    }
}
