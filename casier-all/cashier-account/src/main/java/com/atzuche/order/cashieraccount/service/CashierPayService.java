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
import com.atzuche.order.commons.service.RabbitMsgLogService;
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
    @Autowired RabbitMsgLogService rabbitMsgLogService;


    /**
     * 支付系统回调
     * MQ 异步回调
     */
    @Async
    public void payCallBackAsyn(OrderPayAsynResVO orderPayAsynVO){
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBackAsyn");
        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBackAsyn");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,GsonUtils.toJson(orderPayAsynVO));
            //1 校验是否 支付/退款成功
            if(Objects.nonNull(orderPayAsynVO) && "".equals(orderPayAsynVO.getTransStatus())){
                //1 退款
                if(DataPayTypeConstant.PUR_RETURN.equals(orderPayAsynVO.getPayType())){
                    cashierService.refundCallBackSuccess(orderPayAsynVO);
                }
                //2支付成功回调
                if(DataPayTypeConstant.PAY_PUR.equals(orderPayAsynVO.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(orderPayAsynVO.getPayType())){
                    cashierService.payOrderCallBackSuccess(orderPayAsynVO);
                }
            }
            //3 更新rabbitMQ 记录已消费
            rabbitMsgLogService.updateConsume(orderPayAsynVO.getPayType(),orderPayAsynVO.getQn());
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.info("OrderPayCallBack payCallBackAsyn start param;[{}]", GsonUtils.toJson(orderPayAsynVO));
            t.setStatus(e);
            Cat.logError("异步处理支付系统回调 失败",e);
            throw new OrderPayCallBackAsnyException();
        } finally {
            log.info("OrderPayCallBack payCallBackAsyn start end;[{}]", GsonUtils.toJson(orderPayAsynVO));
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

