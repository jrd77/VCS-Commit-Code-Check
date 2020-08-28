package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.commons.enums.sys.Env;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.CancelOrderReqDTO;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
            List<CashierRefundApplyEntity> list = cashierRefundApplyNoTService.selectorderNo(orderNo,payKind);
            int sum = 0;
            String memNo = "";
            for (CashierRefundApplyEntity cashierRefundApplyEntity : list) {
                AutoPayResultVo result = cashierPayService.orderRefundHandle(cashierRefundApplyEntity);
                if (StringUtils.isBlank(memNo)) {
                    memNo = result.getMemNo();
                }
                if (Objects.nonNull(result) && StringUtils.equals(result.getTransStatus(), TransStatusEnum.PAY_SUCCESS.getCode())) {
                    sum = sum + OrderConstant.ONE;
                }
            }
            if (list.size() == sum && sum > OrderConstant.ZERO) {
                cashierPayService.refundResultHandle(orderNo+":"+payKind, memNo);
            }
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
