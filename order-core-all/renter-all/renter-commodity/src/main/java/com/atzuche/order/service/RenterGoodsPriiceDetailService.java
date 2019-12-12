package com.atzuche.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterGoodsPriiceDetailMapper;
import com.atzuche.order.entity.RenterGoodsPriiceDetailEntity;



/**
 * 商品概览价格明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsPriiceDetailService{
    @Autowired
    private RenterGoodsPriiceDetailMapper renterGoodsPriiceDetailMapper;


}
