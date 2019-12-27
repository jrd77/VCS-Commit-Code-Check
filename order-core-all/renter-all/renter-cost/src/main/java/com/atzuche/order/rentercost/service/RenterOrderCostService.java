package com.atzuche.order.rentercost.service;

import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderCostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客订单费用总表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Service
public class RenterOrderCostService{
    @Autowired
    private RenterOrderCostMapper renterOrderCostMapper;

    public void saveRenterOrderCost(RenterOrderCostEntity renterOrderCostEntity){
        int insert = renterOrderCostMapper.insert(renterOrderCostEntity);
    }
}
