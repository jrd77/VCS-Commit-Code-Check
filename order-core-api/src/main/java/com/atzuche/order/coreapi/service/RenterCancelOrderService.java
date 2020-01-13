package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
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
    private RenterOrderFineDeatailService renterOrderFineDeatailService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OrderFlowService orderFlowService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private OrderStatusService orderStatusService;

    /**
     * 取消处理
     *
     * @return CancelOrderResDTO 返回信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel(String orderNo, String cancelReason) {
        //校验
        check();

        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        //获取租客订单费用明细
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);

        //罚金计算(罚金和收益)
        //租客罚金处理
        CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                renterOrderCostEntity, goodsDetail.getCarOwnerType());
        int penalty = renterOrderFineDeatailService.calCancelFine(cancelFineAmt);
        //罚租客补贴给车主
        RenterOrderFineDeatailEntity renterOrderFineDeatailEntityOne =
                renterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                        FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
        //罚租客补贴给平台(保险费)
        RenterOrderFineDeatailEntity renterOrderFineDeatailEntityTwo =
                renterOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), Math.abs(renterOrderCostEntity.getBasicEnsureAmount()), FineSubsidyCodeEnum.PLATFORM,
                        FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);
        //车主收益
        ConsoleOwnerOrderFineDeatailEntity consoleOwnerOrderFineDeatailEntity =
                consoleOwnerOrderFineDeatailService.fineDataConvert(cancelFineAmt.getCostBaseDTO(), penalty, FineSubsidyCodeEnum.OWNER,
                        FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.CANCEL_FINE);

        //落库
        //订单状态更新
        orderStatusService.saveOrderStatusInfo(buildOrderStatusDTO(orderNo));
        renterOrderService.updateRenterOrderChildStatus(renterOrderEntity.getId(),
                RenterChildStatusEnum.END.getCode());
        ownerOrderService.updateOwnerOrderChildStatus(ownerOrderEntity.getId(), OwnerChildStatusEnum.END.getCode());
        orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.CLOSED);
        //取消信息处理(order_cancel_reason)
        orderCancelReasonService.addOrderCancelReasonRecord(buildOrderCancelReasonEntity(orderNo,renterOrderEntity.getRenterOrderNo(),
                cancelReason));

        renterOrderFineDeatailService.saveRenterOrderFineDeatail(renterOrderFineDeatailEntityOne);
        renterOrderFineDeatailService.saveRenterOrderFineDeatail(renterOrderFineDeatailEntityTwo);
        consoleOwnerOrderFineDeatailService.addFineRecord(consoleOwnerOrderFineDeatailEntity);


        //返回信息处理
        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();
        cancelOrderResDTO.setIsReturnDisCoupon(true);
        cancelOrderResDTO.setIsReturnOwnerCoupon(true);
        cancelOrderResDTO.setIsRefund(true);
        cancelOrderResDTO.setRentCarPayStatus(orderStatusEntity.getRentCarPayStatus());
        cancelOrderResDTO.setCarNo(goodsDetail.getCarNo());
        cancelOrderResDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        cancelOrderResDTO.setSrvGetFlag(null != renterOrderEntity.getIsGetCar() && renterOrderEntity.getIsGetCar() == 1);
        cancelOrderResDTO.setSrvReturnFlag(null != renterOrderEntity.getIsReturnCar() && renterOrderEntity.getIsReturnCar() == 1);
        return cancelOrderResDTO;
    }

    public void check() {
        //TODO:租客取消校验


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


    private OrderStatusDTO buildOrderStatusDTO(String orderNo) {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
        return orderStatusDTO;
    }

    private OrderCancelReasonEntity buildOrderCancelReasonEntity(String orderNo,
                                                                 String renterOrderNo, String cancelReason) {
        OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
        orderCancelReasonEntity.setOperateType(CancelOperateTypeEnum.CANCEL_ORDER.getCode());
        orderCancelReasonEntity.setCancelReason(cancelReason);
        orderCancelReasonEntity.setCancelSource(CancelSourceEnum.RENTER.getCode());
        orderCancelReasonEntity.setOrderNo(orderNo);
        orderCancelReasonEntity.setSubOrderNo(renterOrderNo);
        orderCancelReasonEntity.setDutySource(CancelOrderDutyEnum.CANCEL_ORDER_DUTY_RENTER.getCode());
        return orderCancelReasonEntity;
    }

}
