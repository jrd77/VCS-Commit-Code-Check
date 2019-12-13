package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.dto.SubmitReqDto;
import com.autoyol.commons.web.ResponseData;

public interface SubmitOrderService {

    ResponseData submitOrder(SubmitReqDto submitReqDto);
}
