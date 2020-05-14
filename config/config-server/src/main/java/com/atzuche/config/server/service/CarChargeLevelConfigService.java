package com.atzuche.config.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CarChargeLevelConfigEntity;
import com.atzuche.config.server.mapper.master.CarChargeLevelConfigMapper;

@Service
public class CarChargeLevelConfigService implements ConfigService{

	@Autowired
	private CarChargeLevelConfigMapper carChargeLevelConfigMapper;
	
	private final static String CONFIG_NAME="car_charge_level";
	
	@Override
    public ConfigItemDTO getConfig() {
		List<CarChargeLevelConfigEntity> list = carChargeLevelConfigMapper.listCarLevelConfigByLevel();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(CarChargeLevelConfigEntity.class);
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
