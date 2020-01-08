package com.atzuche.order.coreapi.listener;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.renterwz.entity.DerenCarApproachCitysEntity;
import com.atzuche.order.renterwz.service.DeRenCarApproachCitiesService;
import com.atzuche.order.renterwz.vo.IllegalToDO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * WzDeRunCitiesListener
 *
 * @author shisong
 * @date 2020/1/8
 */
@Component
public class WzDeRunCitiesListener {

    private static final Logger logger = LoggerFactory.getLogger(WzFeedbackIllegalListener.class);

    @Resource
    private DeRenCarApproachCitiesService deRenCarApproachCitiesService;

    private static final String ORDER_CENTER_WZ_CITIES_QUEUE = "order.center.wz.cities.queue";

    @RabbitListener(queues = ORDER_CENTER_WZ_CITIES_QUEUE , containerFactory="rabbitListenerContainerFactory")
    public void process(Message message) {
        String wzDeRunCitiesJson = new String(message.getBody());
        logger.info("wzDeRunCitiesJson process start param;[{}]", wzDeRunCitiesJson);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "查询德润车辆途径城市信息MQ");

        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"WzDeRunCitiesListener.process");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,wzDeRunCitiesJson);
            DerenCarApproachCitysEntity deRenCarApproachCities = GsonUtils.convertObj(wzDeRunCitiesJson, DerenCarApproachCitysEntity.class);
            deRenCarApproachCitiesService.save(deRenCarApproachCities);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("查询德润车辆途径城市信息MQ 异常,wzCheLeHangInfoJson:[{}] , e :[{}]", wzDeRunCitiesJson,e);
            t.setStatus(e);
            Cat.logError("查询德润车辆途径城市信息MQ 异常, e {}", e);

        }finally {
            t.complete();
        }
        logger.info("WzDeRunCitiesListener process end " );
    }

}
