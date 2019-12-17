package com.atzuche.order.rentercommodity.service;

import java.util.List;

import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 商品概览价格明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsPriceDetailService {
    @Autowired
    private RenterGoodsPriceDetailMapper renterGoodsPriceDetailMapper;

    /**
     * 获取租客价格列表
     * @param orderNo 主订单号
     * @return List<RenterGoodsPriiceDetailEntity>
     */
    public List<RenterGoodsPriceDetailEntity> listRenterGoodsPriceByOrderNo(Long orderNo) {
    	return null;
    }






}
