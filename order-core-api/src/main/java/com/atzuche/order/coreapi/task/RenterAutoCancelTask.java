package com.atzuche.order.coreapi.task;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.atzuche.order.coreapi.listener.push.OrderSendMessageFactory;
import com.atzuche.order.coreapi.listener.sms.SMSOrderBaseEventService;
import com.atzuche.order.coreapi.service.CancelOrderService;
import com.atzuche.order.coreapi.service.OrderSearchRemoteService;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * RenterAutoCancelTask
 *
 * @author shisong
 * @date 2020/1/15
 */
@Component
@JobHandler("renterAutoCancelTask")
public class RenterAutoCancelTask extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(RenterAutoCancelTask.class);

    @Resource
    private OrderSearchRemoteService orderSearchRemoteService;

    @Resource
    private CancelOrderService cancelOrderService;

    @Autowired
    OrderSendMessageFactory orderSendMessageFactory;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "定时查询下单后1小时，租客未支付租车费用,自动取消 定时任务");
        try {
            Cat.logEvent(CatConstants.XXL_JOB_METHOD,"renterAutoCancelTask.execute");
            Cat.logEvent(CatConstants.XXL_JOB_PARAM,null);
            logger.info("开始执行 下单后1小时，租客未支付租车费用,自动取消  定时器");
            XxlJobLogger.log("开始执行 下单后1小时，租客未支付租车费用,自动取消 定时器");

            logger.info("查询 远程调用 查询下单后1小时，租客未支付租车费用的订单列表,查询开始");
            XxlJobLogger.log("查询 远程调用 查询下单后1小时，租客未支付租车费用的订单列表,查询开始" );

            List<String> orderNos = orderSearchRemoteService.queryOrderNosWithRenterAutoCancel();

            logger.info("查询 远程调用 查询下单后1小时，租客未支付租车费用的订单列表,查询结果 models:[{}]", orderNos);
            XxlJobLogger.log("查询 远程调用 查询下单后1小时，租客未支付租车费用的订单列表,查询结果 models:" + orderNos);

            if(CollectionUtils.isNotEmpty(orderNos)){
                for (String orderNo : orderNos) {
                    CancelOrderReqVO req = new CancelOrderReqVO();
                    req.setOrderNo(orderNo);
                    req.setCancelReason("下单后1小时，租客未支付租车费用,自动取消");
                    req.setMemRole("2");
                    req.setOperatorName(OrderConstant.SYSTEM_OPERATOR_JOB);
                    try {
                        logger.info("执行 下单后1小时，租客未支付租车费用,自动取消 orderNo:[{}]",orderNo);
                        cancelOrderService.cancel(req);
                        //发送sms/push
                        Map paramMaps = Maps.newHashMap();
                        paramMaps.put("indexUrl","");
                        Map smsMap = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_RENTER.getValue(), ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_OWNER.getValue(), null);
                        Map map = SmsParamsMapUtil.getParamsMap(orderNo, PushMessageTypeEnum.RENTER_NO_PAY_CAR.getValue(), PushMessageTypeEnum.RENTER_NO_PAY_CAR_2_OWNER.getValue(), null);
                        orderSendMessageFactory.sendShortMessage(smsMap);
                        orderSendMessageFactory.sendPushMessage(map);

                    } catch (Exception e) {
                        XxlJobLogger.log("执行 下单后1小时，租客未支付租车费用,自动取消 异常:" + e);
                        logger.error("执行 下单后1小时，租客未支付租车费用,自动取消 异常 orderNo:[{}] , e:[{}]",orderNo,e);
                        Cat.logError("执行 下单后1小时，租客未支付租车费用,自动取消 异常",e);
                        t.setStatus(e);
                    }
                }
            }
            logger.info("结束执行 下单后1小时，租客未支付租车费用,自动取消 ");
            XxlJobLogger.log("结束执行 下单后1小时，租客未支付租车费用,自动取消 ");
            t.setStatus(Transaction.SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log("执行 下单后1小时，租客未支付租车费用,自动取消 异常:" + e);
            logger.error("执行 下单后1小时，租客未支付租车费用,自动取消 异常",e);
            Cat.logError("执行 下单后1小时，租客未支付租车费用,自动取消 异常",e);
            t.setStatus(e);
            return new ReturnT(FAIL.getCode(),e.toString());
        } finally {
            if (t != null) {
                t.complete();
            }
        }
    }
}
