package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import com.atzuche.order.renterorder.mapper.OwnerCouponLongMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 主订单表
 *
 * @author ZhangBin
 * @date 2020-04-07 11:18:55
 */
@Service
public class OwnerCouponLongService{
    @Autowired
    private OwnerCouponLongMapper ownerCouponLongMapper;


    public OwnerCouponLongEntity getByRenterOrderNo(String renterOrderNo) {
        return ownerCouponLongMapper.getByRenterOrderNo(renterOrderNo);
    }
    
    /**
     * 保存长租折扣信息
     * @param ownerCouponLongEntity
     * @return Integer
     */
    public Integer saveOwnerCouponLong(OwnerCouponLongEntity ownerCouponLongEntity) {
    	if (ownerCouponLongEntity == null) {
    		return 0;
    	}
    	return ownerCouponLongMapper.insertSelective(ownerCouponLongEntity);
    }
    
    /**
     * 批量保存长租折扣信息
     * @param list
     */
    public void saveOwnerCouponLongBatch(List<OwnerCouponLongEntity> list) {
    	if (list == null || list.isEmpty()) {
    		return;
    	}
    	for (OwnerCouponLongEntity record:list) {
    		saveOwnerCouponLong(record);
    	}
    }
}
