package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.detain.DetainTransReasonEnum;
import com.atzuche.order.coreapi.service.console.ConsoleOrderDepositHandleService;
import com.atzuche.order.detain.dto.CarDepositTemporaryRefundReqDTO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author pengcheng.fu
 * @date 2020/8/25 15:35
 */
@Component
public class WzHoldFeedbackListener {

    private static final Logger logger = LoggerFactory.getLogger(WzHoldFeedbackListener.class);

    private static final String ORDER_CENTER_WZ_WITHHOLD_QUEUE = "order.center.wz.with.hold.feedback.queue";


    @Autowired
    ConsoleOrderDepositHandleService consoleOrderDepositHandleService;


    @RabbitListener(queues = ORDER_CENTER_WZ_WITHHOLD_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void process(Message message) {
        String jsonStr = new String(message.getBody());
        logger.info("WzHoldFeedbackListener.process. >> jsonStr:[{}]", jsonStr);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "违章超6分暂扣押金");
        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "WzHoldFeedbackListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, jsonStr);
            
            messageHandle(jsonStr);

            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("对违章超6分暂扣押金.异常,jsonStr:[{}] , e :[{}]", jsonStr, e);
            t.setStatus(e);
            Cat.logError("对违章超6分暂扣押金.异常", e);
        } finally {
            t.complete();
        }
        logger.info("WzHoldFeedbackListener.process >> end");
    }

    /**
     * mq 处理
     *
     * @param orderNo 订单号
     */
    private void messageHandle(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return;
        }
        //车辆押金暂扣
        CarDepositTemporaryRefundReqDTO carDepositTemporaryRefund = new CarDepositTemporaryRefundReqDTO();
        carDepositTemporaryRefund.setOrderNo(orderNo);
        carDepositTemporaryRefund.setOperator(OrderConstant.SYSTEM_OPERATOR);
        carDepositTemporaryRefund.setFkDetainFlag(String.valueOf(OrderConstant.NO));
        carDepositTemporaryRefund.setLpDetainFlag(String.valueOf(OrderConstant.NO));
        carDepositTemporaryRefund.setJyDetainFlag(String.valueOf(OrderConstant.YES));
        carDepositTemporaryRefund.setJyDetainReason(DetainTransReasonEnum.TWO.getCode());
        consoleOrderDepositHandleService.carDepositDetainHandle(carDepositTemporaryRefund);
        //违章押金暂扣
        consoleOrderDepositHandleService.wzDepositDetainHandle(orderNo);

    }
}
