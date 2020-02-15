package com.atzuche.order.photo.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.photo.entity.OrderPhotoEntity;
import com.atzuche.order.photo.service.OrderPhotoService;
import com.atzuche.order.photo.vo.req.DeliveryCarConditionPhotoVO;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author 胡春林
 * 接受仁云 交接车 取还车照片数据
 */
@Service
public class RenYunDeliveryCarPhotoListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenYunDeliveryCarPhotoListener.class);
    private static final String REN_YUN_DELIVERY_CAR_PHOTO = "ren_yun_delivery_car_photo_queue1";

    @Autowired
    OrderPhotoService orderPhotoService;

    /**
     * 获取仁云数据信息
     * @param message
     */
    @RabbitListener(queues = REN_YUN_DELIVERY_CAR_PHOTO, containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(Message message) {
        LOGGER.info("RenYunDeliveryCarPhotoListener process start param;[{}]", message.toString());
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "获取仁云交接车 取还车照片数据信息MQ");
        try {
            String handoverCarOilJson = new String(message.getBody());
            DeliveryCarConditionPhotoVO deliveryCarConditionPhotoVO = GsonUtils.convertObj(handoverCarOilJson, DeliveryCarConditionPhotoVO.class);
            OrderPhotoEntity orderPhotoEntity = new OrderPhotoEntity();
            orderPhotoEntity.setCreateTime(DateUtil.asLocalDateTime(deliveryCarConditionPhotoVO.getCreateTime()));
            orderPhotoEntity.setOrderNo(String.valueOf(deliveryCarConditionPhotoVO.getOrderNo()));
            orderPhotoEntity.setPath(deliveryCarConditionPhotoVO.getPath());
            orderPhotoEntity.setUserType(String.valueOf(deliveryCarConditionPhotoVO.getUserType()));
            orderPhotoEntity.setPhotoType(String.valueOf(deliveryCarConditionPhotoVO.getPhotoType()));
            //更新
            OrderPhotoEntity orderPhoto = orderPhotoService.selectObjectByParams(orderPhotoEntity);
            if (Objects.nonNull(orderPhoto)) {
                BeanUtils.copyProperties(orderPhotoEntity, orderPhoto);
                orderPhoto.setOperator("仁云传过来");
                orderPhotoService.updateDeliveryCarPhotoInfo(orderPhoto.getId(), orderPhoto.getPath(), orderPhoto.getOperator(), orderPhoto.getUserType(), orderPhoto.getPhotoType());
            } else {
                orderPhotoEntity.setSerialNumber(0);
                orderPhotoService.recevieRenYunDeliveryCarPhoto(orderPhotoEntity);
            }
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "RenYunDeliveryCarPhotoListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, handoverCarOilJson);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("获取仁云交接车 取还车照片数据信息MQ异常, e {}", e);

        } finally {
            t.complete();
        }
        LOGGER.info("-----获取交接车、取还车照片数据结束--------");

    }

}

