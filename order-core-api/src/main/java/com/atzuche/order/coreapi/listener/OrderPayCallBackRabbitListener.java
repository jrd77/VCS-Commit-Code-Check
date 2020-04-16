package com.atzuche.order.coreapi.listener;

import com.atzuche.order.cashieraccount.common.FasterJsonUtil;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RabbitBusinessTypeEnum;
import com.atzuche.order.coreapi.service.PayCallbackService;
import com.atzuche.order.coreapi.service.RabbitMsgLogService;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.vo.req.BatchNotifyDataVo;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * 支付系统回调
 * @author haibao.yan
 */
@Configuration
@Slf4j
public class OrderPayCallBackRabbitListener {
    @Autowired RabbitMsgLogService rabbitMsgLogService;
    @Autowired CashierPayService cashierPayService;
    
    //回调的处理类
    @Autowired 
    PayCallbackService payCallbackService;

    /**
     * 支付系统回调
     * MQ 异步回调
     */
//    @RabbitListener(bindings = {
//            @QueueBinding(value = @Queue(value = "pay.success.20test", durable = "true"), exchange = @Exchange(value = "auto-paytest", durable = "true", type = "topic"), key = "pay.success.20test")})
    
    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(value = "pay.success.20", durable = "true"), exchange = @Exchange(value = "auto-pay", durable = "true", type = "topic"), key = "pay.success.20")})
    public void payCallBack(Message message) {
        log.info("OrderPayCallBack payCallBack start param;[{}]", message);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBack");

        try {
            String orderPayAsynStr = new String(message.getBody());
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBack");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderPayAsynStr);

            BatchNotifyDataVo batchNotifyDataVo = GsonUtils.convertObj(orderPayAsynStr, BatchNotifyDataVo.class);
            String reqContent = FasterJsonUtil.toJson(batchNotifyDataVo);
            String md5 =  MD5.MD5Encode(reqContent);
            //mq消息落库
            rabbitMsgLogService.insertRabbitMsgLog(message, RabbitBusinessTypeEnum.ORDER_PAY_CALL_BACK,orderPayAsynStr,md5);
            //回调处理
            cashierPayService.payCallBack(batchNotifyDataVo,payCallbackService);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderPayCallBack payCallBack,params message={},e={}",message,e);
            t.setStatus(e);
            Cat.logError("MQ 接收 支付系统回调 失败 ,e :{}",e);
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack payCallBack end " );
    }
    
    //退款处理
    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(value = "refund.success.20", durable = "true"), exchange = @Exchange(value = "auto-refund", durable = "true", type = "topic"), key = "refund.success.20")})
    public void refundCallBack(Message message) {
        log.info("OrderPayCallBack refundCallBack start param;[{}]", message);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调refundCallBack");

        try {
            String orderPayAsynStr = new String(message.getBody());
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.refundCallBack");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,orderPayAsynStr);

            BatchNotifyDataVo batchNotifyDataVo = GsonUtils.convertObj(orderPayAsynStr, BatchNotifyDataVo.class);
            String reqContent = FasterJsonUtil.toJson(batchNotifyDataVo);
            String md5 =  MD5.MD5Encode(reqContent);
            //mq消息落库
            rabbitMsgLogService.insertRabbitMsgLog(message, RabbitBusinessTypeEnum.ORDER_PAY_CALL_BACK,orderPayAsynStr,md5);
            //回调处理,单独处理。
            cashierPayService.refundCallBack(batchNotifyDataVo,payCallbackService);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderPayCallBack refundCallBack,params message={},e={}",message,e);
            t.setStatus(e);
            Cat.logError("MQ 接收 支付系统回调 失败 ,e :{}",e);
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack refundCallBack end " );
    }
    


}