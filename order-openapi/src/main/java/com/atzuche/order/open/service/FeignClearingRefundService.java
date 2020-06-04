package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.ClearingRefundReqVO;
import com.autoyol.autopay.gateway.vo.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name="order-center-api")
public interface FeignClearingRefundService {

    /*
     * @Author ZhangBin
     * @Date 2020/6/4 12:01
     * @Description: 清算退款
     *
     **/
    @PostMapping("/clearingRefundSubmitToQuery")
    public Response<?> clearingRefundSubmitToRefund(@RequestBody ClearingRefundReqVO clearingRefundReqVO);
}
