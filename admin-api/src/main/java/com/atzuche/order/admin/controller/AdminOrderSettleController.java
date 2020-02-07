package com.atzuche.order.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.service.OrderWzSettleService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("settle")
@Slf4j
public class AdminOrderSettleController {

	@Autowired
	private OrderSettleService orderSettleService;
	@Autowired
	private OrderWzSettleService orderWzSettleService;
	

    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "查询支付款项信息", description = "查询支付款项信息", response = String.class)
    @GetMapping("/deposit")
    public ResponseData<String> settleDeposit(@RequestParam("orderNo") String orderNo) {
        log.info("OrderSettleController settleDeposit start param [{}]", orderNo);
        orderSettleService.settleOrder(orderNo);
        log.info("CashierController getOrderPayableAmount end param [{}],result [{}]");
        return ResponseData.success();
    }

    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "查询支付款项信息", description = "查询支付款项信息", response = String.class)
    @GetMapping("/settleOrderCancel")
    public ResponseData<String> settleOrderCancel(@RequestParam("orderNo") String orderNo) {
        log.info("OrderSettleController settleOrderCancel start param [{}]", orderNo);
        orderSettleService.settleOrderCancel(orderNo);
        log.info("CashierController settleOrderCancel end param [{}],result [{}]");
        return ResponseData.success();
    }
    
    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "手动结算违章押金", description = "结算违章押金", response = String.class)
    @GetMapping("/settleOrderWz")
    public ResponseData<String> settleOrderWz(@RequestParam("orderNo") String orderNo) {
        log.info("OrderSettleController settleOrderWz start param [{}]", orderNo);
        orderWzSettleService.settleWzOrder(orderNo);
        log.info("OrderSettleController settleOrderWz end param [{}],result [{}]");
        return ResponseData.success();
    }
    

}
