package com.atzuche.config.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.api.ConfigNotFoundException;
import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 10:13 上午
 **/
@Service
public class CityConfigSDK {
    private final static Logger logger = LoggerFactory.getLogger(CityConfigSDK.class);
    
    @Autowired
    private ConfigSDKFactory sdkFactory;

    private final static String CONFIG_NAME="city";

    public List<CityEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);
        List<SysConfigEntity> sysConfigEntities = new ArrayList<>();

        return JSON.parseObject(itemDTO.getConfigValue(),new TypeReference<List<CityEntity>>(){});
    }

    public CityEntity getConfigByCityCode(ConfigContext context,Integer cityCode){
        if(cityCode==null){
            throw new IllegalArgumentException("cityCode cannot be null");
        }
        List<CityEntity> cityEntities = getConfig(context);
        for(CityEntity cityEntity:cityEntities){
            if(cityCode.equals(cityEntity.getCode())){
                return cityEntity;
            }
        }
        logger.error("configNot found for city={},",cityCode);
        throw new ConfigNotFoundException("ConfigNotFound:cityCode="+cityCode);
    }
}
