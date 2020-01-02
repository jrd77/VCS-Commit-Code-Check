package com.atzuche.order.coreapi.controller;


import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.client.api.SysConstantSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysContantEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.config.oilpriceconfig.OilAverageCostCacheConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @Autowired
    private SysConstantSDK sysConstantSDK;
    @Autowired
    private CityConfigSDK cityConfigSDK;
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

    @GetMapping("/aa")
    public Object aa(){
        List<SysContantEntity> config = sysConstantSDK.getConfig(new DefaultConfigContext());
        return config;
    }
    @GetMapping("city")
    public Object city(){
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(), 310100);
        List<CityEntity> config = cityConfigSDK.getConfig(new DefaultConfigContext());
        logger.info("config={}",JSON.toJSONString(config));
        return configByCityCode;
    }
}
