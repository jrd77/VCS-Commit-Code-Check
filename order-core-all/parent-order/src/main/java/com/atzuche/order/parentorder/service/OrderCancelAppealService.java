package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.parentorder.entity.OrderCancelAppealEntity;
import com.atzuche.order.parentorder.mapper.OrderCancelAppealMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 订单取消申诉
 *
 * @author pengcheng.fu
 * @date 2020-03-02 11:10:10
 */
@Service
public class OrderCancelAppealService {

    private static Logger logger = LoggerFactory.getLogger(OrderCancelAppealService.class);

    @Autowired
    private OrderCancelAppealMapper orderCancelAppealMapper;


    /**
     * 保存申诉信息
     *
     * @param record 申诉信息
     * @return 成功记录
     */
    public int saveOrderCancelAppeal(OrderCancelAppealEntity record) {
        if (null == record) {
            logger.warn("Save order cancel appeal record is empty.");
            return 0;
        }

        OrderCancelAppealEntity orderCancelAppealEntity = orderCancelAppealMapper.selectByOrderNo(record.getOrderNo());
        int result;
        if (null == orderCancelAppealEntity) {
            logger.info("Insert order cancel appeal record is:[{}]", JSON.toJSONString(record));
            result = orderCancelAppealMapper.insertSelective(record);
        } else {
            record.setId(orderCancelAppealEntity.getId());
            logger.info("Update order cancel appeal record is:[{}]", JSON.toJSONString(record));
            result = orderCancelAppealMapper.updateByPrimaryKeySelective(record);
        }
        return result;
    }

    public int addOrderCancelAppeal(OrderCancelAppealEntity record) {

        if (null == record) {
            logger.warn("Add order cancel appeal record is empty.");
            return 0;
        }

        return orderCancelAppealMapper.insertSelective(record);
    }


    public int updateOrderCancelAppeal(OrderCancelAppealEntity record) {
        if (null == record) {
            logger.warn("Update order cancel appeal record is empty.");
            return 0;
        }

        return orderCancelAppealMapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 根据订单号获取申诉信息
     *
     * @param orderNo 订单号
     * @return OrderCancelAppealEntity 申诉信息
     */
    public OrderCancelAppealEntity selectByOrderNo(String orderNo) {
        return orderCancelAppealMapper.selectByOrderNo(orderNo);
    }

}
