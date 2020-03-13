package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 租客取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:21
 */

@Service
public class RenterCancelOrderService {

    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OrderFlowService orderFlowService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private OrderStatusService orderStatusService;

    /**
     * 租客取消订单处理
     *
     * @param cancelReqTime 取消时间
     * @param reqContext 请求公共参数
     * @return CancelOrderResDTO 返回数据
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel(LocalDateTime cancelReqTime,CancelOrderReqContext reqContext) {
        //请求参数
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();
        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = reqContext.getRenterOrderEntity();
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDTO();
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = reqContext.getOwnerCouponEntity();
        //车主订单信息
        OwnerOrderEntity ownerOrderEntity = reqContext.getOwnerOrderEntity();

        //订单状态更新
        orderStatusService.saveOrderStatusInfo(buildOrderStatusDTO(cancelOrderReqDTO.getOrderNo()));
        renterOrderService.updateChildStatusByOrderNo(cancelOrderReqDTO.getOrderNo(), RenterChildStatusEnum.END.getCode());
        ownerOrderService.updateChildStatusByOrderNo(cancelOrderReqDTO.getOrderNo(), OwnerChildStatusEnum.END.getCode());

        orderFlowService.inserOrderStatusChangeProcessInfo(cancelOrderReqDTO.getOrderNo(), OrderStatusEnum.CLOSED);
        //取消信息处理(order_cancel_reason)
        OrderCancelReasonEntity orderCancelReasonEntity = buildOrderCancelReasonEntity(cancelOrderReqDTO.getOrderNo(),
                renterOrderEntity.getRenterOrderNo(),ownerOrderEntity.getOwnerOrderNo(),
                cancelOrderReqDTO.getCancelReason());
        orderCancelReasonEntity.setCancelReqTime(cancelReqTime);
        if(cancelOrderReqDTO.getConsoleInvoke() && !StringUtils.equals(OrderConstant.SYSTEM_OPERATOR_JOB,
                cancelOrderReqDTO.getOperatorName())) {
            orderCancelReasonEntity.setCancelSource(CancelSourceEnum.INSTEAD_OF_RENTER.getCode());
        }
        orderCancelReasonService.addOrderCancelReasonRecord(orderCancelReasonEntity);

        //返回信息处理
        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();
        cancelOrderResDTO.setOrderNo(cancelOrderReqDTO.getOrderNo());
        cancelOrderResDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        cancelOrderResDTO.setCarNo(goodsDetail.getCarNo());
        cancelOrderResDTO.setIsDispatch(false);
        cancelOrderResDTO.setRentCarPayStatus(orderStatusEntity.getRentCarPayStatus());
        cancelOrderResDTO.setSrvGetFlag(null != renterOrderEntity.getIsGetCar() && renterOrderEntity.getIsGetCar() == OrderConstant.YES);
        cancelOrderResDTO.setSrvReturnFlag(null != renterOrderEntity.getIsReturnCar() && renterOrderEntity.getIsReturnCar() == OrderConstant.YES);
        cancelOrderResDTO.setOwnerCouponNo(null == ownerCouponEntity ? null : ownerCouponEntity.getCouponId());
        cancelOrderResDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
        cancelOrderResDTO.setWrongdoer(CancelOrderDutyEnum.CANCEL_ORDER_DUTY_RENTER.getCode());

        return cancelOrderResDTO;
    }




    private OrderStatusDTO buildOrderStatusDTO(String orderNo) {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
        return orderStatusDTO;
    }

    private OrderCancelReasonEntity buildOrderCancelReasonEntity(String orderNo,
                                                                 String renterOrderNo,
                                                                 String ownerOrderNo,
                                                                 String cancelReason) {
        OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
        orderCancelReasonEntity.setOperateType(CancelOperateTypeEnum.CANCEL_ORDER.getCode());
        orderCancelReasonEntity.setCancelReason(cancelReason);
        orderCancelReasonEntity.setCancelSource(CancelSourceEnum.RENTER.getCode());
        orderCancelReasonEntity.setOrderNo(orderNo);
        orderCancelReasonEntity.setRenterOrderNo(renterOrderNo);
        orderCancelReasonEntity.setOwnerOrderNo(ownerOrderNo);
        return orderCancelReasonEntity;
    }

}
