package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.RenterAndOwnerSeeOrderVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="order-center-api")
public interface FeignBusinessService {
    /**
     * 更新未读订单
     * @param renterAndOwnerSeeOrderVO
     * @return
     */
    @PostMapping("/orderBusiness/renterAndOwnerSeeOrder")
    public ResponseData<?> renterAndOwnerSeeOrder(@RequestBody RenterAndOwnerSeeOrderVO renterAndOwnerSeeOrderVO);
}
