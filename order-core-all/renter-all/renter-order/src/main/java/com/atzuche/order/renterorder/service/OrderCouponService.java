package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.mapper.OrderCouponMapper;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


/**
 * 订单券表
 *
 * @author ZhangBin
 * @date 2019-12-25 15:32:31
 */
@Service
public class OrderCouponService {

    @Resource
    private OrderCouponMapper orderCouponMapper;


    /**
     * 获取租客子单使用的优惠券
     *
     * @param renterOrderNo
     * @return List<OrderCouponEntity>
     */
    public List<OrderCouponEntity> listOrderCouponByRenterOrderNo(String renterOrderNo) {
        return orderCouponMapper.listOrderCouponByRenterOrderNo(renterOrderNo);
    }

    /**
     * 批量插入订单优惠券信息
     *
     * @param orderCouponList 订单优惠券信息
     */
    public void insertBatch(List<OrderCouponDTO> orderCouponList) {
        if (!CollectionUtils.isEmpty(orderCouponList)) {
            BeanCopier beanCopier = BeanCopier.create(OrderCouponDTO.class, OrderCouponEntity.class, false);
            for (OrderCouponDTO oc : orderCouponList) {
                OrderCouponEntity record = new OrderCouponEntity();
                beanCopier.copy(oc, record, null);
                orderCouponMapper.insertSelective(record);
            }
        }
    }

    /**
     * 获取订单使用的车主券信息
     *
     * @param orderNo       主订单号
     * @param renterOrderNo 租客订单号
     * @return OrderCouponEntity
     */
    public OrderCouponEntity getOwnerCouponByOrderNoAndRenterOrderNo(String orderNo, String renterOrderNo) {
        return orderCouponMapper.selectByOrderNoAndRenterOrderNo(orderNo, renterOrderNo,
                CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());

    }


}
