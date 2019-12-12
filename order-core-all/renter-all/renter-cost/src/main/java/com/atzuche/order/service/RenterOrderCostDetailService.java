package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterOrderCostDetailMapper;
import com.atzuche.order.entity.RenterOrderCostDetailEntity;



/**
 * 租客费用明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:10:05
 */
@Service
public class RenterOrderCostDetailService{
    @Autowired
    private RenterOrderCostDetailMapper renterOrderCostDetailMapper;


}
