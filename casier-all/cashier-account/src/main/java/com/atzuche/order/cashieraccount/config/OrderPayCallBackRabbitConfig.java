package com.atzuche.order.cashieraccount.config;

import com.atzuche.order.cashieraccount.common.FasterJsonUtil;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RabbitBusinessTypeEnum;
import com.atzuche.order.commons.service.RabbitMsgLogService;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.vo.req.BatchNotifyDataVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.event.rabbit.pay.PayRabbitMQEventEnum;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * 支付系统回调
 * @author haibao.yan
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
    @RabbitListener(queues="auto-pay-queue")
    @RabbitHandler
    public void payCallBack(Message message){
        log.info("OrderPayCallBack payCallBack start param;[{}]", message);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBack");

        try {
            String orderPayAsynStr = new String(message.getBody());
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBack");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderPayAsynStr);

            BatchNotifyDataVo batchNotifyDataVo = GsonUtils.convertObj(orderPayAsynStr, BatchNotifyDataVo.class);
            String reqContent = FasterJsonUtil.toJson(batchNotifyDataVo);
            String md5 =  MD5.MD5Encode(reqContent);
            rabbitMsgLogService.insertRabbitMsgLog(message, RabbitBusinessTypeEnum.ORDER_PAY_CALL_BACK,orderPayAsynStr,md5);
            cashierPayService.payCallBackAsyn(batchNotifyDataVo);
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

    @Bean
    public Queue TestDirectQueue() {
        return new Queue("auto-pay-queue",true);
    }

    @Bean
    DirectExchange TestDirectExchange() {
        return new DirectExchange(PayRabbitMQEventEnum.AUTO_PAY.exchange);
    }

    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with(PayRabbitMQEventEnum.AUTO_PAY.exchange);
    }

}
