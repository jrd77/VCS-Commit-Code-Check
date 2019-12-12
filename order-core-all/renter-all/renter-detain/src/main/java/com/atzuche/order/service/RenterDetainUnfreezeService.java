package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterDetainUnfreezeMapper;
import com.atzuche.order.entity.RenterDetainUnfreezeEntity;



/**
 * 暂扣解冻表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 */
@Service
public class RenterDetainUnfreezeService{
    @Autowired
    private RenterDetainUnfreezeMapper renterDetainUnfreezeMapper;


}
