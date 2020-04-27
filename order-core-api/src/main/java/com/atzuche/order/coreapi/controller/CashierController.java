package com.atzuche.order.coreapi.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.service.remote.PayRemoteService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.sys.Env;
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
    @Autowired
    CashierRefundApplyNoTService cashierRefundApplyNoTService;
    //环境变量，1正式，2测试
    @Value("${sysEnv:2}") String sysEnv;
    
    /**
     * 查询支付款项信息
     * 类同获取支付金额和调起收银台getTransPlatform接口，对应APPSERVER
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
        //入参未传递的化，不考虑收银台的数据获取。兼容该接口之前的支付宝小程序的调用。
        /**
         * 拉取收银台配置信息。
         */
        if(StringUtils.isNotBlank(orderPayReqVO.getPayType()) && StringUtils.isNotBlank(orderPayReqVO.getAtappId())) {  //带支付 为负数
        	//AppServer端调用，小程序没有收银台。
//        	if(result.getAmtTotal() < 0) {
        	if(result.getAmt() < 0) {
		        //调起支付平台获取收银台信息
		        PrePlatformRequest reqData = new PrePlatformRequest();
		        //赋值
		        putPrePlatformRequest(orderPayReqVO, result, reqData);
				
		        PayResVo payResVo = payRemoteService.getPayPlatform(reqData);
		        if(payResVo != null) {
		        	BeanUtils.copyProperties(payResVo, result);
		        }
        	}else {
//        		if(result.isEnterpriseUserOrder() && result.getAmt() == 0) {
//        			log.info("企业用户订单钱包全部抵扣，正常返回，参数的请求:params=[{}]",GsonUtils.toJson(orderPayReqVO));
//        		}else {
//        			//返回提示信息
	        		//金额异常的情况，  提示“没有待支付记录”
	        		return ResponseData.createErrorCodeResponse(ErrorCode.CASHIER_PAY_SIGN_FAIL_ERRER.getCode(), ErrorCode.CASHIER_PAY_SIGN_FAIL_ERRER.getText());
//        		}
        	}
        }else {
        	/**
        	 * 基本原则：
        	 * 进入收银台和拉取tn不能抵扣，否则显示金额和实际支付金额不一致。
        	 */
        	
        	/**
        	 * 刷新订单详情的时候。
        	 */
        	try {
            	//检查是否企业用户订单，刷新钱包,押金为0的情况。
//        		result.getAmt() < 0  //避免为0的情况多次刷新，导致支付双押金为0的情况，多次回调。
        		//result.getAmtWallet() 第一步：计算，第二步：抵扣。分开处理。
                if(result != null && result.isEnterpriseUserOrder() && result.getAmtWallet() > 0) {  //否则无需抵扣
                	//默认按使用钱包处理。
                	OrderPaySignReqVO orderPaySign = cashierPayService.buildOrderPaySignReqVO(result.getOrderNo(), result.getMemNo(), 1);
                	log.info("commonWalletDebt params=[{}]",GsonUtils.toJson(orderPaySign));
                	//默认按使用钱包处理。
                	result.setIsUseWallet(1);
                	cashierPayService.commonWalletDebt(orderPaySign, payCallbackService, result);
                	
                }
    		} catch (Exception e) {
    			log.error("刷新订单详情钱包抵扣异常:params=[{}]",GsonUtils.toJson(result),e);
    		}
        	
        	log.info("无需收银台参数的请求:params=[{}]",GsonUtils.toJson(orderPayReqVO));
        }
        
        return ResponseData.success(result);
    }

	private void putPrePlatformRequest(OrderPayReqVO orderPayReqVO, OrderPayableAmountResVO result,
			PrePlatformRequest reqData) {
		reqData.setAtappId(orderPayReqVO.getAtappId());
        reqData.setInternalNo(orderPayReqVO.getInternalNo());
        //正数
//        reqData.setPayAmt(String.valueOf(Math.abs(result.getAmtTotal())));  //支付金额
        reqData.setPayAmt(String.valueOf(Math.abs(result.getAmt())));  //支付金额
        reqData.setPayKind(orderPayReqVO.getPayKind().get(0)); //默认取第一个。
        reqData.setPayType(orderPayReqVO.getPayType());  //消费
        reqData.setOrderNo(orderPayReqVO.getOrderNo());
	}
    
    /**
     * 收银支付获取支付签名串
     * 类同获取支付金额和getTransTn接口(第一部分，第二部分是直接请求 支付平台网关。/public/paygw/routingrules/payBatch)，对应APPSERVER
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
    
    
    /**
     * 同XXLJOB定时任务退款方法。
     * @param orderNo
     * @param payKind
     * @return
     */
    @AutoDocMethod(value = "手动退款", description = "手动退款", response = String.class)
    @GetMapping("/cashierRefundApply")
    public ResponseData<String> cashierRefundApply(@RequestParam("orderNo") String orderNo,@RequestParam("payKind") String payKind) {
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
    
}
