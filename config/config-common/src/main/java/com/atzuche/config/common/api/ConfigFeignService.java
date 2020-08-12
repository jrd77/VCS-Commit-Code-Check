package com.atzuche.config.common.api;

import com.atzuche.config.common.entity.CityEntity;
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
//@FeignClient(url = "http://localhost:1388" ,name="config-service")  //本地测试
public interface ConfigFeignService {

    @GetMapping("/config/get")
    ResponseData<ConfigItemDTO> getConfig(@RequestParam("configName") String configName,
                                          @RequestParam(value = "pre", required = false)boolean preConfig);

    @GetMapping("/config/getAllNames")
    ResponseData<List<String>> getAllConfigNames();
    
    @GetMapping("/config/city/getbycode")
    public ResponseData<CityEntity> getCityByCode(@RequestParam("cityCode") Integer cityCode);
}
