/**
 * 
 */
package com.atzuche.order.cashieraccount.service.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.cashieraccount.exception.CashierPayApplyException;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.autopay.gateway.api.AutoPayGatewaySecondaryService;
import com.autoyol.autopay.gateway.vo.Response;
import com.autoyol.autopay.gateway.vo.req.PrePlatformRequest;
import com.autoyol.autopay.gateway.vo.res.PayResVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class PayRemoteService {
	@Autowired
    AutoPayGatewaySecondaryService autoPayGatewaySecondaryService;
	
    /**
     * 收银支付系统-收银台配置
     * @param refundVo
     */
    public PayResVo getPayPlatform(PrePlatformRequest reqData) {
        log.info("PayRemoteService  getPayPlatform start 支付退款,reqData：[{}]",GsonUtils.toJson(reqData));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "支付收银台服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"PayRemoteService.getPayPlatform");
            String parameter = "model="+GsonUtils.toJson(reqData);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            Response<PayResVo> responseData =  autoPayGatewaySecondaryService.prePlatform(reqData);
            log.info("PayRemoteService  getPayPlatform end 支付收银台,param :[{}] ,responseData：[{}]",GsonUtils.toJson(reqData), GsonUtils.toJson(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, GsonUtils.toJson(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                throw new CashierPayApplyException();
            }
            PayResVo vo = responseData.getData();
            return vo;
        }catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 支付收银台,e：[{}]",e);
            log.error("PayRemoteService getPayPlatform Feign 支付收银台,model：[{}],e：[{}]",GsonUtils.toJson(reqData),e);
            throw new CashierPayApplyException();
        }finally {
            t.complete();
        }
    }

}
