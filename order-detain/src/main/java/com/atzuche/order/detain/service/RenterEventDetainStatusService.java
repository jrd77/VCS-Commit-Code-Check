package com.atzuche.order.detain.service;

import com.atzuche.order.commons.enums.detain.DetainStatusEnum;
import com.atzuche.order.detain.entity.RenterEventDetainEntity;
import com.atzuche.order.detain.entity.RenterEventDetainStatusEntity;
import com.atzuche.order.detain.mapper.RenterEventDetainStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 租客端暂扣处理状态表
 *
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 */
@Service
public class RenterEventDetainStatusService{
    @Autowired
    private RenterEventDetainStatusMapper renterEventDetainStatusMapper;

    public int saveEventDetainStatus(RenterEventDetainStatusEntity entity) {
        int result = renterEventDetainStatusMapper.insertSelective(entity);
        if(result==0){
            throw new RuntimeException("插入暂扣事件异常");
        }
        return result;
    }

    public void updateEventDetainStatus(RenterEventDetainEntity renterEventDetainEntity) {
        RenterEventDetainStatusEntity ent = renterEventDetainStatusMapper.selectByRentOrderNo(renterEventDetainEntity.getRenterOrderNo());
        ent.setStatus(DetainStatusEnum.DETAIN_CANCEL.getCode());
        renterEventDetainStatusMapper.updateByPrimaryKeySelective(ent);
    }
}
