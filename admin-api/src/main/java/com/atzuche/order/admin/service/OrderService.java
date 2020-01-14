package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.req.order.CancelOrderVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.open.service.FeignOrderUpdateService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private FeignOrderUpdateService feignOrderUpdateService;

    public ResponseData cancelOrder(CancelOrderVO cancelOrderVO) {
        CancelOrderReqVO cancelOrderReqVO = new CancelOrderReqVO();

        feignOrderUpdateService.cancelOrder(cancelOrderReqVO);
        return null;
    }
}
