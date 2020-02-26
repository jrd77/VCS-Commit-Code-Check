package com.atzuche.order.renterorder.service;

import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.mapper.OrderCouponMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(OrderCouponService.class);

    @Resource
    private OrderCouponMapper orderCouponMapper;
    @Resource
    private PlatformCouponService platformCouponService;
    @Resource
    private OwnerDiscountCouponService ownerDiscountCouponService;


    /**
     * 获取租客子单使用的优惠券
     *
     * @param renterOrderNo
     * @return List<OrderCouponEntity>
     */
    public List<OrderCouponEntity> listOrderCouponByRenterOrderNo(String renterOrderNo) {
        return orderCouponMapper.listOrderCouponByRenterOrderNo(renterOrderNo);
    }

    public List<OrderCouponEntity> listOrderCouponByOrderNo(String orderNo) {
        return orderCouponMapper.listOrderCouponByOrderNo(orderNo);
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


    /**
     * 结算后退还优惠券操作
     *
     * @param orderNo               订单号
     * @param isUndoCoupon          是否撤销平台优惠券
     * @param isUndoGetCarFeeCoupon 是否撤销送取服务券
     * @param isUndoOwnerCoupon     是否撤销车主券
     */
    public void settleUndoCoupon(String orderNo, boolean isUndoCoupon, boolean isUndoGetCarFeeCoupon,
                                 boolean isUndoOwnerCoupon) {

        logger.info("Cancel coupon operation after settlement. param is,orderNo:[{}],isUndoCoupon:[{}]," +
                "isUndoGetCarFeeCoupon:[{}],isUndoOwnerCoupon:[{}]", orderNo, isUndoCoupon, isUndoGetCarFeeCoupon, isUndoOwnerCoupon);

        String couponNo;
        //撤销平台优惠券
        if (isUndoCoupon) {
            couponNo = orderCouponMapper.selectCouponNoByOrderNo(orderNo,
                    CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
            if (StringUtils.isNotBlank(couponNo)) {
                platformCouponService.cancelPlatformCoupon(orderNo);
            }
        }

        //撤销送取服务券
        if (isUndoGetCarFeeCoupon) {
            couponNo = orderCouponMapper.selectCouponNoByOrderNo(orderNo,
                    CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
            if (StringUtils.isNotBlank(couponNo)) {
                platformCouponService.cancelGetCarFeeCoupon(orderNo);
            }
        }

        //撤销车主券
        if (isUndoOwnerCoupon) {
            couponNo = orderCouponMapper.selectCouponNoByOrderNo(orderNo,
                    CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
            if (StringUtils.isNotBlank(couponNo)) {
                ownerDiscountCouponService.undoCoupon(orderNo, couponNo, "0");
            }
        }

    }


}
