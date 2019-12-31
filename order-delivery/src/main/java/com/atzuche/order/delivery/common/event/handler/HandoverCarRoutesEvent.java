package com.atzuche.order.delivery.common.event.handler;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.model.Message;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.ZipUtils;
import com.atzuche.order.delivery.vo.HandoverCarRenYunVO;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        // todo 插入记录数据

        sendMessageToAliMnsQueue(handoverCarRenYunVO);
    }

    public void sendMessageToAliMnsQueue(HandoverCarRenYunVO handoverCarVO)
    {
        handoverCarService.handlerHandoverCarStepByTransInfo(handoverCarVO);
    }
}
