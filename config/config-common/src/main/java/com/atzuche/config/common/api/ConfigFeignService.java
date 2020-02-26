package com.atzuche.config.common.api;

import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/31 5:20 下午
 **/
@FeignClient(name="config-service")
public interface ConfigFeignService {

    @GetMapping("/config/get")
    ResponseData<ConfigItemDTO> getConfig(@RequestParam("configName") String configName,
                                          @RequestParam(value = "pre", required = false)boolean preConfig);

    @GetMapping("/config/getAllNames")
    ResponseData<List<String>> getAllConfigNames();
}
