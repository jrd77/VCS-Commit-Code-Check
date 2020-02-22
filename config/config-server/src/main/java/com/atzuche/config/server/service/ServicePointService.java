package com.atzuche.config.server.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.ServicePointEntity;
import com.atzuche.config.server.mapper.master.CityMapper;
import com.atzuche.config.server.mapper.master.ServicePointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 10:08 上午
 **/
@Service
public class ServicePointService implements ConfigService{
    @Autowired
    private ServicePointMapper mapper;

    private final static String CONFIG_NAME="service_point";

    @Override
    public ConfigItemDTO getConfig() {
        List<ServicePointEntity> list = mapper.findAll();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(ServicePointEntity.class);
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
