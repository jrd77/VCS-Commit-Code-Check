package com.atzuche.config.server.service;

import com.atzuche.config.common.api.ConfigItemDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 5:48 下午
 **/
@Service
public class ConfigRegisterFactory {

     private Map<String,ConfigService> configServiceMap = new ConcurrentHashMap<>();

     public void register(ConfigService configService){
         if(configServiceMap.get(configService.registerName())!=null){
             throw new RuntimeException("config already exists:"+configService.registerName());
         }
         configServiceMap.putIfAbsent(configService.registerName(),configService);
     }

     public ConfigItemDTO getConfig(String configName){
         ConfigService configService = configServiceMap.get(configName);
         if(configService!=null){
             return configService.getConfig();
         }else{
             throw new RuntimeException("configNot Found:"+configName);
         }
     }

    public List<String> getAllConfigNames() {
        List<String> configNames = new ArrayList<>(configServiceMap.keySet());
        return configNames;
    }
}
