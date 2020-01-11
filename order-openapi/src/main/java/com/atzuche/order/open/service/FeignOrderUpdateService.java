package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.RentCityAndRiskAccidentReqDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "order-center-api")
public interface FeignOrderUpdateService {
    /*
     * @Author ZhangBin
     * @Date 2020/1/8 21:06
     * @Description: 修改用车城市和风控事故状态
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/order/update/rentCityAndRiskAccident")
    ResponseData<?> updateRentCityAndRiskAccident(@RequestBody RentCityAndRiskAccidentReqDTO rentCityAndRiskAccidentReqDTO);
}
