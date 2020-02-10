package com.atzuche.order.coreapi.controller;

import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.coreapi.service.PayCallbackService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("cashier")
@Slf4j
public class CashierController {

	@Autowired
	private CashierPayService cashierPayService;
    @Autowired PayCallbackService payCallbackService;


    /**
     * 查询支付款项信息
     * @param orderPayReqVO
     * @return
     */
    @AutoDocMethod(value = "查询支付款项信息", description = "查询支付款项信息", response = OrderPayableAmountResVO.class)
    @PostMapping("/getOrderPayableAmount")
    public ResponseData<OrderPayableAmountResVO> getOrderPayableAmount(@Valid @RequestBody OrderPayReqVO orderPayReqVO, BindingResult bindingResult) {
        log.info("CashierController getOrderPayableAmount start param [{}]", GsonUtils.toJson(orderPayReqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        OrderPayableAmountResVO result = cashierPayService.getOrderPayableAmount(orderPayReqVO);
        log.info("CashierController getOrderPayableAmount end param [{}],result [{}]", GsonUtils.toJson(orderPayReqVO),GsonUtils.toJson(result));
        return ResponseData.success(result);
    }
    /**
     * 收银支付获取支付签名串
     * @param orderPaySign
     * @return
     */
    @AutoDocMethod(value = "收银支付获取支付签名串", description = "收银支付获取支付签名串", response = String.class)
    @PostMapping("/getPaySignStr")
	public ResponseData<String> getPaySignStr(@Valid @RequestBody OrderPaySignReqVO orderPaySign,BindingResult bindingResult) {
	    log.info("CashierController getPaySignStr start param [{}]", GsonUtils.toJson(orderPaySign));
        BindingResultUtil.checkBindingResult(bindingResult);
		String result = cashierPayService.getPaySignStr(orderPaySign,payCallbackService);
        log.info("CashierController getPaySignStr end param [{}],result [{}]", GsonUtils.toJson(orderPaySign),result);
        return ResponseData.success(result);
	}
}
