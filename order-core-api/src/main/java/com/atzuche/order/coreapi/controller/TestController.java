package com.atzuche.order.coreapi.controller;

import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.client.api.SysConstantSDK;
import com.atzuche.config.common.api.ConfigFeignService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.config.oilpriceconfig.OilAverageCostCacheConfigService;
import com.atzuche.order.cashieraccount.service.notservice.CashierBindCardNoTService;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 5:03 下午
 **/
@RestController
public class TestController {
    private final static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private CityConfigSDK configSDK;

    @Autowired
    OilAverageCostCacheConfigService oilAverageCostCacheConfig;
    @Autowired
    CashierService cashierService;
    @GetMapping(path = "/test")
    public String test(){
        CashierDeductDebtReqVO vo = new CashierDeductDebtReqVO();
        cashierService.deductDebt(vo);
        System.out.println(oilAverageCostCacheConfig.findOilPriceByEngineTypeAndCity(0,310100));
        return "";
    }

    @GetMapping(path = "/test2/pp")
    public String xx()  {
        logger.info("xxxxxxxxxxxxx");
        logger.info("{}",configSDK.getConfigByCityCode(new DefaultConfigContext(),310100));
        return "xx";
    }
}
