package com.atzuche.order.cashieraccount.config;

import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayAsynResVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RabbitBusinessTypeEnum;
import com.atzuche.order.commons.service.RabbitMsgLogService;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 支付系统回调
 */
@Component
@Slf4j
public class OrderPayCallBackRabbitConfig {
    @Autowired RabbitMsgLogService rabbitMsgLogService;
    @Autowired CashierPayService cashierPayService;
    /**
     * 支付系统回调
     * MQ 异步回调
     */
    @RabbitListener(queues="${xxx}")
    @RabbitHandler
    public void payCallBack(Message message, Channel channel){
        log.info("OrderPayCallBack payCallBack start param;[{}]", message);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBack");

        try {
            String orderPayAsynStr = new String(message.getBody());
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBack");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderPayAsynStr);

            OrderPayAsynResVO orderPayAsynVO = GsonUtils.convertObj(orderPayAsynStr, OrderPayAsynResVO.class);
            rabbitMsgLogService.insertRabbitMsgLog(message, RabbitBusinessTypeEnum.ORDER_PAY_CALL_BACK,orderPayAsynStr,orderPayAsynVO.getQn());
            cashierPayService.payCallBackAsyn(orderPayAsynVO);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderPayCallBack payCallBack,e={},message={}",e,message);
            t.setStatus(e);
            Cat.logError("MQ 接收 支付系统回调 失败 ,e :{}",e);
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack payCallBack end " );
    }


}
