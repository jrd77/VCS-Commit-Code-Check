package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.OrderFlowListResponseDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "http://10.0.3.235:1412",name="order-center-api")
public interface FeignOrderFlowService {

    @GetMapping("/order/flow/list")
    ResponseData<OrderFlowListResponseDTO> selectOrderFlowList(OrderFlowRequestDTO orderFlowRequestDTO);
}
