package com.atzuche.config.server.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.server.mapper.master.CarGpsRuleMapper;
import com.atzuche.config.server.mapper.master.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 10:08 上午
 **/
@Service
public class CityService implements ConfigService{
    @Autowired
    private CityMapper mapper;

    private final static String CONFIG_NAME="city";

    @Override
    public ConfigItemDTO getConfig() {
        List<CityEntity> list = mapper.findAll();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(CityEntity.class);
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
