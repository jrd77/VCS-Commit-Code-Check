package com.atzuche.order.renterorder.config;

import com.autoyol.coupon.api.CouponServiceApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

/**
 * 优惠券服务Hessian配置
 *
 * @author pengcheng.fu
 * @date 2019/12/25 20:21
 */
@Configuration
public class CouponServiceApiConfig {

    @Value("${coupon.service.remote.host}")
    private String couponServiceUrl;

    @Bean
    public HessianProxyFactoryBean couponServiceClient() {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl(couponServiceUrl + "/couponService");
        factory.setServiceInterface(CouponServiceApi.class);
        return factory;
    }


}
