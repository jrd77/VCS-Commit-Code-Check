package com.atzuche.order.renterwz.service;

import com.atzuche.order.commons.JsonUtils;
import com.atzuche.order.renterwz.aliyunmq.AliyunMnsService;
import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;
import com.atzuche.order.renterwz.vo.PhotoPath;
import com.atzuche.order.renterwz.vo.TransIllegalPhotoBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Value("${com.autoyol.mns.queue.renyun-receive-queue-result-feedback-queue}")
    private String renyunReceiveQueueResultFeedbackQueue;

    @Value("${com.autoyol.mns.queue.auto-renter-voucher-queue}")
    private String renterVoucherQueue;

    /** 普通 bucket */
    @Value("${oss.bucket}")
    public String bucket;

    private static final String OSS_BASE_URL = "oss-cn-hangzhou.aliyuncs.com";

    private String bucketUrl = "http://" + bucket + "." + OSS_BASE_URL + "/";

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

    public void transIllegalPhotoToRenyun(RenterOrderWzIllegalPhotoEntity photo) {
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
}
