/**
 * 
 */
package com.atzuche.order.coreapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.commons.vo.req.PaymentReqVO;
import com.atzuche.order.commons.vo.res.CashierResVO;
import com.atzuche.order.coreapi.service.PaymentCashierService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

/**
 * @author jing.huang
 *
 */
@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class PaymentController {
	private static final Logger logger = LoggerFactory.getLogger(SubmitOrderController.class);
	@Autowired
	PaymentCashierService paymentCashierService;
	
	@AutoDocMethod(description = "查询支付记录", value = "查询支付记录", response = CashierResVO.class)
    @PostMapping("/payment/queryByOrderNo")
    public ResponseData<List<CashierResVO>> queryByOrderNo(@Valid @RequestBody PaymentReqVO vo, BindingResult bindingResult) throws Exception {
		logger.info("queryByOrderNo:[{}]", JSON.toJSONString(vo));
        try {
        	 List<CashierResVO> lst = paymentCashierService.queryPaymentList(vo.getOrderNo());
             return ResponseData.success(lst);
		} catch (Exception e) {
			logger.error("查询收银台记录异常:",e);
			return ResponseData.error();
		}
	}
}
