package com.atzuche.order.detain.service;

import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.detain.DetailSourceEnum;
import com.atzuche.order.detain.entity.RenterEventDetainEntity;
import com.atzuche.order.detain.mapper.RenterEventDetainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;


/**
 * 租客端暂扣处理事件表
 *
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 */
@Service
public class RenterEventDetainService{
    @Autowired
    private RenterEventDetainMapper renterEventDetainMapper;

    /**
     * 记录暂扣事件
     * @param entity
     * @return
     */
    public int insertEventDetain(RenterEventDetainEntity entity) {
        int result = renterEventDetainMapper.insertSelective(entity);
        if(result==0){
            throw new RuntimeException("插入暂扣事件异常");
        }
        return result;
    }

    /**
     * 查询冻结记录 并修改冻结状态
     */
    public RenterEventDetainEntity getEventDetainByOrderNoAndEvent(String orderNo, DetailSourceEnum eventType) {
        RenterEventDetainEntity entity = renterEventDetainMapper.getEventDetainByOrderNoAndEvent(orderNo,eventType.getCode());
        Assert.notNull(entity,"暂扣记录不存在");
         //更新0:未冻结
        entity.setFreezeStatus(YesNoEnum.NO.getCode());
        renterEventDetainMapper.updateByPrimaryKeySelective(entity);
        return entity;
    }


}
