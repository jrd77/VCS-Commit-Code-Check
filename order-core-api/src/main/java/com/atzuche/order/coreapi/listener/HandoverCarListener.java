package com.atzuche.order.coreapi.listener;

import com.atzuche.order.delivery.common.event.handler.HandoverCarAsyncEventPublish;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author 胡春林(所有listener走这包)
 * HandoverCarListener 监听交接车数据  做数据分流 更换成MQ监听
 */
@Service
public class HandoverCarListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandoverCarListener.class);
    private static final String HANDOVER_CAR_QUEUE = "handover_car_queue";

    @Autowired
    HandoverCarAsyncEventPublish handoverCarAsyncEventPublish;
    @Autowired
    HandoverCarService handoverCarService;

    /**
     * 获取仁云数据信息
     * @param message
     */
    @RabbitListener(queues = HANDOVER_CAR_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void onMessage(Message message) {

        if (Objects.isNull(message)) {
            LOGGER.info("任云返回数据出错-------->>>>>>message={}", message);
            return;
        }
        try {
            String handoverCarJson = new String(message.getBody());
            HashMap handoverCarMap = GsonUtils.convertObj(handoverCarJson, HashMap.class);
            if(Objects.isNull(handoverCarMap))
            {
                LOGGER.info("任云返回数据转换出错-------->>>>>>message={}", message);
                return;
            }
            String msgId = String.valueOf(handoverCarMap.get("messageId"));
            String handoverCarMsgId = handoverCarService.getHandoverCarInfoByMsgId(msgId);
            if (StringUtils.isBlank(handoverCarMsgId)) {
                handoverCarAsyncEventPublish.push(handoverCarMap);
            }
        } catch (Exception e) {
            LOGGER.error("任云返回数据出错-------->>>>>>message={}", message.toString(), e);
            Cat.logError("任云返回数据出错-------->>>>>>message={}" + message.toString(), e);
        }
    }
}
