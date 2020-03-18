package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
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
import com.atzuche.order.search.OrderSearchProxyService;
import com.atzuche.order.search.dto.ConflictOrderSearchReqDTO;
import com.atzuche.order.search.dto.OrderInfoDTO;
import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.car.api.model.dto.OwnerCancelDTO;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    CarRentalTimeApiProxyService carRentalTimeApiService;
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
    StockProxyService stockService;
    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    OrderSettleService orderSettleService;
    @Autowired
    OrderFlowService orderFlowService;
    @Autowired
    RefuseOrderCheckService refuseOrderCheckService;
    @Autowired
    OrderActionMqService orderActionMqService;
    @Autowired
    OrderStatusMqService orderStatusMqService;


    /**
     * 车主拒单
     *
     * @param reqVO            请求参数
     * @param dispatcherReason 原因
     */
    @Transactional(rollbackFor = Exception.class)
    public void refuse(RefuseOrderReqVO reqVO, DispatcherReasonEnum dispatcherReason) {
        //车主拒绝前置校验
        boolean isConsoleInvoke = dispatcherReason.getCode() == DispatcherReasonEnum.timeout.getCode() || (null != reqVO.getIsConsoleInvoke() && OrderConstant.YES == reqVO.getIsConsoleInvoke());
        refuseOrderCheckService.checkOwnerAgreeOrRefuseOrder(reqVO.getOrderNo(), isConsoleInvoke);
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
                        orderStatusEntity, ownerCouponEntity, 1));

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(reqVO.getOrderNo());
        if (isDispatch) {
            //进入调度
            //订单状态更新
            orderStatusDTO.setStatus(OrderStatusEnum.TO_DISPATCH.getStatus());
            orderStatusDTO.setIsDispatch(OrderConstant.YES);
            orderStatusDTO.setDispatchStatus(OrderConstant.YES);
        } else {
            //不进调度
            orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
            renterOrderService.updateChildStatusByOrderNo(reqVO.getOrderNo(), RenterChildStatusEnum.END.getCode());

            //撤销优惠券
            //退还优惠券(平台券+送取服务券)
            couponAndCoinHandleService.undoPlatformCoupon(reqVO.getOrderNo());
            couponAndCoinHandleService.undoGetCarFeeCoupon(reqVO.getOrderNo());
            //退还车主券
            if (null != ownerCouponEntity) {
                String recover = null == orderStatusEntity.getRentCarPayStatus() || orderStatusEntity.getRentCarPayStatus() == 0 ? "1" : "0";
                couponAndCoinHandleService.undoOwnerCoupon(reqVO.getOrderNo(), ownerCouponEntity.getCouponId(), recover);
            }
            //通知收银台退还凹凸币和钱包
            orderSettleService.settleOrderCancel(reqVO.getOrderNo());
        }

        //落库
        RenterOrderEntity record = new RenterOrderEntity();
        record.setId(renterOrderEntity.getId());
        record.setReqAcceptTime(LocalDateTime.now());
        record.setAgreeFlag(OwnerAgreeTypeEnum.REFUSE.getCode());
        renterOrderService.updateRenterOrderInfo(record);

        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
        //添加order_flow记录
        orderFlowService.inserOrderStatusChangeProcessInfo(reqVO.getOrderNo(),
                OrderStatusEnum.from(orderStatusDTO.getStatus()));
        ownerOrderService.updateChildStatusByOrderNo(reqVO.getOrderNo(), OwnerChildStatusEnum.END.getCode());
        ownerOrderService.updateDispatchReasonByOrderNo(reqVO.getOrderNo(), dispatcherReason);
        //取消信息处理(order_cancel_reason)
        OrderCancelReasonEntity orderCancelReasonEntity = buildOrderCancelReasonEntity(reqVO.getOrderNo(),
                renterOrderEntity.getRenterOrderNo(),
                ownerOrderEntity.getOwnerOrderNo(), "车主拒单");
        orderCancelReasonEntity.setUpdateOp(StringUtils.isBlank(reqVO.getOperatorName()) ? OrderConstant.SYSTEM_OPERATOR :
                reqVO.getOperatorName());
        orderCancelReasonEntity.setCreateOp(orderCancelReasonEntity.getUpdateOp());
        orderCancelReasonEntity.setCancelReqTime(LocalDateTime.now());
        orderCancelReasonService.addOrderCancelReasonRecord(orderCancelReasonEntity);
        //释放库存(车主取消/拒绝时不释放库存)
        stockService.releaseCarStock(reqVO.getOrderNo(), goodsDetail.getCarNo());
        //锁定车辆可租时间
        stockService.ownerCancelStock(buildOwnerCancelDTO(reqVO.getOrderNo(), goodsDetail.getCarNo(), orderEntity));


        //发送车主拒绝事件
        orderActionMqService.sendOwnerRefundOrderSuccess(reqVO.getOrderNo());

        NewOrderMQStatusEventEnum newOrderMqStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_END;
        if (orderStatusDTO.getStatus() == OrderStatusEnum.TO_DISPATCH.getStatus()) {
            newOrderMqStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREDISPATCH;
        }
        orderStatusMqService.sendOrderStatusByOrderNo(reqVO.getOrderNo(), orderStatusDTO.getStatus(), newOrderMqStatusEventEnum);

    }

    /**
     * 车主同意订单拒绝租期重叠的其他订单
     *
     * @param orderNo 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void refuse(String orderNo) {
        logger.info("A.refuse. param is,orderNo:[{}]",orderNo);
        //校验
        refuseOrderCheckService.checkOwnerAgreeOrRefuseOrder(orderNo, true);

        //订单状态变更
        renterOrderService.updateChildStatusByOrderNo(orderNo, RenterChildStatusEnum.END.getCode());
        ownerOrderService.updateChildStatusByOrderNo(orderNo, OwnerChildStatusEnum.END.getCode());

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setStatus(OrderStatusEnum.CLOSED.getStatus());
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);

        //添加order_flow记录
        orderFlowService.inserOrderStatusChangeProcessInfo(orderNo, OrderStatusEnum.from(orderStatusDTO.getStatus()));

        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        //更新车主请求信息
        RenterOrderEntity record = new RenterOrderEntity();
        record.setId(renterOrderEntity.getId());
        record.setReqAcceptTime(LocalDateTime.now());
        record.setAgreeFlag(OwnerAgreeTypeEnum.REFUSE.getCode());
        renterOrderService.updateRenterOrderInfo(record);

        //更新车主拒单理由
        ownerOrderService.updateDispatchReasonByOrderNo(orderNo, DispatcherReasonEnum.owner_refuse);

        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        //订单结束原因信息更新
        OrderCancelReasonEntity orderCancelReasonEntity = buildOrderCancelReasonEntity(orderNo,
                renterOrderEntity.getRenterOrderNo(),
                ownerOrderEntity.getOwnerOrderNo(), "租期重叠车主拒单");
        orderCancelReasonEntity.setUpdateOp(OrderConstant.SYSTEM_OPERATOR);
        orderCancelReasonEntity.setCreateOp(OrderConstant.SYSTEM_OPERATOR);
        orderCancelReasonEntity.setCancelReqTime(LocalDateTime.now());
        orderCancelReasonService.addOrderCancelReasonRecord(orderCancelReasonEntity);


        //撤销优惠券
        //退还优惠券(平台券+送取服务券)
        couponAndCoinHandleService.undoPlatformCoupon(orderNo);
        couponAndCoinHandleService.undoGetCarFeeCoupon(orderNo);
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //退还车主券
        if (null != ownerCouponEntity) {
            String recover = null == orderStatusEntity.getRentCarPayStatus() || orderStatusEntity.getRentCarPayStatus() == 0 ? "1" : "0";
            couponAndCoinHandleService.undoOwnerCoupon(orderNo, ownerCouponEntity.getCouponId(), recover);
        }
        //通知收银台退还凹凸币和钱包
        orderSettleService.settleOrderCancel(orderNo);
        //发送车主拒绝事件
        orderActionMqService.sendOwnerRefundOrderSuccess(orderNo);
        orderStatusMqService.sendOrderStatusByOrderNo(orderNo, orderStatusDTO.getStatus(), NewOrderMQStatusEventEnum.ORDER_END);
    }


    /**
     * 构建订单结束原因
     *
     * @param orderNo      订单号
     * @param ownerOrderNo 车主订单号
     * @param cancelReason 结束原因
     * @return OrderCancelReasonEntity
     */
    private OrderCancelReasonEntity buildOrderCancelReasonEntity(String orderNo,
                                                                 String renterOrderNo,
                                                                 String ownerOrderNo,
                                                                 String cancelReason) {
        OrderCancelReasonEntity orderCancelReasonEntity = new OrderCancelReasonEntity();
        orderCancelReasonEntity.setOperateType(CancelOperateTypeEnum.REFUSE_ORDER.getCode());
        orderCancelReasonEntity.setCancelReason(cancelReason);
        orderCancelReasonEntity.setCancelSource(CancelSourceEnum.OWNER.getCode());
        orderCancelReasonEntity.setOrderNo(orderNo);
        orderCancelReasonEntity.setRenterOrderNo(renterOrderNo);
        orderCancelReasonEntity.setOwnerOrderNo(ownerOrderNo);
        return orderCancelReasonEntity;
    }

    /**
     * 构建车主取消库存处理参数
     *
     * @param orderNo     订单号
     * @param carNo       车辆号
     * @param orderEntity 订单信息
     * @return OwnerCancelDTO
     */
    private OwnerCancelDTO buildOwnerCancelDTO(String orderNo, Integer carNo, OrderEntity orderEntity) {
        OwnerCancelDTO ownerCancelDTO = new OwnerCancelDTO();
        ownerCancelDTO.setOrderNo(orderNo);
        ownerCancelDTO.setCarNo(carNo);
        ownerCancelDTO.setCityCode(Integer.valueOf(orderEntity.getCityCode()));
        ownerCancelDTO.setSource(1);
        ownerCancelDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(orderEntity.getExpRentTime()));
        ownerCancelDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(orderEntity.getExpRevertTime()));
        return ownerCancelDTO;
    }

}
