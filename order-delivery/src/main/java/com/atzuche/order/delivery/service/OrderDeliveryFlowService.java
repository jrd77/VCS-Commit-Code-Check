package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.entity.OrderDeliveryFlowEntity;
import com.atzuche.order.delivery.mapper.OrderDeliveryFlowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 发送仁云信息表
 *
 * @author 胡春林
 */
@Service
public class OrderDeliveryFlowService {
    @Resource
    private OrderDeliveryFlowMapper orderDeliveryFlowMapper;

    /**
     * 新增配送订单
     * @param orderDeliveryFlowEntity
     */
    public Integer insertOrderDeliveryFlow(OrderDeliveryFlowEntity orderDeliveryFlowEntity) {
        return orderDeliveryFlowMapper.insert(orderDeliveryFlowEntity);
    }

    /**
     * 根据订单号和配送类型获取仁云数据
     * @param renterOrderNo
     * @return
     */
    public List<OrderDeliveryFlowEntity> selectOrderDeliveryFlowByOrderNo(String renterOrderNo) {
        return orderDeliveryFlowMapper.selectOrderDeliveryByRenterOrderNo(renterOrderNo);
    }

    public OrderDeliveryFlowEntity selectOrderDeliveryFlowByOrderNo(String orderNo, String serviceType) {
        return orderDeliveryFlowMapper.selectOrderDeliveryByOrderNo(orderNo, serviceType);
    }

    public Integer updateOrderDeliveryFlow(OrderDeliveryFlowEntity orderDeliveryFlowEntity) {
        return orderDeliveryFlowMapper.updateByPrimaryKey(orderDeliveryFlowEntity);
    }


}
