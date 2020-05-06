package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.enums.sys.Env;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.CancelOrderReqDTO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class SettleCancelController {

    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    private CashierPayService cashierPayService;
    @Autowired
    CashierRefundApplyNoTService cashierRefundApplyNoTService;
    //环境变量，1正式，2测试
    @Value("${sysEnv:2}") String sysEnv;
    /**
     * 手动退款-（自己用）
     * @param orderNo
     * @param payKind
     * @return
     */
    @AutoDocMethod(value = "手动退款", description = "手动退款", response = String.class)
    @GetMapping("/settleCancel/cashierRefundApply")
    public ResponseData<String> cashierRefundApply(@RequestParam("orderNo") String orderNo, @RequestParam("payKind") String payKind) {
        //测试环境执行
        if(Env.test.getCode().equals(sysEnv)) {
            log.info("OrderSettleController cashierRefundApply start param orderNo=[{}],payKind={}", orderNo,payKind);
            CashierRefundApplyEntity cashierRefundApply = cashierRefundApplyNoTService.selectorderNo(orderNo,payKind);
            cashierPayService.refundOrderPay(cashierRefundApply);
            log.info("CashierController cashierRefundApply end param [{}],result [{}]");
            return ResponseData.success();
        }else {
            //访问受限
            log.info("pro sysEnv="+sysEnv);
            return ResponseData.createErrorCodeResponse(com.autoyol.commons.web.ErrorCode.DENY_ACCESS.getCode(),com.autoyol.commons.web.ErrorCode.DENY_ACCESS.getText());
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/30 11:35 
     * @Description: 订单取消结算-（自己用）
     * 
     **/
    @AutoDocMethod(value = "订单取消-组合结算", description = "订单取消-组合结算", response = String.class)
    @PostMapping("/settleCancel/orderCancelSettleCombination")
    public ResponseData<?> orderCancelSettleCombination(@RequestBody CancelOrderReqDTO cancelOrderReqDTO){
        log.info("取消订单-结算SettleCashierController.orderCancelSettleCombination cancelOrderReqDTO={}", JSON.toJSONString(cancelOrderReqDTO));
        orderSettleService.orderCancelSettleCombination(cancelOrderReqDTO);
        return ResponseData.success();
    }
}
