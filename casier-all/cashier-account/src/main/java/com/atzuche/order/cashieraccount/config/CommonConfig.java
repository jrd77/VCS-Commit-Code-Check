package com.atzuche.order.cashieraccount.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengcheng.fu
 * @date 2020/7/8 15:20
 */

@Configuration
public class CommonConfig {

    @Value("${auto.secondOpen.url}")
    public String secondOpenUrl;

}
