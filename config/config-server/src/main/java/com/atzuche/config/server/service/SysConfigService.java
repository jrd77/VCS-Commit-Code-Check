package com.atzuche.config.server.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.config.server.mapper.master.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/31 2:36 下午
 **/
@Service
public class SysConfigService implements ConfigService {
    @Autowired
    private SysConfigMapper mapper;

    private final static String CONFIG_NAME="sys_config";

    @Override
    public ConfigItemDTO getConfig() {
        List<SysConfigEntity> list = mapper.findAll();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(SysConfigEntity.class);
        configItemDTO.setConfigName(CONFIG_NAME);
        configItemDTO.setConfigType(ConfigConstants.TABLE_TYPE);
        configItemDTO.setConfigValue(JSON.toJSONString(list));
        return configItemDTO;
    }

    @Override
    public String registerName() {
        return CONFIG_NAME;
    }
}
