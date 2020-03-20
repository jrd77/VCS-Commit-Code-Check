package com.atzuche.violation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019-08-01 14:03
 **/

@SpringBootApplication
@MapperScan({"com.atzuche.violation.mapper","com.atzuche.order.renterwz.mapper","com.atzuche.order.accountrenterwzdepost.mapper",
        "com.atzuche.order.rentercommodity.mapper","com.atzuche.order.rentermem.mapper","com.atzuche.order.cashieraccount.mapper",
        "com.atzuche.order.accountrenterdeposit.mapper","com.atzuche.order.renterorder.mapper","com.atzuche.order.rentercost.mapper",
        "com.atzuche.order.coin.mapper","com.atzuche.order.accountrenterrentcost.mapper","com.atzuche.order.accountownerincome.mapper",
        "com.atzuche.order.accountownercost.mapper","com.atzuche.order.parentorder.mapper","com.atzuche.order.detain.mapper",
        "com.atzuche.order.settle.mapper","com.atzuche.order.flow.mapper","com.atzuche.order.accountrenterdetain.mapper",
        "com.atzuche.order.wallet","com.atzuche.order.accountplatorm.mapper","com.atzuche.order.accountrenterclaim.mapper",
        "com.atzuche.order.ownercost","com.atzuche.order.delivery.mapper","com.atzuche.order.transport","com.atzuche.order.owner.commodity"})
@ComponentScan({"com.atzuche.violation","com.atzuche.order.renterwz","com.atzuche.order.accountrenterwzdepost","com.atzuche.order.rentercommodity",
        "com.atzuche.order.rentermem","com.atzuche.order.cashieraccount","com.atzuche.order.accountrenterdeposit","com.atzuche.order.renterorder",
        "com.atzuche.order.rentercost","com.atzuche.config.client.api","com.atzuche.config.common.api","com.atzuche.order.coin","com.atzuche.order.accountrenterrentcost",
        "com.atzuche.order.mq","com.atzuche.order.accountownerincome","com.atzuche.order.accountownercost","com.atzuche.order.parentorder",
        "com.atzuche.order.detain","com.atzuche.order.settle","com.atzuche.order.flow","com.atzuche.order.accountrenterdetain",
        "com.atzuche.order.wallet","com.autoyol.autopay.gateway","com.atzuche.order.accountplatorm","com.atzuche.order.accountrenterclaim",
        "com.atzuche.order.ownercost","com.atzuche.order.delivery.service","com.atzuche.order.transport","com.atzuche.order.owner.commodity",
        "com.atzuche.order.delivery"})
@EnableFeignClients({"com.atzuche.config","com.autoyol.feeservice","com.autoyol.car.api","com.autoyol.auto.coin","com.autoyol.feign","com.atzuche.order",
                    "com.autoyol.autopay"})

public class TemplateApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TemplateApplication.class);
    }
}
