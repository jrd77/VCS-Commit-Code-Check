package com.atzuche.order.coreapi.controller;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.remote.PayRemoteService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.coreapi.service.PayCallbackService;
import com.autoyol.autopay.gateway.vo.req.PrePlatformRequest;
import com.autoyol.autopay.gateway.vo.res.PayResVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("cashier")
@Slf4j
public class CashierController {

	@Autowired
	private CashierPayService cashierPayService;
    @Autowired PayCallbackService payCallbackService;
    @Autowired
    PayRemoteService payRemoteService;

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
        //支付金额大于0
        if(result.getAmtTotal() < 0) {  //带支付 为负数
	        //调起支付平台获取收银台信息
	        PrePlatformRequest reqData = new PrePlatformRequest();
	        //赋值
	        putPrePlatformRequest(orderPayReqVO, result, reqData);
			
	        PayResVo payResVo = payRemoteService.getPayPlatform(reqData);
	        if(payResVo != null) {
	        	BeanUtils.copyProperties(payResVo, result);
	        }
        }
        return ResponseData.success(result);
    }

	private void putPrePlatformRequest(OrderPayReqVO orderPayReqVO, OrderPayableAmountResVO result,
			PrePlatformRequest reqData) {
		reqData.setAtappId(orderPayReqVO.getAtappId());
        reqData.setInternalNo(orderPayReqVO.getInternalNo());
        //正数
        reqData.setPayAmt(String.valueOf(Math.abs(result.getAmtTotal())));  //支付金额
        reqData.setPayKind(orderPayReqVO.getPayKind().get(0)); //默认取第一个。
        reqData.setPayType(orderPayReqVO.getPayType());  //消费
        reqData.setOrderNo(orderPayReqVO.getOrderNo());
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
