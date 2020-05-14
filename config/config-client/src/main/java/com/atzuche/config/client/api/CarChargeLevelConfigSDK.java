package com.atzuche.config.client.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CarChargeLevelConfigEntity;
import com.atzuche.config.common.entity.SysConfigEntity;

@Service
public class CarChargeLevelConfigSDK {
	@Autowired
    private ConfigSDKFactory sdkFactory;

    private final static String CONFIG_NAME="car_charge_level";

    public List<CarChargeLevelConfigEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);
        List<SysConfigEntity> sysConfigEntities = new ArrayList<>();

        return JSON.parseObject(itemDTO.getConfigValue(),new TypeReference<List<CarChargeLevelConfigEntity>>(){});
    }
}
