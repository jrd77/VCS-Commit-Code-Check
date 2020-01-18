package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.delivery.service.handover.HandoverCarOilMileageService;
import com.atzuche.order.delivery.vo.handover.MileageOilVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 胡春林
 * 获取车主修改后的里程油耗
 */
@Service
public class HandoverCarMileageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandoverCarOilListener.class);
    private static final String HANDOVER_CAR_MILEAGE_QUEUE = "handover_car_oil_queue";

    @Autowired
    HandoverCarOilMileageService handoverCarOilMileageService;

    /**
     * 获取油耗数据信息
     *
     * @param message
     */
    @RabbitListener(queues = HANDOVER_CAR_MILEAGE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(Message message) {
        LOGGER.info("HandoverCarOilListener process start param;[{}]", message.toString());
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "获取车主里程油耗信息MQ");
        try {
            String oilMileageData = new String(message.getBody());
            MileageOilVO mileageOilVO = GsonUtils.convertObj(oilMileageData, MileageOilVO.class);
            handoverCarOilMileageService.saveMileageOil(mileageOilVO);
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD, "HandoverCarOilListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM, oilMileageData);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("获取车主油耗和里程处理信息MQ异常, e {}", e);

        } finally {
            t.complete();
        }
        LOGGER.info("-----获取车主油耗和里程结束--------");

    }
}
