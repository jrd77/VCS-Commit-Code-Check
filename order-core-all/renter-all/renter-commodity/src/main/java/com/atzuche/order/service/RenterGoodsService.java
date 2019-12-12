package com.atzuche.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterGoodsMapper;
import com.atzuche.order.entity.RenterGoodsEntity;



/**
 * 租客商品概览表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsService{
    @Autowired
    private RenterGoodsMapper renterGoodsMapper;


}
