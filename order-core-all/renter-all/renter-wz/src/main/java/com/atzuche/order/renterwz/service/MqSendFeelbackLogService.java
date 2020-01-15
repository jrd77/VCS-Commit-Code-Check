package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.MqSendFeelbackLogEntity;
import com.atzuche.order.renterwz.mapper.MqSendFeelbackLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * MqSendFeelbackLogService
 *
 * @author shisong
 * @date 2020/1/15
 */
@Service
public class MqSendFeelbackLogService {

    @Resource
    private MqSendFeelbackLogMapper mqSendFeelbackLogMapper;

    public void updateByMsgIdSelective(MqSendFeelbackLogEntity log) {
        mqSendFeelbackLogMapper.updateMqSendFeelbackLog(log);
    }

    void saveMqSendFeelbackLog(MqSendFeelbackLogEntity log) {
        mqSendFeelbackLogMapper.saveMqSendFeelbackLog(log);
    }
}
