package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.mapper.OrderCarTrusteeshipMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;


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
    public Integer insertOrderCarTrusteeship(OrderCarTrusteeshipEntity orderCarTrusteeshipEntity) {
        return orderCarTrusteeshipMapper.insertSelective(orderCarTrusteeshipEntity);
    }

    /**
     * 修改托管信息
     * @param orderCarTrusteeshipEntity
     * @return
     */
    public Integer updateOrderCarTrusteeship(OrderCarTrusteeshipEntity orderCarTrusteeshipEntity) {
        return orderCarTrusteeshipMapper.updateByPrimaryKeySelective(orderCarTrusteeshipEntity);
    }


    /**
     * 删除托管信息
     * @param orderNo
     * @param carNo
     * @return
     */
    public Integer deleteOrderCarTrusteeship(String orderNo, String carNo) {
        OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = orderCarTrusteeshipMapper.selectObjectByOrderNoAndType(orderNo, carNo);
        if (Objects.isNull(orderCarTrusteeshipEntity)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到托管车信息");
        }
        orderCarTrusteeshipEntity.setIsDelete(1);
        return orderCarTrusteeshipMapper.updateByPrimaryKeySelective(orderCarTrusteeshipEntity);
    }

    /**
     * 获取托管车信息
     * @param orderNo
     * @param carNo
     * @return
     */
    public OrderCarTrusteeshipEntity selectObjectByOrderNoAndCar(String orderNo, String carNo) {
        OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = orderCarTrusteeshipMapper.selectObjectByOrderNoAndType(orderNo, carNo);
        return orderCarTrusteeshipEntity;
    }









}
