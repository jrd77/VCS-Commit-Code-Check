package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.DispatcherStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.RenterChildStatusEnum;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 调度取消处理
 *
 * @author pengcheng.fu
 * @date 2020/1/17 11:57
 */

@Service
public class OrderDispatchCancelHandleService {


    @Autowired
    OwnerOrderFineApplyHandelService ownerOrderFineApplyHandelService;
    @Autowired
    OrderStatusService orderStatusService;
    @Autowired
    OrderFlowService orderFlowService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OrderCouponService orderCouponService;

    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancelDispatch(String orderNo) {

        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);

        //订单状态更新
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
        orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.DISPATCH_FAIL.getCode());
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        //添加order_flow
        orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.from(orderStatusDTO.getStatus()));
        renterOrderService.updateChildStatusByOrderNo(orderNo, RenterChildStatusEnum.END.getCode());

        //车主取消进调度,罚金后续处理
        ownerOrderFineApplyHandelService.handleFineApplyRecord(orderNo, DispatcherStatusEnum.DISPATCH_FAIL);

        //返回信息处理
        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();
        cancelOrderResDTO.setIsReturnDisCoupon(true);
        cancelOrderResDTO.setIsReturnOwnerCoupon(true);
        cancelOrderResDTO.setOwnerCouponNo(null == ownerCouponEntity ? null : ownerCouponEntity.getCouponId());
        cancelOrderResDTO.setIsRefund(true);
        cancelOrderResDTO.setRentCarPayStatus(orderStatusEntity.getRentCarPayStatus());
        cancelOrderResDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        cancelOrderResDTO.setSrvGetFlag(null != renterOrderEntity.getIsGetCar() && renterOrderEntity.getIsGetCar() == 1);
        cancelOrderResDTO.setSrvReturnFlag(null != renterOrderEntity.getIsReturnCar() && renterOrderEntity.getIsReturnCar() == 1);
        return cancelOrderResDTO;
    }
}
