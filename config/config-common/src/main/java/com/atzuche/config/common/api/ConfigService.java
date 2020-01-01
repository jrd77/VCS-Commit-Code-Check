package com.atzuche.config.common.api;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/26 4:37 下午
 **/
public interface ConfigService {
    /**
     * 获取指定的配置项
     * @param context
     * @param configName
     * @return
     */
    ConfigItemDTO getConfig(ConfigContext context,String configName);

}
