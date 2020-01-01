package com.atzuche.config.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.api.ConfigNotFoundException;
import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.common.entity.CarParamHotBrandDepositEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/1 3:32 下午
 **/
@Service
public class CarParamHotBrandDepositSDK {
    @Autowired
    private ConfigSDKFactory sdkFactory;
    
    private final static Logger logger = LoggerFactory.getLogger(CarParamHotBrandDepositSDK.class);
    

    private final static String CONFIG_NAME="car_param_hot_brand_deposit";

    public List<CarParamHotBrandDepositEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);
        List<SysConfigEntity> sysConfigEntities = new ArrayList<>();

        return JSON.parseObject(itemDTO.getConfigValue(),new TypeReference<List<CarParamHotBrandDepositEntity>>(){});
    }

    public CarParamHotBrandDepositEntity getConfigByBrandIdAndTypeId(ConfigContext context,Integer brandId,Integer typeId){
        if(brandId==null){
            throw new IllegalArgumentException("brandId cannot be null");
        }
        if(typeId==null){
            throw new IllegalArgumentException("typeId cannot be null");
        }
        List<CarParamHotBrandDepositEntity> list = getConfig(context);
        for(CarParamHotBrandDepositEntity entity:list){
            if(brandId.equals(entity.getBrandId())&&typeId.equals(entity.getTypeId())){
                return entity;
            }
        }
        logger.error("not found brandId={} and TypeId={} config",brandId,typeId);
        throw new ConfigNotFoundException("brandId="+brandId+",typeId="+typeId+" config not found");
    }
}
