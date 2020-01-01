package com.atzuche.order.renterwz.service;

import com.atzuche.order.commons.JsonUtils;
import com.atzuche.order.renterwz.aliyunmq.AliyunMnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * TransIllegalSendAliyunMq
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class TransIllegalSendAliyunMq {

    @Resource
    private AliyunMnsService aliyunMnsService;

    @Value("${com.autoyol.mns.queue.renyun-receive-queue-result-feedback-queue}")
    private String renyunReceiveQueueResultFeedbackQueue;

    public void renyunReceiveQueueResultFeedbackQueue(Map resMap){
        if(resMap==null){
            return ;
        }
        try {
            aliyunMnsService.asyncSend̨MessageToQueue(JsonUtils.fromObject(resMap), renyunReceiveQueueResultFeedbackQueue, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
