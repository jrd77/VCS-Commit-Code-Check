package com.atzuche.config.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.common.entity.IllegalDepositConfigEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/1 3:35 下午
 **/
public class IllegalDepositConfigSDK {
    @Autowired
    private ConfigSDKFactory sdkFactory;

    private final static String CONFIG_NAME="illegal_deposit_config";

    public List<IllegalDepositConfigEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);
        List<SysConfigEntity> sysConfigEntities = new ArrayList<>();

        return JSON.parseObject(itemDTO.getConfigValue(),new TypeReference<List<IllegalDepositConfigEntity>>(){});
    }
}
