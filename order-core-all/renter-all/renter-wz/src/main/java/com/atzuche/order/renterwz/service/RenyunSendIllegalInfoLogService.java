package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenyunSendIllegalInfoLogEntity;
import com.atzuche.order.renterwz.mapper.RenyunSendIllegalInfoLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RenyunSendIllegalInfoLogService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenyunSendIllegalInfoLogService {

    @Resource
    private RenyunSendIllegalInfoLogMapper renyunSendIllegalInfoLogMapper;

    public void saveRenyunSendIllegalInfoLog(RenyunSendIllegalInfoLogEntity log) {
        renyunSendIllegalInfoLogMapper.saveRenyunSendIllegalInfoLog(log);
    }

    public Integer count(String wzCode, String dataType, String carNum) {
        return renyunSendIllegalInfoLogMapper.count(wzCode,dataType,carNum);
    }
}
