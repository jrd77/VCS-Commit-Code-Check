package com.atzuche.order.coreapi.controller;

<<<<<<< HEAD
import com.atzuche.order.config.oilpriceconfig.OilAverageCostCacheConfigService;
=======
import com.atzuche.order.config.oilprice.OilAverageCostCacheConfig;
import com.atzuche.order.service.CashierBindCardService;
>>>>>>> caea5322fc35a26d938961a5cbdf674de35816f8
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
<<<<<<< HEAD
    OilAverageCostCacheConfigService oilAverageCostCacheConfig;

=======
    OilAverageCostCacheConfig oilAverageCostCacheConfig;
    @Autowired
    CashierBindCardService cashierBindCardService;
>>>>>>> caea5322fc35a26d938961a5cbdf674de35816f8
    @GetMapping(path = "/test")
    public String test(){

        System.out.println(cashierBindCardService.test());
        System.out.println(oilAverageCostCacheConfig.findOilPriceByEngineTypeAndCity(0,310100));
        return "test";
    }
}
