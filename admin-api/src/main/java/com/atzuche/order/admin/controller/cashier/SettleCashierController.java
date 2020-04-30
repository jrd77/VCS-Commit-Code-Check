package com.atzuche.order.admin.controller.cashier;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.open.service.FeignOrderSettleService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.CancelOrderReqDTO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private FeignOrderSettleService feignOrderSettleService;
    /**
     * 手动车辆结算接口
     * @param orderNo
     * @return
     */
    @AutoDocMethod(value = "手动订单车辆押金结算", description = "手动订单车辆押金结算", response = String.class)
    @GetMapping("/deposit")
    public ResponseData<String> settleDeposit(@RequestParam("orderNo") String orderNo) {
        log.info("SettleCashierController deposit start param [{}]", orderNo);
        ResponseData result = feignOrderSettleService.depositSettle(orderNo);
        log.info("SettleCashierController deposit end param [{}],result [{}]",orderNo,result);
        return result;
    }



}
