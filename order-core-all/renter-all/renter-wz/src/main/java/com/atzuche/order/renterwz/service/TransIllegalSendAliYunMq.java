package com.atzuche.order.renterwz.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.model.Message;
import com.atzuche.order.commons.JsonUtil;
import com.atzuche.order.commons.JsonUtils;
import com.atzuche.order.renterwz.aliyunmq.AliyunMnsService;
import com.atzuche.order.renterwz.entity.MqSendFeelbackLogEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;
import com.atzuche.order.renterwz.mapper.MqSendFeelbackLogMapper;
import com.atzuche.order.renterwz.dto.BaseMessageBody;
import com.atzuche.order.renterwz.vo.OrderInfoForIllegal;
import com.atzuche.order.renterwz.vo.PhotoPath;
import com.atzuche.order.renterwz.vo.TransIllegalPhotoBo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * TransIllegalSendAliYunMq
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class TransIllegalSendAliYunMq {

    private static final Logger logger = LoggerFactory.getLogger(TransIllegalSendAliYunMq.class);

    @Resource
    private AliyunMnsService aliyunMnsService;

    @Resource
    private RenyunSendIllegalInfoLogService renyunSendIllegalInfoLogService;

    @Resource
    private RenterOrderWzIllegalPhotoService renterOrderWzIllegalPhotoService;

    @Resource
    private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;

    @Resource
    private MqSendFeelbackLogService mqSendFeelbackLogService;

    @Value("${com.autoyol.mns.queue.renyun-receive-queue-result-feedback-queue}")
    private String renyunReceiveQueueResultFeedbackQueue;
    @Value("${com.autoyol.mns.queue.auto-order-info-for-traffic-violation-queue}")
    private String queueNameTransIllegal;
    @Value("${com.autoyol.mns.queue.auto-renter-voucher-queue}")
    private String renterVoucherQueue;
    @Value("${mq.auto.platform.message.queue}")
    private String mqAutoPlatformMessageQueue;

    /** 普通 bucket */
    @Value("${oss.bucket}")
    public String bucket;

    private static final String OSS_BASE_URL = "oss-cn-hangzhou.aliyuncs.com";

    private String bucketUrl = "http://" + bucket + "." + OSS_BASE_URL + "/";

    public void renYunReceiveQueueResultFeedbackQueue(Map resMap){
        if(resMap==null){
            return ;
        }
        try {
            aliyunMnsService.asyncSend̨MessageToQueue(JsonUtils.fromObject(resMap), renyunReceiveQueueResultFeedbackQueue, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transIllegalPhotoToRenYun(RenterOrderWzIllegalPhotoEntity photo) {
        try{
            String orderNo = photo.getOrderNo();
            String carPlateNum = photo.getCarPlateNum();
            String wzCode = renyunSendIllegalInfoLogService.queryWzCodeByOrderNo(orderNo,carPlateNum);
            TransIllegalPhotoBo transIllegalPhotoBo=new TransIllegalPhotoBo();
            transIllegalPhotoBo.setWzcode(wzCode);
            transIllegalPhotoBo.setOrderno(orderNo);
            transIllegalPhotoBo.setCarNum(carPlateNum);

            List<PhotoPath> photoPathList=renterOrderWzIllegalPhotoService.queryIllegalPhotoByOrderNo(orderNo,carPlateNum);
            if(photoPathList!=null&&!photoPathList.isEmpty()){
                for(PhotoPath p:photoPathList) {
                    p.setImg(bucketUrl+p.getImg());
                }
                transIllegalPhotoBo.setImagePath(photoPathList);
                //发送缴纳凭证MQ
                aliyunMnsService.asyncSend̨MessageToQueue(JsonUtils.fromObject(transIllegalPhotoBo), renterVoucherQueue,true);
            }
            logger.info("发送违章凭证到仁云流程系统-结束");
        }catch (Exception e){
            logger.info("发送违章凭证到仁云流程系统，报错：{}",e);
        }
    }

    public void sendTrafficViolationMq(List<OrderInfoForIllegal> list){
        for(OrderInfoForIllegal bo:list) {
            //记录标示
            renterOrderWzSettleFlagService.addTransIllegalSettleFlag(bo.getOrderno(),bo.getPlatenum(),1,0,"发送违章订单定时任务");

            String msgJson= JsonUtil.toJson(bo);
            aliyunMnsService.asyncSend̨MessageToQueue(msgJson, queueNameTransIllegal,true, new AsyncCallback<Message>() {
                @Override
                public void onSuccess(Message result) {
                    String messageId=result.getMessageId();
                    MqSendFeelbackLogEntity log=new MqSendFeelbackLogEntity();
                    log.setMsgId(messageId);
                    log.setMsg(msgJson);
                    log.setCreateTime(new Date());
                    log.setQueueName(queueNameTransIllegal);
                    log.setStatus("01");
                    log.setSendTime(new Date());
                    mqSendFeelbackLogService.saveMqSendFeelbackLog(log);
                }
                @Override
                public void onFail(Exception ex) {
                    ex.printStackTrace();
                }});
        }
    }

    /**
     * 发送push
     *
     * @param baseMessageBody
     */
    public void sendPushMsg(BaseMessageBody baseMessageBody) {
        String messageBody = null;
        try {
            messageBody = JSON.toJSONString(baseMessageBody);
            logger.info("send platform message to im center ,message body = {}", messageBody);
            if (StringUtils.isEmpty(messageBody)) {
                logger.error("send platform message error,message body is empty!");
                return;
            }
            aliyunMnsService.asyncSend̨MessageToQueue(messageBody, mqAutoPlatformMessageQueue);
        } catch (Exception e) {
            logger.error("send a push message is error, the body is {}, queueName is {}", messageBody, mqAutoPlatformMessageQueue, e);
        }
    }
}
