package com.atzuche.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.cache.Cache;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 3:20 下午
 **/
public abstract class CacheConfigServiceTemplate<T> {
     
     private final static Logger logger = LoggerFactory.getLogger(CacheConfigServiceTemplate.class);
     
     @Autowired
     private Cache cache;

     /**
      * 加载数据
      * @param <T>
      * @return
      */
     protected abstract <T> T loadConfigData();

     /**
      * 该配置对应的键名
      * @return
      */
     protected abstract String key();

     /**
      * 获取需要缓存加载的数据
      * @param <T>
      * @return
      */
     public <T> T getConfig(){
         String keyName = key();
         Object cachedValue = cache.get(keyName);

         if(cachedValue!=null){
              return (T)cachedValue;
         }else{
              synchronized (this) {
                   Object tempValue = cache.get(keyName);
                   if(tempValue!=null){
                        return (T)tempValue;
                   }else {
                        T t = loadConfigData();
                        cache.put(keyName, t);
                        return t;
                   }
              }

         }
     }

    /**
     * 强制刷新配置
     */
    public void refresh(){
         T t = loadConfigData();
         String key = key();
         synchronized (this){
             cache.put(key,t);
         }
     }
}
