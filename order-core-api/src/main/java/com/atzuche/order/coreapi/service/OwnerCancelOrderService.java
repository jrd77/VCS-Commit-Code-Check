package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 车主取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:22
 */
@Service
public class OwnerCancelOrderService {

    @Autowired
    private CarRentalTimeApiProxyService carRentalTimeApiService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    OrderStatusService orderStatusService;
    @Autowired
    RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired
    OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired
    ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
    @Autowired
    OrderFlowService orderFlowService;
    @Autowired
    OrderCancelReasonService orderCancelReasonService;
    @Autowired
    OwnerOrderFineApplyService ownerOrderFineApplyService;
    @Autowired
    CancelOrderCheckService cancelOrderCheckService;

    /**
     * 车主取消订单处理
     *
     * @param reqContext 请求公共参数
     * @return CancelOrderResDTO 返回数据
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel(LocalDateTime cancelReqTime,CancelOrderReqContext reqContext) {
        //校验
        cancelOrderCheckService.checkOwnerCancelOrder(reqContext);
        //请求参数
        CancelOrderReqDTO cancelOrderReqDTO = reqContext.getCancelOrderReqDTO();
        //获取订单信息
        OrderEntity orderEntity = reqContext.getOrderEntity();
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = reqContext.getOrderStatusEntity();
        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = reqContext.getOwnerOrderEntity();
        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = reqContext.getRenterOrderEntity();
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = reqContext.getRenterGoodsDetailDTO();
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = reqContext.getOwnerCouponEntity();
        //调度判定
        boolean isDispatch =
                carRentalTimeApiService.checkCarDispatch(carRentalTimeApiService.buildCarDispatchReqVO(orderEntity,
                        orderStatusEntity, ownerCouponEntity, 2));

        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();
        cancelOrderResDTO.setOrderNo(cancelOrderReqDTO.getOrderNo());
        cancelOrderResDTO.setIsDispatch(isDispatch);

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(cancelOrderReqDTO.getOrderNo());
        if (isDispatch) {
            //取消进调度
            orderStatusDTO.setStatus(OrderStatusEnum.TO_DISPATCH.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.YES);
            orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.DISPATCH_ING.getCode());

        } else {
            //取消不进调度
            cancelOrderResDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
            cancelOrderResDTO.setOwnerCouponNo(null == ownerCouponEntity ? null : ownerCouponEntity.getCouponId());
            cancelOrderResDTO.setSrvGetFlag(null != renterOrderEntity.getIsGetCar() && renterOrderEntity.getIsGetCar() == OrderConstant.YES);
            cancelOrderResDTO.setSrvReturnFlag(null != renterOrderEntity.getIsReturnCar() && renterOrderEntity.getIsReturnCar() == OrderConstant.YES);

            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.NO);
            orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.NOT_DISPATCH.getCode());
            renterOrderService.updateChildStatusByOrderNo(cancelOrderReqDTO.getOrderNo(), RenterChildStatusEnum.END.getCode());
        }
        //落库
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        orderFlowService.inserOrderStatusChangeProcessInfo(cancelOrderReqDTO.getOrderNo(),
                OrderStatusEnum.from(orderStatusDTO.getStatus()));

        if (null != ownerOrderEntity) {
            ownerOrderService.updateChildStatusByOrderNo(cancelOrderReqDTO.getOrderNo(), OwnerChildStatusEnum.END.getCode());
            ownerOrderService.updateDispatchReasonByOrderNo(cancelOrderReqDTO.getOrderNo(), DispatcherReasonEnum.owner_cancel);
            //取消信息处理(order_cancel_reason)
            OrderCancelReasonEntity orderCancelReasonEntity =
                    buildOrderCancelReasonEntity(cancelOrderReqDTO.getOrderNo(),
                    ownerOrderEntity.getOwnerOrderNo(),
                    cancelOrderReqDTO.getCancelReason());
            orderCancelReasonEntity.setCancelReqTime(cancelReqTime);
            orderCancelReasonService.addOrderCancelReasonRecord(orderCancelReasonEntity);
        }
        //返回信息处理
        cancelOrderResDTO.setCarNo(goodsDetail.getCarNo());
        cancelOrderResDTO.setRentCarPayStatus(orderStatusEntity.getRentCarPayStatus());
        cancelOrderResDTO.setCityCode(Integer.valueOf(orderEntity.getCityCode()));
        cancelOrderResDTO.setRentTime(orderEntity.getExpRentTime());
        cancelOrderResDTO.setRevertTime(orderEntity.getExpRevertTime());
        cancelOrderResDTO.setStatus(orderStatusDTO.getStatus());
        cancelOrderResDTO.setWrongdoer(CancelOrderDutyEnum.CANCEL_ORDER_DUTY_OWNER.getCode());
        return cancelOrderResDTO;
    }

    /**
     * 组装计算取消订单罚金请求参数
     *
     * @param renterOrderEntity     租客订单信息
     * @param renterOrderCostEntity 租客订单费用信息
     * @param carOwnerType          车辆类型
     * @return CancelFineAmtDTO
     */
    private CancelFineAmtDTO buildCancelFineAmtDTO(RenterOrderEntity renterOrderEntity,
                                                   RenterOrderCostEntity renterOrderCostEntity,
                                                   Integer carOwnerType) {
        CancelFineAmtDTO cancelFineAmtDTO = new CancelFineAmtDTO();
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrderEntity.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        costBaseDTO.setMemNo(renterOrderCostEntity.getMemNo());
        costBaseDTO.setStartTime(renterOrderEntity.getExpRentTime());
        costBaseDTO.setEndTime(renterOrderEntity.getExpRevertTime());

        cancelFineAmtDTO.setCostBaseDTO(costBaseDTO);
        cancelFineAmtDTO.setCancelTime(LocalDateTime.now());
        cancelFineAmtDTO.setRentAmt(Math.abs(renterOrderCostEntity.getRentCarAmount()));
        cancelFineAmtDTO.setOwnerType(carOwnerType);
        return cancelFineAmtDTO;
    }


    private OrderCancelReasonEntity buildOrderCancelReasonEntity(String orderNo, String ownerOrderNo,
                                                                 String cancelReason) {
        OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
        orderCancelReasonEntity.setOperateType(CancelOperateTypeEnum.CANCEL_ORDER.getCode());
        orderCancelReasonEntity.setCancelReason(cancelReason);
        orderCancelReasonEntity.setCancelSource(CancelSourceEnum.OWNER.getCode());
        orderCancelReasonEntity.setOrderNo(orderNo);
        orderCancelReasonEntity.setSubOrderNo(ownerOrderNo);
        return orderCancelReasonEntity;
    }



}
