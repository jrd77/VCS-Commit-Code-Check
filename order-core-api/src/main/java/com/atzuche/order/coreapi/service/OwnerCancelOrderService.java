package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineApplyEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderFineApplyService;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
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
    OrderCouponService orderCouponService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    RenterOrderCostService renterOrderCostService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    OwnerGoodsService ownerGoodsService;
    @Autowired
    OrderStatusService orderStatusService;
    @Autowired
    OrderService orderService;
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
     * 取消处理
     *
     * @param orderNo      主订单号
     * @param cancelReason 取消原因
     * @return CancelOrderResDTO 返回信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel(String orderNo, String cancelReason, boolean isConsoleInvoke) {
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        //获取车主订单商品信息
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
        //校验
        cancelOrderCheckService.checkOwnerCancelOrder(orderStatusEntity,ownerGoodsDetail.getCarOwnerType(),isConsoleInvoke);
        
        //获取订单信息
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        //获取租客订单费用明细
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //调度判定
        boolean isDispatch =
                carRentalTimeApiService.checkCarDispatch(carRentalTimeApiService.buildCarDispatchReqVO(orderEntity,
                orderStatusEntity, ownerCouponEntity,2));


        //车主罚金处理
        CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                renterOrderCostEntity, goodsDetail.getCarOwnerType());
        int penalty = orderStatusEntity.getRentCarPayStatus() == OrderConstant.YES ? renterOrderFineDeatailService.calCancelFine(cancelFineAmt) : 0;
        //罚车主补贴给平台(保险费)
        if(orderStatusEntity.getStatus() >= OrderStatusEnum.TO_RETURN_CAR.getStatus() && orderStatusEntity.getStatus() != OrderStatusEnum.CLOSED.getStatus()) {
            OwnerOrderFineDeatailEntity ownerOrderFineDeatailEntityTwo =
                    ownerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), Math.abs(renterOrderCostEntity.getBasicEnsureAmount()),
                            FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
            ownerOrderFineDeatailEntityTwo.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
            ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDeatailEntityTwo);
        }

        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        if (isDispatch) {
            //取消进调度
            cancelOrderResDTO.setIsReturnDisCoupon(false);
            cancelOrderResDTO.setIsReturnOwnerCoupon(false);
            cancelOrderResDTO.setIsRefund(false);
            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.TO_DISPATCH.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.YES);
            orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.DISPATCH_ING.getCode());

            //调度后处理
            ownerOrderFineApplyService.addFineApplyRecord(buildOwnerOrderFineApplyEntity(orderNo,
                    ownerOrderEntity.getOwnerOrderNo(),Integer.valueOf(ownerOrderEntity.getMemNo()),penalty));
        } else {
            //取消不进调度
            cancelOrderResDTO.setIsReturnDisCoupon(true);
            cancelOrderResDTO.setIsReturnOwnerCoupon(true);
            cancelOrderResDTO.setOwnerCouponNo(null == ownerCouponEntity ? null : ownerCouponEntity.getCouponId());
            cancelOrderResDTO.setIsRefund(true);
            cancelOrderResDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
            cancelOrderResDTO.setSrvGetFlag(null != renterOrderEntity.getIsGetCar() && renterOrderEntity.getIsGetCar() == 1);
            cancelOrderResDTO.setSrvReturnFlag(null != renterOrderEntity.getIsReturnCar() && renterOrderEntity.getIsReturnCar() == 1);

            //罚车主补贴给租客
            OwnerOrderFineDeatailEntity ownerOrderFineDetailEntityOne =
                    ownerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                            FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
            ownerOrderFineDetailEntityOne.setOwnerOrderNo(ownerOrderEntity.getOwnerOrderNo());
            ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDetailEntityOne);

            //租客收益处理
            ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
                    consoleRenterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty,
                            FineSubsidyCodeEnum.RENTER, FineSubsidySourceCodeEnum.OWNER, FineTypeEnum.CANCEL_FINE);
            consoleRenterOrderFineDeatailService.saveConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);

            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.NO);
            orderStatusDTO.setDispatchStatus(DispatcherStatusEnum.NOT_DISPATCH.getCode());
            renterOrderService.updateChildStatusByOrderNo(orderNo, RenterChildStatusEnum.END.getCode());
        }
        //落库
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.from(orderStatusDTO.getStatus()));
        if(null != ownerOrderEntity) {
            ownerOrderService.updateChildStatusByOrderNo(orderNo, OwnerChildStatusEnum.END.getCode());
            ownerOrderService.updateDispatchReasonByOrderNo(orderNo,DispatcherReasonEnum.owner_cancel);
            //取消信息处理(order_cancel_reason)
            orderCancelReasonService.addOrderCancelReasonRecord(buildOrderCancelReasonEntity(orderNo,ownerOrderEntity.getOwnerOrderNo(),
                    cancelReason));
        }
        //返回信息处理
        cancelOrderResDTO.setCarNo(goodsDetail.getCarNo());
        cancelOrderResDTO.setRentCarPayStatus(orderStatusEntity.getRentCarPayStatus());
        cancelOrderResDTO.setCityCode(Integer.valueOf(orderEntity.getCityCode()));
        cancelOrderResDTO.setRentTime(orderEntity.getExpRentTime());
        cancelOrderResDTO.setRevertTime(orderEntity.getExpRevertTime());
        cancelOrderResDTO.setStatus(orderStatusDTO.getStatus());
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
        orderCancelReasonEntity.setDutySource(CancelOrderDutyEnum.CANCEL_ORDER_DUTY_OWNER.getCode());
        return orderCancelReasonEntity;
    }


    /**
     * 车主取消罚金调度后续处理信息
     *
     * @param orderNo      订单号
     * @param ownerOrderNo 车主订单号
     * @param memNo        租客会员号
     * @param fineAmt      罚金
     * @return OwnerOrderFineApplyEntity
     */
    private OwnerOrderFineApplyEntity buildOwnerOrderFineApplyEntity(String orderNo, String ownerOrderNo,
                                                                     Integer memNo, Integer fineAmt) {
        if(null == fineAmt || fineAmt == 0) {
            return null;
        }
        OwnerOrderFineApplyEntity applyEntity = new OwnerOrderFineApplyEntity();
        applyEntity.setOrderNo(orderNo);
        applyEntity.setOwnerOrderNo(ownerOrderNo);
        applyEntity.setMemNo(memNo);
        applyEntity.setFineAmount(fineAmt);

        applyEntity.setFineSubsidySourceCode(FineSubsidySourceCodeEnum.OWNER.getFineSubsidySourceCode());
        applyEntity.setFineSubsidySourceDesc(FineSubsidySourceCodeEnum.OWNER.getFineSubsidySourceDesc());
        applyEntity.setFineType(FineTypeEnum.CANCEL_FINE.getFineType());
        applyEntity.setFineTypeDesc(FineTypeEnum.CANCEL_FINE.getFineTypeDesc());

        return applyEntity;
    }
}
