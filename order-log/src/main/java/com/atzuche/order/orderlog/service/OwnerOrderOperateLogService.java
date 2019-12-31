package com.atzuche.order.orderlog.service;

import com.atzuche.order.orderlog.entity.OwnerOrderOperateLogEntity;
import com.atzuche.order.orderlog.mapper.OwnerOrderOperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 车主订单操作记录表
 *
 * @author ZhangBin
 * @date 2019-12-30 20:31:55
 */
@Service
public class OwnerOrderOperateLogService{
    @Autowired
    private OwnerOrderOperateLogMapper ownerOrderOperateLogMapper;

    public void saveOrderOperateLog(OwnerOrderOperateLogEntity ownerOrderOperateLogEntity){
        ownerOrderOperateLogMapper.insert(ownerOrderOperateLogEntity);
    }
}
