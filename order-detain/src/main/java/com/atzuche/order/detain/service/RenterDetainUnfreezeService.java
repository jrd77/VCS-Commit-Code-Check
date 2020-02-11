package com.atzuche.order.detain.service;

import com.atzuche.order.detain.mapper.RenterDetainUnfreezeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 暂扣解冻表
 *
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 */
@Service
public class RenterDetainUnfreezeService{
    @Autowired
    private RenterDetainUnfreezeMapper renterDetainUnfreezeMapper;


}
