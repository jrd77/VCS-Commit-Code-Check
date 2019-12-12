package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterOrderMapper;
import com.atzuche.order.entity.RenterOrderEntity;



/**
 * 租客订单子表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:18:40
 */
@Service
public class RenterOrderService{
    @Autowired
    private RenterOrderMapper renterOrderMapper;


}
