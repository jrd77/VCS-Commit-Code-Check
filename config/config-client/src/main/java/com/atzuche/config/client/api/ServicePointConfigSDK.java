package com.atzuche.config.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.ServicePointEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 10:13 上午
 **/
@Service
public class ServicePointConfigSDK {
    private final static Logger logger = LoggerFactory.getLogger(ServicePointConfigSDK.class);
    
    @Autowired
    private ConfigSDKFactory sdkFactory;

    private final static String CONFIG_NAME="service_point";

    public List<ServicePointEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);

        return JSON.parseObject(itemDTO.getConfigValue(),new TypeReference<List<ServicePointEntity>>(){});
    }

    public ServicePointEntity getConfig(ConfigContext context,String address, String lat, String lon){
        if(address==null || lat==null || lon==null){
            throw new IllegalArgumentException("address/lat/lon cannot be null");
        }
        List<ServicePointEntity> servicePointEntities = getConfig(context);
        for(ServicePointEntity servicePointEntity:servicePointEntities){
            if(address.equals(servicePointEntity.getAddressContent()) && lat.equals(servicePointEntity.getLat()) && lon.equals(servicePointEntity.getLon())){
                return servicePointEntity;
            }
        }
        logger.error("configNot found for address={},lat={},lon={}",address,lat,lon);
        //throw new ConfigNotFoundException("ConfigNotFound:address="+address+",lat="+lat+",lon="+lon);*/
        return null;
    }


}
