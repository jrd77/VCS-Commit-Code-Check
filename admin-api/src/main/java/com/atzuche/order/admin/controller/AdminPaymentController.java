package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.service.RemoteFeignService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.vo.req.ClearingRefundReqVO;
import com.autoyol.autopay.gateway.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.admin.service.PaymentService;
import com.atzuche.order.admin.vo.req.payment.PaymentRequestVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentInformationResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class AdminPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(AdminPaymentController.class);
    @Autowired
    PaymentService paymentService;
    @Autowired
    RemoteFeignService remoteFeignService;

	@AutoDocMethod(description = "支付信息", value = "支付信息", response = PaymentInformationResponseVO.class)
	@RequestMapping(value="payment/information",method = RequestMethod.POST)
	public ResponseData platformPaymentList(@RequestBody @Validated PaymentRequestVO paymentRequestVO, BindingResult bindingResult) {
        BindingResultUtil.checkBindingResult(bindingResult);
        
        try {
        	PaymentInformationResponseVO respVo = paymentService.platformPaymentList(paymentRequestVO);
        	return ResponseData.success(respVo);
		} catch (Exception e) {
			Cat.logError("获取支付信息异常:params="+paymentRequestVO.toString(),e);
			logger.error("获取支付信息异常:params="+paymentRequestVO.toString(),e);
			return ResponseData.error();
		}
		
	}
    @AutoDocMethod(description = "清算退款", value = "清算退款", response = ClearingRefundReqVO.class)
    @RequestMapping(value="/clearingRefund/clearingRefundSubmit",method = RequestMethod.POST)
    public Response<?> clearingRefundSubmitToRefund(@RequestBody @Validated ClearingRefundReqVO clearingRefundReqVO, BindingResult bindingResult) {
        BindingResultUtil.checkBindingResult(bindingResult);
        clearingRefundReqVO.setOperateName(AdminUserUtil.getAdminUser().getAuthName());
        Response<?> response = remoteFeignService.clearingRefundFromRemote(clearingRefundReqVO);
        return response;
    }
    @AutoDocMethod(description = "清算退款权限", value = "清算退款权限", response = ClearingRefundReqVO.class)
    @RequestMapping(value="/clearingRefund/clearingRefundSubmitpower",method = RequestMethod.POST)
    public ResponseData<Boolean> clearingRefundSubmitToRefundPower(){
        return ResponseData.success(true);
    }

}
