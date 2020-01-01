package com.atzuche.order.config;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/26 2:25 下午
 **/
public interface ConfigSDK {

    <T> T getConfig(ConfigContext context,String configName,T t);

}
