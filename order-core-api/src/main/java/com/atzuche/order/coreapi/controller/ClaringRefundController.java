package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
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
    /*
     * @Author ZhangBin
     * @Date 2020/5/29 14:40
     * @Description: 清算退款提交-查询
     *
     **/
    @GetMapping("/clearingRefundSubmitToQuery")
    public ResponseData<?> clearingRefundSubmitToQuery(@RequestBody ClearingRefundReqVO  clearingRefundReqVO){
        log.info("清算退款提交-查询-入参-clearingRefundReqVO={}", JSON.toJSONString(clearingRefundReqVO));
        clearingRefundService.clearingRefundSubmitToQuery(clearingRefundReqVO);
        return null;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/5/29 14:40
     * @Description: 清算退款提交-退款
     *
     **/
    @PostMapping("/clearingRefundSubmitToQuery")
    public ResponseData<?> clearingRefundSubmitToRefund(@RequestBody ClearingRefundReqVO  clearingRefundReqVO){
        log.info("清算退款提交-退款-入参-clearingRefundReqVO={}",JSON.toJSONString(clearingRefundReqVO));
        ResponseData<?> response = clearingRefundService.clearingRefundSubmitToRefund(clearingRefundReqVO);
        log.info("清算退款提交-退款-结果{}-入参-clearingRefundReqVO={}",JSON.toJSONString(response),JSON.toJSONString(clearingRefundReqVO));
        return response;
    }

}
