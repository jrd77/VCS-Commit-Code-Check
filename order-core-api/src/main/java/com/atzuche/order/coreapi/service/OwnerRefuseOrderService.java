package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 车主拒绝订单
 *
 * @author pengcheng.fu
 * @date 2020/1/9 16:57
 */

@Service
public class OwnerRefuseOrderService {

    private static Logger logger = LoggerFactory.getLogger(OwnerRefuseOrderService.class);

    @Autowired
    CarRentalTimeApiService carRentalTimeApiService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    OrderStatusService orderStatusService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderCouponService orderCouponService;
    @Autowired
    OrderCancelReasonService orderCancelReasonService;
    @Autowired
    CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    StockService stockService;
    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    OrderSettleService orderSettleService;
    @Autowired
    OrderFlowService orderFlowService;



    /**
     * 车主拒单
     *
     * @param reqVO 请求参数
     */
    public void refuse(RefuseOrderReqVO reqVO) {
        //TODO:车主拒绝前置校验


        //判断是都进入调度
        //获取订单信息
        OrderEntity orderEntity = orderService.getOrderEntity(reqVO.getOrderNo());
        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(reqVO.getOrderNo());
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(reqVO.getOrderNo(),
                renterOrderEntity.getRenterOrderNo());
        boolean isDispatch =
                carRentalTimeApiService.checkCarDispatch(carRentalTimeApiService.buildCarDispatchReqVO(orderEntity,
                        orderStatusEntity, ownerCouponEntity));

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(reqVO.getOrderNo());
        if (isDispatch) {
            //进入调度
            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.TO_DISPATCH.getStatus());
            orderStatusDTO.setIsDispatch(1);
            orderStatusDTO.setDispatchStatus(1);
        } else {
            //不进调度
            orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
            renterOrderService.updateRenterOrderChildStatus(renterOrderEntity.getId(),
                    RenterChildStatusEnum.END.getCode());

            //撤销优惠券
            //退还优惠券(平台券+送取服务券)
            couponAndCoinHandleService.undoPlatformCoupon(reqVO.getOrderNo());
            couponAndCoinHandleService.undoPlatformCoupon(reqVO.getOrderNo());
            //退还车主券
            if(null != ownerCouponEntity) {
                String recover = null == orderStatusEntity.getRentCarPayStatus() || orderStatusEntity.getRentCarPayStatus() == 0 ? "1" : "0";
                couponAndCoinHandleService.undoOwnerCoupon(reqVO.getOrderNo(), ownerCouponEntity.getCouponId(), recover);
            }
            //通知收银台退还凹凸币和钱包
            orderSettleService.settleOrderCancel(reqVO.getOrderNo());
        }


        //落库
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        //添加order_flow记录
        orderFlowService.inserOrderStatusChangeProcessInfo(reqVO.getOrderNo(),
                OrderStatusEnum.from(orderStatusDTO.getStatus()));
        ownerOrderService.updateOwnerOrderChildStatus(ownerOrderEntity.getId(), OwnerChildStatusEnum.END.getCode());
        //取消信息处理(order_cancel_reason)
        orderCancelReasonService.addOrderCancelReasonRecord(buildOrderCancelReasonEntity(reqVO.getOrderNo(), ownerOrderEntity.getOwnerOrderNo()));
        //扣除库存
        stockService.releaseCarStock(reqVO.getOrderNo(), goodsDetail.getCarNo());

        //TODO:发送车主拒绝事件
    }


    private OrderCancelReasonEntity buildOrderCancelReasonEntity(String orderNo, String ownerOrderNo) {
        OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
        orderCancelReasonEntity.setOperateType(CancelOperateTypeEnum.REFUSE_ORDER.getCode());
        orderCancelReasonEntity.setCancelReason("车主拒单");
        orderCancelReasonEntity.setCancelSource(CancelSourceEnum.OWNER.getCode());
        orderCancelReasonEntity.setOrderNo(orderNo);
        orderCancelReasonEntity.setSubOrderNo(ownerOrderNo);
        return orderCancelReasonEntity;
    }


}
