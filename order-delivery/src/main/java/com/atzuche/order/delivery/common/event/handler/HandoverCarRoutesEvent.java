package com.atzuche.order.delivery.common.event.handler;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.delivery.common.mq.handover.HandoverCarMq;
import com.atzuche.order.delivery.common.mq.handover.HandoverRabbitMQEventEnum;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UserTypeEnum;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.vo.HandoverCarRenYunVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarInfoDTO;
import com.atzuche.order.delivery.vo.handover.HandoverCarRemarkDTO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        log.info("仁云推送过来的交接车进度消息数据：object:{}", JSONObject.toJSONString(object));
        if (Objects.isNull(object)) {
            log.info("仁云推送过来的消息数据存在问题：object:{}", object);
            return;
        }

        HashMap message = GsonUtils.convertObj(GsonUtils.toJson(object), HashMap.class);
        log.info("仁云推送过来的交接车进度Map数据：object:{}", JSONObject.toJSONString(message));
        HandoverCarRenYunVO handoverCarRenYunVO = new HandoverCarRenYunVO();
        try {
            BeanUtils.populate(handoverCarRenYunVO, message);
        } catch (Exception e) {
            log.error("map转换失败，message：{}", JSONObject.toJSONString(message));
        }
        if(StringUtils.isBlank(handoverCarRenYunVO.getUserType()))
        {
            if(message.containsKey("userType")) {
                handoverCarRenYunVO.setUserType(message.get("userType").toString());
            }
        }
        sendMessageToQueue(handoverCarRenYunVO);
    }

    /**
     * 发送消息到MQ
     * @param handoverCarVO
     */
    public void sendMessageToQueue(HandoverCarRenYunVO handoverCarVO)
    {
        log.info("仁云推送过来的消息开始入库操作：handoverCarVO:{}", JSONObject.toJSONString(handoverCarVO));
        if (StringUtils.isBlank(handoverCarVO.getProId()) || !handoverCarVO.isUserType()) {
            return;
        }
        if (!handoverCarVO.getProId().equals("4") && !handoverCarVO.getProId().equals("8") && !handoverCarVO.getProId().equals("3")) {
            return;
        }
        int userType = Integer.valueOf(handoverCarVO.getUserType());
        if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
            log.info("发送租客端事件,OrderNo:{}", handoverCarVO.getOrderNo());
            if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                HandoverCarVO handoverCar = createHandoverParams(handoverCarVO, RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue(), handoverCarVO.getServiceType());
                log.info("交接车时间数据入库：{}", JSONObject.toJSONString(handoverCar));
                handoverCarService.addHandoverCarInfo(handoverCar, userType);
            } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                HandoverCarVO handoverCar = createHandoverParams(handoverCarVO, RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue(), handoverCarVO.getServiceType());
                log.info("交接车时间数据入库：{}", JSONObject.toJSONString(handoverCar));
                handoverCarService.addHandoverCarInfo(handoverCar, userType);
            }
        } else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
            log.info("发送车主端事件,OrderNo:{}", handoverCarVO.getOrderNo());
            if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                HandoverCarVO handoverCar = createHandoverParams(handoverCarVO, RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue(), handoverCarVO.getServiceType());
                log.info("交接车时间数据入库：{}", JSONObject.toJSONString(handoverCar));
                handoverCarService.addHandoverCarInfo(handoverCar, userType);
            } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                HandoverCarVO handoverCar = createHandoverParams(handoverCarVO, RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue(), handoverCarVO.getServiceType());
                log.info("交接车时间数据入库：{}", JSONObject.toJSONString(handoverCar));
                handoverCarService.addHandoverCarInfo(handoverCar, userType);
            }
        } else {
            log.info("没有合适的交接车类型接受的数据 handoverCarVO：[{}]", handoverCarVO.toString());
            Cat.logError("没有合适的交接车类型接受的数据 handoverCarVO" + handoverCarVO.toString(), null);
        }
    }

    /**
     * 构造数据
     * @return
     */
    public HandoverCarVO createHandoverParams(HandoverCarRenYunVO handoverCarRenYunVO, Integer type, String serviceType) {
        HandoverCarVO handoverCarVO = new HandoverCarVO();
        HandoverCarInfoDTO handoverCarInfoDTO = new HandoverCarInfoDTO();
        handoverCarInfoDTO.setOrderNo(handoverCarRenYunVO.getOrderNo());
        handoverCarInfoDTO.setType(type);
        if (serviceType.equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
            handoverCarInfoDTO.setRealGetUserName(handoverCarRenYunVO.getHeadName() == null ? "" : handoverCarRenYunVO.getHeadName());
            handoverCarInfoDTO.setRealGetUserPhone(handoverCarRenYunVO.getHeadPhone() == null ? "" : handoverCarRenYunVO.getHeadPhone());
            handoverCarInfoDTO.setRealReturnTime(DateUtil.asLocalDateTime(DateUtil.parseDate(handoverCarRenYunVO.getHandleTime(),DateUtil.BASIC_DATE_TIME_FORMAT)));
        } else if (serviceType.equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
            handoverCarInfoDTO.setRealReturnUserName(handoverCarRenYunVO.getHeadName() == null ? "" : handoverCarRenYunVO.getHeadName());
            handoverCarInfoDTO.setRealReturnUserPhone(handoverCarRenYunVO.getHeadPhone() == null ? "" : handoverCarRenYunVO.getHeadPhone());
            handoverCarInfoDTO.setRealReturnTime(DateUtil.asLocalDateTime(DateUtil.parseDate(handoverCarRenYunVO.getHandleTime(),DateUtil.BASIC_DATE_TIME_FORMAT)));
        }
        handoverCarInfoDTO.setCreateOp("");
        handoverCarInfoDTO.setMsgId(handoverCarRenYunVO.getMessageId());
        if (handoverCarRenYunVO.getDescription() != null) {
            HandoverCarRemarkDTO handoverCarRemarkDTO = new HandoverCarRemarkDTO();
            handoverCarRemarkDTO.setOrderNo(handoverCarRenYunVO.getOrderNo());
            handoverCarRemarkDTO.setRemark(handoverCarRenYunVO.getDescription());
            handoverCarRemarkDTO.setRealName(handoverCarRenYunVO.getHeadName() == null ? "" : handoverCarRenYunVO.getHeadName());
            handoverCarRemarkDTO.setPhone(handoverCarRenYunVO.getHeadPhone() == null ? "" : handoverCarRenYunVO.getHeadPhone());
            handoverCarRemarkDTO.setType(type);
            handoverCarRemarkDTO.setProId(handoverCarRenYunVO.getProId());
            handoverCarVO.setHandoverCarRemarkDTO(handoverCarRemarkDTO);
        }
        handoverCarVO.setHandoverCarInfoDTO(handoverCarInfoDTO);
        return handoverCarVO;
    }

}
