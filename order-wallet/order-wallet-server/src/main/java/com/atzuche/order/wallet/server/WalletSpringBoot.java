package com.atzuche.order.wallet.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 该服务是订单钱包服务的启动主程序
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 4:12 下午
 **/
@SpringBootApplication(scanBasePackages = {"com.atzuche.order.wallet.server"})
@MapperScan(basePackages = {"com.atzuche.order.wallet.server.mapper"})
public class WalletSpringBoot extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WalletSpringBoot.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WalletSpringBoot.class);
    }
}
