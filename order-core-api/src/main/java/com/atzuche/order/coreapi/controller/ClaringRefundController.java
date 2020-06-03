package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.commons.exceptions.CleanRefoundException;
import com.atzuche.order.commons.exceptions.NotFoundCashierException;
import com.atzuche.order.coreapi.entity.request.ClearingRefundReqVO;
import com.atzuche.order.coreapi.service.ClearingRefundService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * @Author ZhangBin
 * @Date 2020/5/29 14:21
 * @Description: 清算退款
 *
 **/
@Slf4j
@RestController
@RequestMapping("/clearingRefund")
public class ClaringRefundController {
    @Autowired
    private ClearingRefundService clearingRefundService;
    @Autowired
    private CashierNoTService cashierNoTService;

    /*
     * @Author ZhangBin
     * @Date 2020/5/29 14:40
     * @Description: 清算退款提交-退款
     *
     **/
    @PostMapping("/clearingRefundSubmitToQuery")
    public ResponseData<?> clearingRefundSubmitToRefund(@RequestBody ClearingRefundReqVO  clearingRefundReqVO){
        log.info("清算退款提交-退款-入参-clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO));
        String orderNo = clearingRefundReqVO.getOrderNo();
        String payTransNo = clearingRefundReqVO.getPayTransNo();
        CashierEntity cashierEntity = cashierNoTService.getCashierBypayTransNo(orderNo, payTransNo);
        if(cashierEntity == null){
            NotFoundCashierException e = new NotFoundCashierException();
            log.error("清算退款-找不到流水号对应的记录clearingRefundReqVO={}", JSON.toJSONString(clearingRefundReqVO),e);
            throw e;
        }
        String payType = clearingRefundReqVO.getPayType();
        if(true){//退款、预授权解冻、预授权查询
            Integer response = clearingRefundService.clearingRefundSubmitToRefund(clearingRefundReqVO,cashierEntity);
            log.info("清算退款提交-退款-结果{}-入参-clearingRefundReqVO={}",JSON.toJSONString(response),JSON.toJSONString(clearingRefundReqVO));
            return ResponseData.success(response);
        }else if(true){//查询操作
            clearingRefundService.clearingRefundSubmitToQuery(clearingRefundReqVO);
            return null;
        }else if(true){//支付宝同步履约
            return null;
        }else{
            CleanRefoundException e = new CleanRefoundException("无法匹配的操作类型");
            log.error("清算退款-流水号找不到对应的操作类型");
            throw e;
        }
    }
}
