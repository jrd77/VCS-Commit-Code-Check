package com.atzuche.order.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 1:57 下午
 **/
@SpringBootApplication(scanBasePackages = {"com.atzuche.order"})
@MapperScan({"com.atzuche.order"})
@EnableFeignClients({"com.autoyol"})
public class AdminSpringBoot extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AdminSpringBoot.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminSpringBoot.class);
    }
}
