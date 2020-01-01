package com.atzuche.config.server.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.common.api.ConfigConstants;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.OilAverageCostEntity;
import com.atzuche.config.server.mapper.master.OilAverageCostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 10:00 上午
 **/
@Service
public class OilAverageCostConfigService implements ConfigService {

    @Autowired

    private OilAverageCostMapper mapper;

    private final static String CONFIG_NAME="oil_average_cost";

//    private Gson gson = new Gson();

    @Override
    public ConfigItemDTO getConfig() {
        List<OilAverageCostEntity>  list = mapper.findAll();
        ConfigItemDTO configItemDTO = new ConfigItemDTO();
        configItemDTO.setConfigClass(OilAverageCostEntity.class);
        configItemDTO.setConfigName(CONFIG_NAME);
        configItemDTO.setConfigType(ConfigConstants.TABLE_TYPE);
        configItemDTO.setConfigValue(JSON.toJSONString(list));
        return configItemDTO;
    }

    @Override
    public String registerName(){
        return CONFIG_NAME;
    }
}
