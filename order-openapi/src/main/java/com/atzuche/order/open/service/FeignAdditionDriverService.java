package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name="order-center-api")
public interface FeignAdditionDriverService {

    /*
     * @Author ZhangBin
     * @Date 2020/4/29 18:07
     * @Description: 增加附加驾驶人险
     *
     **/
    @PostMapping("/additionalDriver/insertAdditionalDriver")
    public ResponseData<?> insertAdditionalDriver(@RequestBody AdditionalDriverInsuranceIdsReqVO renterCostReqVO);
}
