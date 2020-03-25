package com.atzuche.order.detain.service;

import com.atzuche.order.commons.enums.detain.DetainStatusEnum;
import com.atzuche.order.detain.entity.RenterEventDetainEntity;
import com.atzuche.order.detain.entity.RenterEventDetainStatusEntity;
import com.atzuche.order.detain.mapper.RenterEventDetainStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


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
        RenterEventDetainStatusEntity ent = renterEventDetainStatusMapper.selectByOrderNo(entity.getOrderNo());
        int result;
        if(Objects.isNull(ent)){
            result = renterEventDetainStatusMapper.insertSelective(entity);
        }else {
            entity.setId(ent.getId());
            entity.setVersion(ent.getVersion());
            result = renterEventDetainStatusMapper.updateByPrimaryKeySelective(entity);
        }
        return result;
    }

    public void updateEventDetainStatus(RenterEventDetainEntity renterEventDetainEntity) {
        RenterEventDetainStatusEntity ent = renterEventDetainStatusMapper.selectByRentOrderNo(renterEventDetainEntity.getRenterOrderNo());
        ent.setStatus(DetainStatusEnum.DETAIN_CANCEL.getCode());
        renterEventDetainStatusMapper.updateByPrimaryKeySelective(ent);
    }

    /**
     * 查询订单暂扣信息
     * @param orderNo
     * @return
     */
    public RenterEventDetainStatusEntity getRenterDetainStatus(String orderNo) {
        RenterEventDetainStatusEntity ent = renterEventDetainStatusMapper.selectByOrderNo(orderNo);
        return ent;
    }
}
