package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.coreapi.submitOrder.exception.CancelOrderCheckException;
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
import com.autoyol.commons.web.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 平台取消
 *
 * @author pengcheng.fu
 * @date 2020/1/16 11:38
 */
@Service
public class PlatformCancelOrderHandleService {

    private static Logger logger = LoggerFactory.getLogger(PlatformCancelOrderHandleService.class);

    @Autowired
    private CarRentalTimeApiProxyService carRentalTimeApiService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderCouponService orderCouponService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OrderFlowService orderFlowService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;


    /**
     * 取消
     *
     * @param orderNo          主订单号
     * @param operator         操作人
     * @param cancelReasonEnum 取消原因
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel(String orderNo, String operator, PlatformCancelReasonEnum cancelReasonEnum) {
        //校验
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(null != orderStatusEntity) {
            //已结束的订单不能取消
            if (null != orderStatusEntity.getStatus()
                    && (OrderStatusEnum.CLOSED.getStatus() == orderStatusEntity.getStatus() || OrderStatusEnum.TO_SETTLE.getStatus() <= orderStatusEntity.getStatus())) {
                throw new CancelOrderCheckException(ErrorCode.TRANS_CANCEL_DUPLICATE);
            }
        } else {
            throw new CancelOrderCheckException(ErrorCode.ORDER_NOT_EXIST);
        }
        //获取订单信息
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        //后台取消进调度判定逻辑
        boolean isDispatch =
                carRentalTimeApiService.checkCarDispatch(carRentalTimeApiService.buildCarDispatchReqVO(orderEntity,
                        orderStatusEntity, ownerCouponEntity,null));

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);

        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();
        cancelOrderResDTO.setIsDispatch(isDispatch);
        if(isDispatch) {
            //进调度
            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.TO_DISPATCH.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.YES);
            orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.DISPATCH_ING.getCode());
        } else {
            //不进调度
            cancelOrderResDTO.setOwnerCouponNo(null == ownerCouponEntity ? null : ownerCouponEntity.getCouponId());
            cancelOrderResDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
            cancelOrderResDTO.setSrvGetFlag(null != renterOrderEntity.getIsGetCar() && renterOrderEntity.getIsGetCar() == 1);
            cancelOrderResDTO.setSrvReturnFlag(null != renterOrderEntity.getIsReturnCar() && renterOrderEntity.getIsReturnCar() == 1);

            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.NO);
            orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.NOT_DISPATCH.getCode());
            renterOrderService.updateChildStatusByOrderNo(orderNo, RenterChildStatusEnum.END.getCode());
        }
        //订单状态变更处理
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.from(orderStatusDTO.getStatus()));
        ownerOrderService.updateChildStatusByOrderNo(orderNo, OwnerChildStatusEnum.END.getCode());
        //取消信息处理(order_cancel_reason)
        OrderCancelReasonEntity orderCancelReasonEntity = buildOrderCancelReasonEntity(orderNo,
                cancelReasonEnum.getCode()+":"+cancelReasonEnum.getName());
        orderCancelReasonEntity.setCancelReqTime(LocalDateTime.now());
        orderCancelReasonEntity.setAppealFlag(OrderConstant.NO);
        orderCancelReasonEntity.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        orderCancelReasonEntity.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
        orderCancelReasonEntity.setCreateOp(operator);
        orderCancelReasonEntity.setUpdateOp(operator);
        orderCancelReasonService.addOrderCancelReasonRecord(orderCancelReasonEntity);

        cancelOrderResDTO.setCarNo(goodsDetail.getCarNo());
        cancelOrderResDTO.setRentCarPayStatus(orderStatusEntity.getRentCarPayStatus());
        cancelOrderResDTO.setStatus(orderStatusDTO.getStatus());

        return cancelOrderResDTO;
    }

    private OrderCancelReasonEntity buildOrderCancelReasonEntity(String orderNo, String cancelReason) {
        OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
        orderCancelReasonEntity.setOperateType(CancelOperateTypeEnum.CANCEL_ORDER.getCode());
        orderCancelReasonEntity.setCancelReason(cancelReason);
        orderCancelReasonEntity.setCancelSource(CancelSourceEnum.PLATFORM.getCode());
        orderCancelReasonEntity.setOrderNo(orderNo);
        orderCancelReasonEntity.setDutySource(CancelOrderDutyEnum.CANCEL_ORDER_DUTY_PLATFORM.getCode());
        return orderCancelReasonEntity;
    }

}
