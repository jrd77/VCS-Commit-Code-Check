package com.atzuche.order.delivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryRenYunConfig {

    @Value("${auto.add_flow_order}")
    public String ADD_FLOW_ORDER;

    @Value("${auto.change_flow_order}")
    public String CHANGE_FLOW_ORDER;

    @Value("${auto.cancel_flow_order}")
    public String CANCEL_FLOW_ORDER;
}
