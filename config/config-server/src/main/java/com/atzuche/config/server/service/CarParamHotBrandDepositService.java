package com.atzuche.config.server.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CarParamHotBrandDepositEntity;
import com.atzuche.config.server.mapper.master.CarParamHotBrandDepositMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/31 2:17 下午
 **/
@Service
public class CarParamHotBrandDepositService implements ConfigService {
    @Autowired
    private CarParamHotBrandDepositMapper mapper;

    private final static String CONFIG_NAME="car_param_hot_brand_deposit";

    @Override
    public ConfigItemDTO getConfig() {
        List<CarParamHotBrandDepositEntity> list = mapper.findAll();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(CarParamHotBrandDepositEntity.class);
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
