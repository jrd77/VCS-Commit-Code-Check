package com.atzuche.order.admin.controller;

import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("settle")
@Slf4j
public class OrderSettleController {

	@Autowired
	private OrderSettleService orderSettleService;

    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "查询支付款项信息", description = "查询支付款项信息", response = String.class)
    @PostMapping("/deposit")
    public ResponseData<String> settleDeposit(@RequestParam("orderNo") String orderNo) {
        log.info("OrderSettleController settleDeposit start param [{}]", orderNo);
        orderSettleService.settleOrder(orderNo);
        log.info("CashierController getOrderPayableAmount end param [{}],result [{}]");
        return ResponseData.success();
    }

}
