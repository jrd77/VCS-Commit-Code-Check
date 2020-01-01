package com.atzuche.config.client.api;

import com.atzuche.config.common.api.ConfigContext;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/1 3:25 下午
 **/
public class DefaultConfigContext implements ConfigContext {
    @Override
    public boolean preConfig() {
        return false;
    }
}
