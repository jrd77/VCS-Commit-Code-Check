package com.atzuche.order.renterpenalty.service;

import com.atzuche.order.renterpenalty.mapper.RenterOrderFineDeatailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客订单罚金明细表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Service
public class RenterOrderFineDeatailService{
    @Autowired
    private RenterOrderFineDeatailMapper renterOrderFineDeatailMapper;


}
