package com.atzuche.delivery.utils;

import com.alibaba.fastjson.JSON;
import com.atzuche.delivery.entity.DeliveryHttpLogEntity;
import com.atzuche.delivery.mapper.DeliveryHttpLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 胡春林
 * 请求仁云数据log
 */
@Component
public class DeliveryLogUtil {

    public static Logger logger = LoggerFactory.getLogger(DeliveryLogUtil.class);

    @Autowired
    DeliveryHttpLogMapper deliveryHttpLogMapper;

    public void addDeliveryLog(DeliveryHttpLogEntity deliveryHttpLogEntity) {
        deliveryHttpLogMapper.insert(deliveryHttpLogEntity);
        logger.info("记录司机修改日志" + JSON.toJSONString(deliveryHttpLogEntity));
    }

}
