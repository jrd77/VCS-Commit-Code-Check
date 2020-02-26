package com.atzuche.config.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 该服务是配置服务的启动主程序
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 4:12 下午
 **/
@SpringBootApplication(scanBasePackages = {"com.atzuche.config.server"})
public class ConfigServerSpringBoot extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerSpringBoot.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ConfigServerSpringBoot.class);
    }
}
