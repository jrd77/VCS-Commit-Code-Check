package com.atzuche.config.client.api;

import com.atzuche.config.common.api.*;
import com.autoyol.cat.CatConfig;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 2:48 下午
 **/
@Service
public class ConfigSDKFactory implements ConfigService {
    private final static Logger logger = LoggerFactory.getLogger(ConfigSDKFactory.class);


    @Autowired
    private ConfigFeignService configFeignService;

    private volatile  boolean inited = false;
    

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
        if(!inited){
            init();
        }
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


    private void init(){
        List<String> configNames = getAllConfigNames();

        for(String configName:configNames){
            ConfigItemDTO configItemDTO = getConfig(configName,false);
            proConfigValues.put(configName,configItemDTO);
            ConfigItemDTO preConfigItemDTO = getConfig(configName,true);
            preConfigValues.put(configName,configItemDTO);
        }
    }

    /**
     * 获取指定配置名称的配置
     * @param configNames
     * @param pre
     * @return
     */
    private ConfigItemDTO getConfig(String configNames,boolean pre){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "配置服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"ConfigFeignService.getConfig");
            Cat.logEvent(CatConstants.FEIGN_PARAM,"configNames="+configNames+",pre="+pre);
            ResponseData<ConfigItemDTO> responseData = configFeignService.getConfig(configNames, pre);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                logger.error("Feign 配置服务",JSON.toJSONString(responseData));
                ConfigNotFoundException configNotFoundException = new ConfigNotFoundException();
                throw configNotFoundException;
            }
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();

        }catch (ConfigNotFoundException e){
            Cat.logError("Feign 获配置服务失败,configName="+configNames+",pre="+pre,e);
            t.setStatus(e);
            throw e;
        }
        catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获配置服务失败,configName="+configNames+",pre="+pre,e);
            logger.error("Feign 获配置服务失败,configName={},pre={}",configNames,pre,e);
            throw new ConfigNotFoundException();
        }finally {
            t.complete();
        }
    }


    /**
     * 获取远程配置服务的所有的名称
     * @return
     */
    private List<String> getAllConfigNames(){
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "配置服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"ConfigFeignService.getAllConfigNames");
            Cat.logEvent(CatConstants.FEIGN_PARAM,"");
            ResponseData<List<String>> responseData = configFeignService.getAllConfigNames();
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                logger.error("Feign 配置服务",JSON.toJSONString(responseData));
                ConfigNotFoundException configNotFoundException = new ConfigNotFoundException();
                throw configNotFoundException;
            }
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();

        }catch (ConfigNotFoundException e){
            Cat.logError("Feign 获配置服务失败",e);
            t.setStatus(e);
            throw e;
        }
        catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 获配置服务失败",e);
            logger.error("Feign 获配置服务失败",e);
            throw new ConfigNotFoundException();
        }finally {
            t.complete();
        }
    }


}
