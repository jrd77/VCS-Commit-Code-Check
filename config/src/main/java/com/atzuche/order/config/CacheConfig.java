package com.atzuche.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 4:30 下午
 **/
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(){
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();
        return cacheManager;
    }

    @Bean(name="configCache")
    public Cache cache(){
        MutableConfiguration<String, Object> config = new MutableConfiguration();
        config.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TWENTY_MINUTES));
        return cacheManager().createCache("configCache",config);
    }
}
