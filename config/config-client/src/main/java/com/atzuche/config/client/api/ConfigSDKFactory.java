package com.atzuche.config.client.api;

import com.atzuche.config.common.api.*;
import com.autoyol.commons.web.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 2:48 下午
 **/
@Service
public class ConfigSDKFactory implements ConfigService, InitializingBean {
    private final static Logger logger = LoggerFactory.getLogger(ConfigSDKFactory.class);


    @Autowired
    private ConfigFeignService configFeignService;
    

    /**
     * 存放正式环境所有的配置
     */
    private ConcurrentHashMap<String, ConfigItemDTO> proConfigValues = new ConcurrentHashMap<>();

    /**
     * 存放所有预发环境的配置
     */
    private ConcurrentHashMap<String,ConfigItemDTO> preConfigValues = new ConcurrentHashMap<>();

    @Override
    public ConfigItemDTO getConfig(ConfigContext context, String configName) {
        if(context.preConfig()){
             ConfigItemDTO itemDTO = preConfigValues.get(configName);
             if(itemDTO==null){
                 logger.error("没有找到configName={}的预发配置",configName);
                 throw new ConfigNotFoundException();
             }
             return itemDTO;
        }else{
            ConfigItemDTO itemDTO = proConfigValues.get(configName);
            if(itemDTO==null){
                logger.error("没有找到configName={}的生成配置",configName);
                throw new ConfigNotFoundException();
            }
            return itemDTO;
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        ResponseData<List<String>> configNames = configFeignService.getAllConfigNames();
        if(configNames!=null&&"000000".equalsIgnoreCase(configNames.getResCode())){
            logger.info("configNames is {}",configNames);
        }
    }
}
