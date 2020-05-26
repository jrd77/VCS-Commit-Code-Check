package com.atzuche.config.server.config;

import com.atzuche.config.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/30 11:50 上午
 **/
@Configuration
public class ConfigServiceConfiguration {
    @Bean
    public ServicePointService registerServicePoint(@Autowired ConfigRegisterFactory factory, ServicePointService service){
        factory.register(service);
        return service;
    }

    @Bean
    public OilAverageCostConfigService registerOilAverageCost(@Autowired  ConfigRegisterFactory factory, OilAverageCostConfigService configService){
        factory.register(configService);
        return configService;
    }

    @Bean
    public InsuranceConfigService registerInsuranceConfig(@Autowired ConfigRegisterFactory factory,InsuranceConfigService service){
        factory.register(service);
        return service;
    }

    @Bean
    public CarGpsRuleService registerCarGpsRuleConfig(@Autowired ConfigRegisterFactory factory,CarGpsRuleService service){
        factory.register(service);
        return service;
    }

    @Bean
    public CarParamHotBrandDepositService registerCarparamHotBrandDeposit(@Autowired ConfigRegisterFactory factory,
                                                                          CarParamHotBrandDepositService service){
        factory.register(service);
        return service;
    }

    @Bean
    public DepositConfigService registerDepositConfig(@Autowired ConfigRegisterFactory factory,
                                                      DepositConfigService service){
        factory.register(service);
        return service;
    }

    @Bean
    public HolidaySettingService registerHolidaySetting(@Autowired ConfigRegisterFactory factory,HolidaySettingService service){
        factory.register(service);
        return service;
    }

    @Bean
    public SysConfigService registerSysConfigService(@Autowired ConfigRegisterFactory factory,SysConfigService service){
        factory.register(service);
        return service;
    }

    @Bean
    public SysConstantService registerSysConstantService(@Autowired ConfigRegisterFactory factory,
                                                         SysConstantService service){
        factory.register(service);
        return service;
    }

    @Bean
    public IllegalDepositConfigService registerIllegalDepositService(@Autowired ConfigRegisterFactory factory,
                                                                     IllegalDepositConfigService service){
        factory.register(service);
        return service;
    }

    @Bean
    public CityService registerCityService(@Autowired ConfigRegisterFactory factory,
                                           CityService service){
        factory.register(service);
        return service;
    }
    
    
    @Bean
    public CarChargeLevelConfigService registerCarChargeLevelConfigService(@Autowired ConfigRegisterFactory factory,CarChargeLevelConfigService service){
        factory.register(service);
        return service;
    }
}
