//package com.atzuche.delivery.listener;
//
//import com.aliyun.mns.model.Message;
//import com.atzuche.delivery.common.DeliveryConstants;
//import com.atzuche.delivery.common.event.handler.HandoverCarAsyncEventPublish;
//import com.autoyol.aliyunmq.annotations.AliyunMnsListener;
//import com.dianping.cat.Cat;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author 胡春林
// * HandoverCarListener 监听交接车数据
// */
//@Slf4j
//@Component
//public class HandoverCarListener {
//
//    @Autowired
//    HandoverCarAsyncEventPublish handoverCarAsyncEventPublish;
//
//    @AliyunMnsListener(queueKey = DeliveryConstants.REN_YUN_QUEUE_KEY)
//    public void onMessage(Message message) {
//
//        if (null == message || StringUtils.isBlank(message.getMessageId())) {
//            log.info("任云返回数据出错-------->>>>>>message={}", message);
//        }
//        String msgId = "";
//        try {
//            // todo 查询数据库是否存在mesId transProgressService.queryByMesId(messageIdPre);
//            if (StringUtils.isBlank(msgId)) {
//                handoverCarAsyncEventPublish.push(message);
//            }
//        } catch (Exception e) {
//            log.error("任云返回数据出错-------->>>>>>message={}", message.toString(), e);
//            Cat.logError("任云返回数据出错-------->>>>>>message={}" + message.toString(), e);
//        }
//    }
//}
