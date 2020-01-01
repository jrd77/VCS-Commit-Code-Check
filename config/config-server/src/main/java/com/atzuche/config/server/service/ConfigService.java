package com.atzuche.config.server.service;

import com.atzuche.config.common.api.ConfigItemDTO;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 9:58 上午
 **/
public interface ConfigService {

    public ConfigItemDTO getConfig();

    public String registerName();
}
