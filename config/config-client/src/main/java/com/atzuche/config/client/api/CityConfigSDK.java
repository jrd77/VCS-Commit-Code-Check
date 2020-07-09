package com.atzuche.config.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.api.ConfigFeignService;
import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.common.api.ConfigNotFoundException;
import com.atzuche.config.common.entity.CarGpsRuleEntity;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

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
    @Autowired
    private ConfigFeignService configFeignService;

    private final static String CONFIG_NAME="city";

    public List<CityEntity> getConfig(ConfigContext configContext){
        ConfigItemDTO itemDTO = sdkFactory.getConfig(configContext,CONFIG_NAME);

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
    
    
    /**
     * 获取城市信息根据编码
     * @param cityCode
     * @return CityEntity
     */
    public CityEntity getCityByCityCode(Integer cityCode){
        if(cityCode==null){
            throw new IllegalArgumentException("cityCode cannot be null");
        }
        CityEntity cityEntity = getCityFign(cityCode);
        if (cityEntity == null) {
        	logger.error("configNot found for city={},",cityCode);
            throw new ConfigNotFoundException("ConfigNotFound:cityCode="+cityCode);
        }
        return cityEntity;
    }
    
    
    /**
     * 获取城市信息根据城市编号
     * @param cityCode
     * @return CityEntity
     */
    private CityEntity getCityFign(Integer cityCode){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "配置服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"ConfigFeignService.getCityFign");
            Cat.logEvent(CatConstants.FEIGN_PARAM,"cityCode="+cityCode);
            ResponseData<CityEntity> responseData = configFeignService.getCityByCode(cityCode);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                logger.error("Feign 配置服务",JSON.toJSONString(responseData));
                ConfigNotFoundException configNotFoundException = new ConfigNotFoundException();
                throw configNotFoundException;
            }
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();

        } catch (ConfigNotFoundException e){
            Cat.logError("Feign 获配置服务失败,cityCode="+cityCode,e);
            t.setStatus(e);
            throw e;
        } catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获配置服务失败,cityCode="+cityCode,e);
            logger.error("Feign 获配置服务失败,cityCode={}",cityCode,e);
            throw new ConfigNotFoundException();
        }finally {
            t.complete();
        }
    }
}
