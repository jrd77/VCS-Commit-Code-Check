package com.atzuche.rentercommodity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.rentercommodity.mapper.RenterGoodsPriiceDetailMapper;
import com.atzuche.rentercommodity.entity.RenterGoodsPriiceDetailEntity;



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

    /**
     * 获取租客价格列表
     * @param orderNo 主订单号
     * @return List<RenterGoodsPriiceDetailEntity>
     */
    public List<RenterGoodsPriiceDetailEntity> listRenterGoodsPriceByOrderNo(Long orderNo) {
    	return renterGoodsPriiceDetailMapper.listRenterGoodsPriceByOrderNo(orderNo);
    }
}
