package com.atzuche.order.ownercost.service;


import com.atzuche.order.ownercost.mapper.OwnerOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.ownercost.entity.*;

@Service
public class OwnerOrderService {
    @Autowired
    private OwnerOrderMapper ownerOrderMapper;

    /*
     * @Author ZhangBin
     * @Date 2019/12/25 10:08
     * @Description: 查询有效的子订单
     *
     **/
    public OwnerOrderEntity getOwnerOrderByOrderNoAndIsEffective(String orderNo){
        return ownerOrderMapper.getOwnerOrderByOrderNoAndIsEffective(orderNo);
    }
    
    /**
     * 根据id把上笔车主子单置为无效
     * @param id
     * @return Integer
     */
    public Integer updateOwnerOrderInvalidById(Integer id) {
    	return ownerOrderMapper.updateOwnerOrderInvalidById(id);
    }
    
    /**
     * 保存车主子订单
     * @param ownerOrderEntity
     * @return Integer
     */
    public Integer saveOwnerOrder(OwnerOrderEntity ownerOrderEntity) {
    	return ownerOrderMapper.insertSelective(ownerOrderEntity);
    }



}
