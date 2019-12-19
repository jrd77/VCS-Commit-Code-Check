package com.atzuche.order.coreapi.controller;

import com.atzuche.order.config.oilpriceconfig.OilAverageCostCacheConfigService;
import com.atzuche.order.cashieraccount.service.notservice.CashierBindCardNoTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 5:03 下午
 **/
@RestController
public class TestController {

    @Autowired

    OilAverageCostCacheConfigService oilAverageCostCacheConfig;

    @Autowired
    CashierBindCardNoTService cashierBindCardService;
    @GetMapping(path = "/test")
    public String test(){

        String result = cashierBindCardService.test();
        System.out.println(result);
//        System.out.println(oilAverageCostCacheConfig.findOilPriceByEngineTypeAndCity(0,310100));
        return result;
    }
}
