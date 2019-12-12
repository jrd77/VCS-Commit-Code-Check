package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterOrderCostMapper;
import com.atzuche.order.entity.RenterOrderCostEntity;



/**
 * 租客订单费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:10:06
 */
@Service
public class RenterOrderCostService{
    @Autowired
    private RenterOrderCostMapper renterOrderCostMapper;


}
