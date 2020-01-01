package com.atzuche.config.server.controller;

import com.atzuche.config.common.api.ConfigItemDTO;
import com.atzuche.config.server.service.ConfigRegisterFactory;
import com.autoyol.commons.web.ResponseData;
import com.ctrip.framework.apollo.spi.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/27 4:37 下午
 **/
@RestController
public class ConfigController {


    @Autowired
    private ConfigRegisterFactory configFactory;

     @GetMapping("/config/get")
     public ResponseData<ConfigItemDTO> getConfigItem(@RequestParam("configName") String configName,
                                                      @RequestParam(value = "pre", required = false)boolean preConfig,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response)throws IOException{

         ConfigItemDTO configItemDTO = configFactory.getConfig(configName);
         return ResponseData.success(configItemDTO);
     }

     @GetMapping("/config/getAllNames")
     public ResponseData<List<String>> getAllConfigNames(HttpServletRequest request,
                                                         HttpServletResponse response)throws IOException {
         List<String> configNames = configFactory.getAllConfigNames();
         return ResponseData.success(configNames);
     }
}
