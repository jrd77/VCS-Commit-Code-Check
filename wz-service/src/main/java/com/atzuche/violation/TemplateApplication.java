package com.atzuche.violation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019-08-01 14:03
 **/

@SpringBootApplication
@MapperScan({"com.atzuche.violation.mapper"})
@ComponentScan("com.atzuche.violation")
public class TemplateApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TemplateApplication.class);
    }
}
