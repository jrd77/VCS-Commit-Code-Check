package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterOrderFineDeatailMapper;
import com.atzuche.order.entity.RenterOrderFineDeatailEntity;



/**
 * 租客订单罚金明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:10:05
 */
@Service
public class RenterOrderFineDeatailService{
    @Autowired
    private RenterOrderFineDeatailMapper renterOrderFineDeatailMapper;


}
