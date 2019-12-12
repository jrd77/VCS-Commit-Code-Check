package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.dto.SubmitReqDto;

public interface SubmitOrderService {

    void submitOrder(SubmitReqDto submitReqDto);
}
