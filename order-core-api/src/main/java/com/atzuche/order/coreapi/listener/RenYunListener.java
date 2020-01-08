package com.atzuche.order.coreapi.listener;

import com.atzuche.order.coreapi.entity.dto.RenYunToWzMqDTO;
import com.autoyol.commons.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RenYunListener
 *
 * @author shisong
 * @date 2019/12/28
 */
@Component
public class RenYunListener {

    private static final Logger logger = LoggerFactory.getLogger(RenYunListener.class);

    String  processRenYunMessage(Map<String, String> resMap, String message){
        try {
            RenYunToWzMqDTO renYunToWzMqDTO = GsonUtils.convertObj(message, RenYunToWzMqDTO.class);
            if(renYunToWzMqDTO != null){
                resMap.put("msgId",renYunToWzMqDTO.getMsgId());
                resMap.put("queueName",renYunToWzMqDTO.getQueueName());
                return renYunToWzMqDTO.getMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("convertObj 异常,message:[{}] , e :[{}]", message,e);
        }
        return "";
    }
}
