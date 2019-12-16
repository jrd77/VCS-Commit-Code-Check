package com.atzuche.order.rentercommodity.service;

import com.atzuche.order.rentercommodity.mapper.RenterGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
