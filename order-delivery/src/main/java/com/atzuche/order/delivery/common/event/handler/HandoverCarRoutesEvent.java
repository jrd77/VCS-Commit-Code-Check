package com.atzuche.order.delivery.common.event.handler;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.atzuche.order.delivery.common.mq.handover.HandoverCarMq;
import com.atzuche.order.delivery.common.mq.handover.HandoverRabbitMQEventEnum;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UserTypeEnum;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.ZipUtils;
import com.atzuche.order.delivery.vo.HandoverCarRenYunVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 胡春林
 * 处理仁云推送过来的消息数据
 */
@Component
@Slf4j
public class HandoverCarRoutesEvent {

    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Subscribe
    @AllowConcurrentEvents
    public void onProcessHandoverCarInfo(Object object){

        if (Objects.isNull(object)) {
            log.info("仁云推送过来的消息数据存在问题：object:{}", object);
            return;
        }
        Message message = (Message)object;
        byte[] messageBody = message.getMessageBodyAsBytes();
        String json = ZipUtils.uncompress(messageBody);
        HandoverCarRenYunVO handoverCarRenYunVO = JSONObject.parseObject(json, HandoverCarRenYunVO.class);
        handoverCarRenYunVO.setMessageId(message.getMessageId());
        //发送mq event
        sendMessageToQueue(handoverCarRenYunVO);
    }

    /**
     * 发送消息到MQ
     * @param handoverCarVO
     */
    public void sendMessageToQueue(HandoverCarRenYunVO handoverCarVO)
    {
        if (StringUtils.isBlank(handoverCarVO.getProId()) || !handoverCarVO.isUserType()) {
            return;
        }
        int userType = Integer.valueOf(handoverCarVO.getUserType());
        HandoverCarMq handoverCarMq = new HandoverCarMq();
        handoverCarMq.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
        handoverCarMq.setMsgCreateTime(DateUtil.formatDate(new Date(),"yyyyMMddHHmmss"));
        if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
            log.info("发送租客端事件,OrderNo:{}", handoverCarVO.getOrderNo());
            if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                handoverCarMq.setRouteKey(HandoverRabbitMQEventEnum.RENTER_TAKE.routingKey);
                String handoverCarMqJson = GsonUtils.toJson(handoverCarMq);
                rabbitTemplate.convertAndSend(HandoverRabbitMQEventEnum.RENTER_TAKE.exchange,HandoverRabbitMQEventEnum.RENTER_TAKE.routingKey,handoverCarMqJson);
            } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                handoverCarMq.setRouteKey(HandoverRabbitMQEventEnum.RENTER_TAKE.routingKey);
                String handoverCarMqJson = GsonUtils.toJson(handoverCarMq);
                rabbitTemplate.convertAndSend(HandoverRabbitMQEventEnum.RENTER_TAKE.exchange,HandoverRabbitMQEventEnum.RENTER_BACK.routingKey,handoverCarMqJson);
            }
        }
        else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
            log.info("发送车主端事件,OrderNo:{}", handoverCarVO.getOrderNo());
            if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                handoverCarMq.setRouteKey(HandoverRabbitMQEventEnum.RENTER_TAKE.routingKey);
                String handoverCarMqJson = GsonUtils.toJson(handoverCarMq);
                rabbitTemplate.convertAndSend(HandoverRabbitMQEventEnum.RENTER_TAKE.exchange,HandoverRabbitMQEventEnum.OWNER_TAKE.routingKey,handoverCarMqJson);
            } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                handoverCarMq.setRouteKey(HandoverRabbitMQEventEnum.RENTER_TAKE.routingKey);
                String handoverCarMqJson = GsonUtils.toJson(handoverCarMq);
                rabbitTemplate.convertAndSend(HandoverRabbitMQEventEnum.RENTER_TAKE.exchange,HandoverRabbitMQEventEnum.OWNER_BACK.routingKey,handoverCarMqJson);
            }
        }else {
            log.info("没有合适的交接车类型接受的数据 handoverCarVO：[{}]",handoverCarVO.toString());
            Cat.logError("没有合适的交接车类型接受的数据 handoverCarVO" + handoverCarVO.toString(),null);
        }
    }
}
