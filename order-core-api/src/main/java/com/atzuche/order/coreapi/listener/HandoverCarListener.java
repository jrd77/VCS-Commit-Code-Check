package com.atzuche.order.coreapi.listener;

import com.aliyun.mns.model.Message;
import com.atzuche.order.delivery.common.DeliveryConstants;
import com.atzuche.order.delivery.common.event.handler.HandoverCarAsyncEventPublish;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.autoyol.aliyunmq.annotations.AliyunMnsListener;
import com.dianping.cat.Cat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 胡春林(所有listener走这包)
 * HandoverCarListener 监听交接车数据
 */
@Service
public class HandoverCarListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandoverCarListener.class);

    @Autowired
    HandoverCarAsyncEventPublish handoverCarAsyncEventPublish;
    @Autowired
    HandoverCarService handoverCarService;

    public void onMessage(Message message) {

        if (null == message || StringUtils.isBlank(message.getMessageId())) {
            LOGGER.info("任云返回数据出错-------->>>>>>message={}", message);
        }
        try {
            String msgId = handoverCarService.getHandoverCarInfoByMsgId(message.getMessageId());
            if (StringUtils.isBlank(msgId)) {
                handoverCarAsyncEventPublish.push(message);
            }
        } catch (Exception e) {
            LOGGER.error("任云返回数据出错-------->>>>>>message={}", message.toString(), e);
            Cat.logError("任云返回数据出错-------->>>>>>message={}" + message.toString(), e);
        }
    }
}
