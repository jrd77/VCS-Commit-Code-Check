package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.mapper.OrderStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 主订单表状态
 *
 * @author ZhangBin
 * @date 2019-12-24 16:19:32
 */
@Service
public class OrderStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatusService.class);

    @Resource
    private OrderStatusMapper orderStatusMapper;


    /**
     * 主订单状态信息保存
     *
     * @param orderStatusDTO 主订单状态信息
     * @return int 操作成功记录数
     */
    public int saveOrderStatusInfo(OrderStatusDTO orderStatusDTO) {

        LOGGER.info("Save master order status information. param is, orderStatusDTO:[{}]", JSON.toJSONString(orderStatusDTO));
        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByOrderNo(orderStatusDTO.getOrderNo());
        OrderStatusEntity record = buildOrderStatusEntity(orderStatusDTO);
        int result;
        if (null == orderStatusEntity) {
            result = orderStatusMapper.insertSelective(record);
        } else {
            record.setId(orderStatusEntity.getId());
            result = orderStatusMapper.updateByPrimaryKeySelective(record);
        }
        LOGGER.info("Save master order status information. result is, result:[{}]", result);
        return result;
    }


    /**
     * 组装主订单状态信息
     *
     * @param orderStatusDTO 主订单状态信息
     * @return OrderEntity
     */
    private OrderStatusEntity buildOrderStatusEntity(OrderStatusDTO orderStatusDTO) {
        OrderStatusEntity record = new OrderStatusEntity();
        BeanCopier beanCopier = BeanCopier.create(OrderStatusDTO.class, OrderStatusEntity.class, false);
        beanCopier.copy(orderStatusDTO, record, null);

        record.setCreateOp("");
        record.setUpdateOp("");

        LOGGER.info("Build master order status information. result is, record:[{}]", JSON.toJSONString(record));
        return record;
    }

    public List<String> queryOrderNoByStartTimeAndEndTime(Date startTime, Date endTime) {
        return orderStatusMapper.queryOrderNoByStartTimeAndEndTime(startTime,endTime);
    }
}
