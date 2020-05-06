package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="order-center-api")
public interface FeignAdditionDriverService {

    /*
     * @Author ZhangBin
     * @Date 2020/4/29 18:07
     * @Description: 增加附加驾驶人险
     *
     **/
    @PostMapping("/additionalDriver/insertAdditionalDriver")
    ResponseData<?> insertAdditionalDriver(@RequestBody AdditionalDriverInsuranceIdsReqVO renterCostReqVO);
    /*
     * @Author ZhangBin
     * @Date 2020/4/29 18:01
     * @Description: 查询添加的附加驾驶人
     *
     **/
    @PostMapping("/additionalDriver/queryAdditionalDriver")
    ResponseData<List<String>> queryAdditionalDriver(@RequestParam("renterOrderNo") String renterOrderNo);
}
