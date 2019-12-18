package com.atzuche.order.rentercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderFineDeatailMapper;


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


    /**
     * 获取罚金明细列表
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return List<RenterOrderFineDeatailEntity>
     */
    public List<RenterOrderFineDeatailEntity> listRenterOrderFineDeatail(String orderNo, String renterOrderNo) {
    	return renterOrderFineDeatailMapper.listRenterOrderFineDeatail(orderNo, renterOrderNo);
    }
    
    /**
     * 保存罚金记录
     * @param renterOrderFineDeatailEntity 罚金明细
     * @return Integer
     */
    public Integer saveRenterOrderFineDeatail(RenterOrderFineDeatailEntity renterOrderFineDeatailEntity) {
    	return renterOrderFineDeatailMapper.saveRenterOrderFineDeatail(renterOrderFineDeatailEntity);
    }
    
}
