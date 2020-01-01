package com.atzuche.order.cashieraccount.service.remote;

import com.atzuche.order.cashieraccount.exception.DeductWalletRemoteException;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.autopay.gateway.api.AutoPayGatewaySecondaryService;
import com.autoyol.autopay.gateway.vo.Response;
import com.autoyol.autopay.gateway.vo.req.RefundVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RefundRemoteService {
    @Autowired
    AutoPayGatewaySecondaryService autoPayGatewaySecondaryService;

    /**
     * 收银支付系统退款
     * @param refundVo
     */
    public AutoPayResultVo refundOrderPay(RefundVo refundVo) {
        log.info("RefundRemoteService  refundOrderPay start 支付退款,walletDeduction：[{}]",GsonUtils.toJson(refundVo));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "支付退款服务");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"RefundRemoteService.refundOrderPay");
            String parameter = "model="+GsonUtils.toJson(refundVo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,parameter);
            Response<AutoPayResultVo> responseData =  autoPayGatewaySecondaryService.routingRulesRefund(refundVo);
            log.info("RefundRemoteService  refundOrderPay end 支付退款,responseData：[{}]", GsonUtils.toJson(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, GsonUtils.toJson(responseData));
            if(responseData == null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                throw new DeductWalletRemoteException();
            }
            AutoPayResultVo vo = responseData.getData();
            return vo;
        }catch (Exception e){
            t.setStatus(e);
            Cat.logError("Feign 支付退款,e：[{}]",e);
            log.error("RefundRemoteService refundOrderPay Feign 支付退款,model：[{}],e：[{}]",GsonUtils.toJson(refundVo),e);
            throw new DeductWalletRemoteException();
        }finally {
            t.complete();
        }
    }


}
