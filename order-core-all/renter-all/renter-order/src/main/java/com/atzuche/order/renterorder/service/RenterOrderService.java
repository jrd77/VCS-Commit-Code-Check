package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 租客订单子表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Service
public class RenterOrderService{
    @Autowired
    private RenterOrderMapper renterOrderMapper;


    public List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(String orderNo) {
        return renterOrderMapper.listAgreeRenterOrderByOrderNo(orderNo);
    }
    
    /**
     * 获取有效的租客子单
     * @param orderNo 主订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByOrderNoAndIsEffective(String orderNo) {
    	return renterOrderMapper.getRenterOrderByOrderNoAndIsEffective(orderNo);
    }
    
    
    /**
     * 获取租客子单根据租客子单号
     * @param renterOrderNo 租客子订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByRenterOrderNo(String renterOrderNo) {
    	return renterOrderMapper.getRenterOrderByRenterOrderNo(renterOrderNo);
    }
    
    /**
     * 修改租客子订单是否有效状态
     * @param id
     * @param effectiveFlag
     * @return Integer
     */
    public Integer updateRenterOrderEffective(Integer id, Integer effectiveFlag) {
    	return renterOrderMapper.updateRenterOrderEffective(id, effectiveFlag);
    }
    
    /**
     * 保存租客子订单
     * @param renterOrderEntity
     * @return Integer
     */
    public Integer saveRenterOrder(RenterOrderEntity renterOrderEntity) {
    	return renterOrderMapper.insertSelective(renterOrderEntity);
    }

}
