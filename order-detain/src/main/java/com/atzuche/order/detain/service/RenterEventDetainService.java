package com.atzuche.order.detain.service;

import com.atzuche.order.detain.entity.RenterEventDetainEntity;
import com.atzuche.order.detain.mapper.RenterEventDetainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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
}
