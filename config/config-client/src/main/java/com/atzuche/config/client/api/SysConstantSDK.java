package com.atzuche.config.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.api.ConfigNotFoundException;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.config.common.entity.SysContantEntity;
import com.google.inject.internal.cglib.core.$Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/1 3:29 下午
 **/
@Service
public class SysConstantSDK {
    private final static Logger logger = LoggerFactory.getLogger(SysConstantSDK.class);
    
    @Autowired
    private ConfigSDKFactory sdkFactory;

    private final static String CONFIG_NAME="sys_constant";

    public List<SysContantEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);
        List<SysConfigEntity> sysConfigEntities = new ArrayList<>();

        return JSON.parseObject(itemDTO.getConfigValue(),new TypeReference<List<SysContantEntity>>(){});
    }

    public SysContantEntity getConfigByCode(ConfigContext configContext,String code){
        if(code==null) {
            throw new IllegalArgumentException("code cannot be null");
        }
        List<SysContantEntity> list = getConfig(configContext);
        for(SysContantEntity constant:list){
            if(code.equalsIgnoreCase(constant.getCode())){
                return constant;
            }
        }
        logger.error("config about code={} not found",code);
        throw new ConfigNotFoundException("code="+code+" not found");
    }
}
