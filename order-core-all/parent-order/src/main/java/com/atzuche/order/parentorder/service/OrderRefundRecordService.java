package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import com.atzuche.order.parentorder.mapper.OrderRefundRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 订单取消退款延时记录处理
 *
 * @author pengcheng.fu
 * @date 2020-03-16 13:59:58
 */
@Service
public class OrderRefundRecordService {

    private static Logger logger = LoggerFactory.getLogger(OrderStatusService.class);

    @Autowired
    private OrderRefundRecordMapper orderRefundRecordMapper;


    public int saveOrderRefundRecord(OrderRefundRecordEntity entity) {
        if (null == entity) {
            logger.warn("Order refund record is empty.");
            return 0;
        }
        logger.info("Save order refund record. entity:[{}]", JSON.toJSONString(entity));
        OrderRefundRecordEntity record = orderRefundRecordMapper.selectByOrderNo(entity.getOrderNo());
        if (null == record) {
            return orderRefundRecordMapper.insertSelective(entity);
        } else {
            entity.setId(record.getId());
            return orderRefundRecordMapper.updateByPrimaryKeySelective(entity);
        }
    }


    public OrderRefundRecordEntity getByOrderNo(String orderNo) {
        return orderRefundRecordMapper.selectByOrderNo(orderNo);
    }


    /**
     * 数据封装
     *
     * @param orderNo       订单号
     * @param renterOrderNo 租客订单号
     * @param ownerOrderNo  车主订单号
     * @param fineAmt       租客罚金/车主收益
     * @return OrderRefundRecordEntity 订单取消退款延时记录
     */
    public OrderRefundRecordEntity orderRefundDataConvert(String orderNo, String renterOrderNo, String ownerOrderNo,
                                                          Integer fineAmt) {
        if (fineAmt == null || fineAmt == 0) {
            return null;
        }

        OrderRefundRecordEntity orderRefundRecordEntity = new OrderRefundRecordEntity();
        orderRefundRecordEntity.setOrderNo(orderNo);
        orderRefundRecordEntity.setRenterOrderNo(renterOrderNo);
        orderRefundRecordEntity.setOwnerOrderNo(ownerOrderNo);
        orderRefundRecordEntity.setRenterDeduct(Math.abs(fineAmt));
        orderRefundRecordEntity.setOwnerRealIncome(Math.abs(fineAmt));

        return orderRefundRecordEntity;
    }
}
