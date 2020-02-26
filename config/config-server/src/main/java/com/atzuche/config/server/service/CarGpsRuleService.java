package com.atzuche.config.server.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.server.mapper.master.CarGpsRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 2:51 下午
 **/
@Service
public class CarGpsRuleService implements ConfigService {
    @Autowired
    private CarGpsRuleMapper mapper;

    private final static String CONFIG_NAME="car_gps_rule";

    @Override
    public ConfigItemDTO getConfig() {
        List<CarGpsRuleEntity> list = mapper.findAll();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(CarGpsRuleEntity.class);
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
