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
@MapperScan({"com.atzuche.violation.mapper","com.atzuche.order.renterwz.mapper","com.atzuche.order.accountrenterwzdepost.mapper",
        "com.atzuche.order.rentercommodity.mapper","com.atzuche.order.rentermem.mapper","com.atzuche.order.cashieraccount.mapper",
        "com.atzuche.order.accountrenterdeposit.mapper","com.atzuche.order.renterorder.mapper"})
@ComponentScan({"com.atzuche.violation",",com.atzuche.order.renterwz","com.atzuche.order.accountrenterwzdepost","com.atzuche.order.rentercommodity",
        "com.atzuche.order.rentermem","com.atzuche.order.cashieraccount","com.atzuche.order.accountrenterdeposit","com.atzuche.order.renterorder"})

public class TemplateApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TemplateApplication.class);
    }
}
