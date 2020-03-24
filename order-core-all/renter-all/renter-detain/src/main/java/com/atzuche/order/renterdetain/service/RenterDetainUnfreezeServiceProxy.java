package com.atzuche.order.renterdetain.service;

import com.atzuche.order.renterdetain.mapper.RenterDetainUnfreezeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 暂扣解冻表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:13:12
 */
@Service
public class RenterDetainUnfreezeServiceProxy{
    @Autowired
    private RenterDetainUnfreezeMapper renterDetainUnfreezeMapper;


}
