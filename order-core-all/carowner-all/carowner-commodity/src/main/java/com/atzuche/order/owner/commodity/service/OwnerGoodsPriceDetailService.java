package com.atzuche.order.owner.commodity.service;

import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsPriceDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 车主端商品概览价格明细表
 *
 * @author ZhangBin
 * @date 2019-12-17 20:30:11
 */
@Service
public class OwnerGoodsPriceDetailService{
    @Autowired
    private OwnerGoodsPriceDetailMapper ownerGoodsPriceDetailMapper;

    /*
     * @Author ZhangBin
     * @Date 2020/1/16 20:13
     * @Description: 通过车主子订单号
     *
     **/
    public List<OwnerGoodsPriceDetailEntity> getByOwnerOrderNo(String ownerOrderNo){
        return ownerGoodsPriceDetailMapper.getByOwnerOrderNo(ownerOrderNo);
    }
}
