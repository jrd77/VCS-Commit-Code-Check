package com.atzuche.order.coreapi.config;

import com.atzuche.config.client.api.ConfigSDKFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/1 4:51 下午
 **/
@Service
public class ConfigInitCommandRunner implements CommandLineRunner {
    private final static Logger logger = LoggerFactory.getLogger(ConfigInitCommandRunner.class);
    
    @Autowired
    private ConfigSDKFactory configSDKFactory;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("init the configSDK");
        configSDKFactory.init();
    }
}
