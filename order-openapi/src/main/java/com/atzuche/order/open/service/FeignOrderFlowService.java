package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.OrderFlowListResponseDTO;
import com.atzuche.order.commons.entity.dto.OrderFlowRequestDTO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
public interface FeignOrderFlowService {

    @PostMapping("/order/flow/list")
    ResponseData<OrderFlowListResponseDTO> selectOrderFlowList(@RequestBody OrderFlowRequestDTO orderFlowRequestDTO);
}
