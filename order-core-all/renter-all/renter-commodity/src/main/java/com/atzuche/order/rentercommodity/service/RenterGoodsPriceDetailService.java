package com.atzuche.order.rentercommodity.service;

import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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

    public List<RenterGoodsPriceDetailEntity> listRenterGoodsPriceByOrderNo(Long orderNo) {
        return null;
    }
}
