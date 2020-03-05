//package com.atzuche.order.mq.common.push;
//
//import com.alibaba.fastjson.JSONObject;
//import com.autoyol.aliyunmq.AliyunMnsService;
//import com.autoyol.commons.utils.GsonUtils;
//import com.google.common.collect.Maps;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * @author 胡春林
// * 推送通知
// */
//@Service
//public class PushMessageService {
//
//    private static final Logger logger = LoggerFactory.getLogger(PushMessageService.class);
//
//    @Value("${com.autoyol.mns.queue.auto-platform-message-queue}")
//    private String PLATFORM_MESSAGE_QUEUE_NAME;
//
//    @Resource
//    AliyunMnsService aliyunMnsService;
//
//    /**
//     * 推送数据
//     * @param paramsMap
//     */
//    public void sendCompleteOrderMsg(Map paramsMap) {
//        paramsMap.put("messageId", UUID.randomUUID().toString().replaceAll("-", ""));
//        logger.info("传入数据-------->>>"+ JSONObject.toJSONString(paramsMap));
//        aliyunMnsService.asyncSend̨MessageToQueue(GsonUtils.toJson(paramsMap), PLATFORM_MESSAGE_QUEUE_NAME);
//
//    }
//
//}
