package com.atzuche.order.rentercost.service;

import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderCostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客订单费用总表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:35:56
 */
@Service
public class RenterOrderCostService{
    @Autowired
    private RenterOrderCostMapper renterOrderCostMapper;

    public Integer saveRenterOrderCost(RenterOrderCostEntity renterOrderCostEntity){
        return renterOrderCostMapper.insert(renterOrderCostEntity);
    }

    public Integer updateRenterOrderCost(RenterOrderCostEntity renterOrderCostEntity){
        return renterOrderCostMapper.updateByPrimaryKeySelective(renterOrderCostEntity);
    }
    
    /**
     * 根据主订单号和租客订单号获取租客费用信息
     *
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return RenterOrderCostEntity
     */
    public RenterOrderCostEntity getByOrderNoAndRenterNo(String orderNo, String renterOrderNo) {

        return renterOrderCostMapper.selectByOrderNoAndRenterNo(orderNo, renterOrderNo);
    }



}
