package com.atzuche.order.renterorder.mapper;

import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单券表
 *
 * @author ZhangBin
 * @date 2019-12-25 15:32:31
 */
@Mapper
public interface OrderCouponMapper {

    OrderCouponEntity selectByPrimaryKey(Integer id);

    int insert(OrderCouponEntity record);

    int insertSelective(OrderCouponEntity record);

    int updateByPrimaryKey(OrderCouponEntity record);

    int updateByPrimaryKeySelective(OrderCouponEntity record);

    List<OrderCouponEntity> listOrderCouponByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);
    
    List<OrderCouponEntity> listOrderCouponByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询指定订单的优惠券信息
     *
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @param couponType 优惠券类型:CouponTypeEnum
     * @return OrderCouponEntity
     */
    OrderCouponEntity selectByOrderNoAndRenterOrderNo(@Param("orderNo") String orderNo, @Param(
            "renterOrderNo") String renterOrderNo,@Param("couponType") Integer couponType);

    /**
     * 获取车主券码
     *
     * @param orderNo 订单号
     * @param couponType 优惠券类型
     * @return String
     */
    String selectCouponNoByOrderNo(@Param("orderNo") String orderNo,@Param("couponType") int couponType);

}
