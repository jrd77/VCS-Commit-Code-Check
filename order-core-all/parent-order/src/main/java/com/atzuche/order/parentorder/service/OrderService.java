package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 主订单表
 *
 * @author ZhangBin
 * @date 2019-12-24 16:19:33
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Resource
    private OrderMapper orderMapper;


    /**
     * 主订单信息保存
     *
     * @param orderDTO 主订单信息
     * @return int 操作成功记录数
     */
    public int saveOrderInfo(OrderDTO orderDTO) {
        LOGGER.info("Save master order information. param is, orderDTO:[{}]", JSON.toJSONString(orderDTO));
        OrderEntity orderEntity = orderMapper.selectByOrderNo(orderDTO.getOrderNo());
        OrderEntity record = buildOrderEntity(orderDTO);
        int result;
        if (null == orderEntity) {
            result = orderMapper.insertSelective(record);
        } else {
            record.setId(orderEntity.getId());
            result = orderMapper.updateByPrimaryKeySelective(record);
        }
        LOGGER.info("Save master order information. result is, result:[{}]", result);
        return result;
    }


    /**
     * 组装主订单信息
     *
     * @param orderDTO 主订单信息
     * @return OrderEntity
     */
    private OrderEntity buildOrderEntity(OrderDTO orderDTO) {
        OrderEntity record = new OrderEntity();
        BeanCopier beanCopier = BeanCopier.create(OrderDTO.class, OrderEntity.class, false);
        beanCopier.copy(orderDTO, record, null);

        record.setCreateOp("");
        record.setUpdateOp("");

        LOGGER.info("Build master order information. result is, record:[{}]", JSON.toJSONString(record));
        return record;
    }

}
