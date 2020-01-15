package com.atzuche.order.admin.controller.cashier;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/console/cashier/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class SettleCashierController {

    private static final Logger log = LoggerFactory.getLogger(SettleCashierController.class);


    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    CashierPayService cashierPayService;
    @Autowired
    CashierRefundApplyNoTService cashierRefundApplyNoTService;

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
     * 手动退款
     */
    @AutoDocMethod(value = "查询支付款项信息", description = "查询支付款项信息", response = String.class)
    @GetMapping("/cashierRefundApply")
    public ResponseData<String> cashierRefundApply(@RequestParam("orderNo") String orderNo,@RequestParam("payKind") String payKind) {
        log.info("OrderSettleController settleOrderCancel start param [{}]", orderNo);
        CashierRefundApplyEntity cashierRefundApply = cashierRefundApplyNoTService.selectorderNo(orderNo,payKind);
        cashierPayService.refundOrderPay(cashierRefundApply);
        log.info("CashierController settleOrderCancel end param [{}],result [{}]");
        return ResponseData.success();
    }



}
