package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.common.DataPayTypeConstant;
import com.atzuche.order.cashieraccount.exception.OrderPayCallBackAsnyException;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayAsynResVO;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;


/**
 * 收银台支付退款操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierPayService{

    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired CashierService cashierService;



    /**
     * 支付系统回调
     * MQ 异步回调
     */
    @Async
    public void payCallBackAsyn(String orderPayAsynStr){
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调");
        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBackAsyn");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderPayAsynStr);
            OrderPayAsynResVO orderPayAsynVO = GsonUtils.convertObj(orderPayAsynStr, OrderPayAsynResVO.class);
            //1 校验是否 支付/退款成功
            if(Objects.nonNull(orderPayAsynVO) && "".equals(orderPayAsynVO.getTransStatus())){
                //1 退款
                if(DataPayTypeConstant.PUR_RETURN.equals(orderPayAsynVO.getPayType())){
                    cashierService.refundCallBackSuccess(orderPayAsynVO);
                    t.setStatus(Transaction.SUCCESS);
                    return;
                }
                //支付成功回调
                if(DataPayTypeConstant.PAY_PUR.equals(orderPayAsynVO.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(orderPayAsynVO.getPayType())){
                    cashierService.payOrderCallBackSuccess(orderPayAsynVO);
                    t.setStatus(Transaction.SUCCESS);
                    return;
                }
            }
            t.setStatus(Transaction.SUCCESS);
            log.info("OrderPayCallBack payCallBackAsyn start end;[{}]", orderPayAsynStr);
        } catch (Exception e) {
            log.info("OrderPayCallBack payCallBackAsyn start param;[{}]", orderPayAsynStr);
            t.setStatus(e);
            Cat.logError("Feign 获取车主会员信息失败",e);
            throw new OrderPayCallBackAsnyException();
        } finally {
            t.complete();
        }


    }


    /**
     * 获取支付验签数据
     * @param orderPaySign
     * @return
     */
    public String getPaySignStr(OrderPaySignReqVO orderPaySign){
        //1校验
        Assert.notNull(orderPaySign, ErrorCode.PARAMETER_ERROR.getText());
        orderPaySign.check();
        return null;
    }

    /**
     * 车俩押金支付成功回调
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit){

    }

    /**
     * 支付成功后记录 实付违章押金信息 和违章押金资金进出信息
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateRenterWZDeposit(PayedOrderRenterWZDepositReqVO payedOrderWZRenterDeposit){
    }

}

