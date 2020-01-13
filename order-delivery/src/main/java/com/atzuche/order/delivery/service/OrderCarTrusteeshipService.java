package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.mapper.OrderCarTrusteeshipMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 托管车信息表
 * @author 胡春林
 */
@Service
public class OrderCarTrusteeshipService{

    @Resource
    private OrderCarTrusteeshipMapper orderCarTrusteeshipMapper;

    /**
     * 新增托管信息
     * @param orderCarTrusteeshipEntity
     * @return
     */
    public Integer insertOrderCarTrusteeship(OrderCarTrusteeshipEntity orderCarTrusteeshipEntity){
       return orderCarTrusteeshipMapper.insertSelective(orderCarTrusteeshipEntity);
    }


}
