package com.atzuche.order.rentercost.service;

import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderCostDetailMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客费用明细表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:30:57
 */
@Service
public class RenterOrderCostDetailService{
    @Autowired
    private RenterOrderCostDetailMapper renterOrderCostDetailMapper;


    /**
     * 批量保存费用明细
     * @param costList
     * @return Integer
     */
    public Integer saveRenterOrderCostDetailBatch(List<RenterOrderCostDetailEntity> costList) {
    	return renterOrderCostDetailMapper.saveRenterOrderCostDetailBatch(costList);
    }
}
