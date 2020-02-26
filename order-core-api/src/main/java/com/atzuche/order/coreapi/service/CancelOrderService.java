package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.CancelSourceEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.vo.req.AdminCancelOrderReqVO;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.car.api.model.dto.OwnerCancelDTO;

import java.util.Map;

/**
 * 订单取消操作
 *
 * @author pengcheng.fu
 * @date 2020/1/7 15:45
 */

@Service
public class CancelOrderService {

    private static Logger logger = LoggerFactory.getLogger(CancelOrderService.class);

    @Autowired
    private RenterCancelOrderService renterCancelOrderService;
    @Autowired
    private OwnerCancelOrderService ownerCancelOrderService;
    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    private StockProxyService stockService;
    @Autowired
    private DeliveryCarService deliveryCarService;
    @Autowired
    private OrderCommonConver orderCommonConver;
    @Autowired
    private CancelOrderCheckService cancelOrderCheckService;
    @Autowired
    private OrderActionMqService orderActionMqService;
    @Autowired
    private OrderStatusMqService orderStatusMqService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderCouponService orderCouponService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private RenterOrderCostService renterOrderCostService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;



    /**
     * 订单取消
     *
     * @param cancelOrderReqVO 请求参数
     */
    public void cancel(CancelOrderReqVO cancelOrderReqVO) {
        //参数转换处理
        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        BeanUtils.copyProperties(cancelOrderReqVO,cancelOrderReqDTO);
        //兼容定时任务校验
        cancelOrderReqDTO.setConsoleInvoke(StringUtils.equals(OrderConstant.SYSTEM_OPERATOR_JOB,cancelOrderReqVO.getOperatorName()));
        CancelOrderReqContext reqContext = buildCancelOrderReqContext(cancelOrderReqDTO);
        //公共校验
        cancelOrderCheckService.checkCancelOrder(reqContext);

        //TODO A 订单取消逻辑处理
        //TODO A.1 订单取消

        //TODO A.2 优惠券处理

        //TODO A.3 车主券处理

        //TODO A.4 库存处理

        //TODO A.5 退款处理

        //TODO A.6 仁云流程系统

        //TODO B 订单责任判定处理


        //TODO C mq消息处理
        //TODO C.1 行为MQ发送
        //TODO C.2 状态MQ发送
        //TODO C.3 短信MQ发送












        //取消处理
        CancelOrderResDTO res = null;
        if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), cancelOrderReqVO.getMemRole())) {
            //租客取消
            res = renterCancelOrderService.cancel(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getCancelReason(),
                    false);
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqVO.getMemRole())) {
            //车主取消
            res = ownerCancelOrderService.cancel(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getCancelReason(),
                    false);
        }

        logger.info("res:[{}]", JSON.toJSONString(res));
        //优惠券
        if (null != res && null != res.getIsReturnDisCoupon() && res.getIsReturnDisCoupon()) {
            //退还优惠券(平台券+送取服务券)
            couponAndCoinHandleService.undoPlatformCoupon(cancelOrderReqVO.getOrderNo());
            couponAndCoinHandleService.undoGetCarFeeCoupon(cancelOrderReqVO.getOrderNo());
        }
        //订单取消（租客取消、车主取消、平台取消）如果使用了车主券且未支付，则退回否则不处理
        if (null != res && null != res.getIsReturnOwnerCoupon() && res.getIsReturnOwnerCoupon()) {
            //退还车主券
            String recover = null == res.getRentCarPayStatus() || res.getRentCarPayStatus() == 0 ? "1" : "0";
            couponAndCoinHandleService.undoOwnerCoupon(cancelOrderReqVO.getOrderNo(), res.getOwnerCouponNo(), recover);
        }

        //库存处理
        if (null != res) {
            //释放库存(trans_filter)
            stockService.releaseCarStock(cancelOrderReqVO.getOrderNo(), res.getCarNo());
            if(StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqVO.getMemRole())) {
                //锁定car_filter
                stockService.ownerCancelStock(buildOwnerCancelDTO(cancelOrderReqVO.getOrderNo(), res));
            }
        }

        if (null != res && null != res.getIsRefund() && res.getIsRefund()) {
            //通知收银台退款以及退还凹凸币和钱包
            orderSettleService.settleOrderCancel(cancelOrderReqVO.getOrderNo());
            //通知流程系统
            CancelOrderDeliveryVO cancelOrderDeliveryVO =
                    orderCommonConver.buildCancelOrderDeliveryVO(cancelOrderReqVO.getOrderNo(),
                    res);
            if (null != cancelOrderDeliveryVO) {
                deliveryCarService.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO);
            }
        }

        //发送订单取消事件
        Map map = Maps.newHashMap();
        CancelSourceEnum cancelSourceEnum = CancelSourceEnum.OWNER;
        NewOrderMQActionEventEnum actionEventEnum = NewOrderMQActionEventEnum.ORDER_FINISH;
        if(StringUtils.equals(MemRoleEnum.RENTER.getCode(), cancelOrderReqVO.getMemRole())) {
            cancelSourceEnum = CancelSourceEnum.RENTER;
            actionEventEnum = NewOrderMQActionEventEnum.ORDER_CANCEL;
            map = SmsParamsMapUtil.getParamsMap(cancelOrderReqVO.getOrderNo(), ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_RENTER.getValue(),ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_OWNER.getValue(),null);
        }
        orderActionMqService.sendCancelOrderSuccess(cancelOrderReqVO.getOrderNo(), cancelSourceEnum, actionEventEnum,map);
        NewOrderMQStatusEventEnum newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_END;
        if(null != res && res.getStatus() == OrderStatusEnum.TO_DISPATCH.getStatus()) {
            newOrderMQStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREDISPATCH;
        }
        orderStatusMqService.sendOrderStatusByOrderNo(cancelOrderReqVO.getOrderNo(),res.getStatus(),newOrderMQStatusEventEnum);
    }


    /**
     * 管理后台代取消订单
     *
     * @param adminCancelOrderReqVO 请求参数
     */
    public void cancel(AdminCancelOrderReqVO adminCancelOrderReqVO) {
        //参数转换处理
        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        BeanUtils.copyProperties(adminCancelOrderReqVO,cancelOrderReqDTO);
        cancelOrderReqDTO.setConsoleInvoke(true);
        CancelOrderReqContext reqContext = buildCancelOrderReqContext(cancelOrderReqDTO);
        //公共校验
        cancelOrderCheckService.checkCancelOrder(reqContext);


        //TODO 订单取消逻辑处理

        //TODO 取消订单后续操作

        //TODO 订单责任判定处理

        //TODO mq消息处理
        //TODO 行为MQ发送
        //TODO 状态MQ发送
        //TODO 短信MQ发送




    }



    private void cancelSuccessHandle() {

        //TODO 优惠券处理

        //TODO 车主券处理

        //TODO 库存处理

        //TODO 仁云流程系统
    }

    /**
     * 撤销优惠券
     *
     * @param orderNo 订单号
     */
    private void undoCoupon(String orderNo) {
        //撤销平台优惠券
        couponAndCoinHandleService.undoPlatformCoupon(orderNo);
        //撤销送取服务
        couponAndCoinHandleService.undoGetCarFeeCoupon(orderNo);
    }

    /**
     * 撤销车主券
     *
     * @param orderNo 订单号
     * @param rentCarPayStatus 租车费用支付状态
     * @param ownerCouponNo 车主券码
     */
    private void undoOwnerCoupon(String orderNo, Integer rentCarPayStatus, String ownerCouponNo){
        //退还车主券
        String recover = null == rentCarPayStatus || rentCarPayStatus == 0 ? "1" : "0";
        couponAndCoinHandleService.undoOwnerCoupon(orderNo, ownerCouponNo, recover);
    }

    /**
     * 库存处理
     *
     * @param orderNo 订单号
     * @param memRole 操作角色
     * @param res 取消逻辑返回结果
     */
    private void stockHandle(String orderNo, String memRole, CancelOrderResDTO res) {
        //释放库存(trans_filter)
        stockService.releaseCarStock(orderNo, res.getCarNo());
        if(StringUtils.equals(MemRoleEnum.OWNER.getCode(), memRole)) {
            //锁定car_filter
            stockService.ownerCancelStock(buildOwnerCancelDTO(memRole, res));
        }
    }


    /**
     * 构建OwnerCancelDTO
     *
     * @param orderNo 订单号
     * @param res 取消逻辑返回结果
     * @return OwnerCancelDTO
     */
    private OwnerCancelDTO buildOwnerCancelDTO(String orderNo, CancelOrderResDTO res){
        OwnerCancelDTO ownerCancelDTO = new OwnerCancelDTO();
        ownerCancelDTO.setOrderNo(orderNo);
        ownerCancelDTO.setCarNo(res.getCarNo());
        ownerCancelDTO.setCityCode(res.getCityCode());
        ownerCancelDTO.setSource(2);
        ownerCancelDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(res.getRentTime()));
        ownerCancelDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(res.getRevertTime()));
        return ownerCancelDTO;
    }


    /**
     * 构建公共请求参数
     *
     * @param cancelOrderReqDTO 请求参数
     * @return CancelOrderReqContext
     */
    private CancelOrderReqContext buildCancelOrderReqContext(CancelOrderReqDTO cancelOrderReqDTO){
        CancelOrderReqContext context = new CancelOrderReqContext();
        context.setCancelOrderReqDTO(cancelOrderReqDTO);

        OrderEntity orderEntity = orderService.getOrderEntity(cancelOrderReqDTO.getOrderNo());
        context.setOrderEntity(orderEntity);

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(cancelOrderReqDTO.getOrderNo());
        context.setOrderStatusEntity(orderStatusEntity);

        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(cancelOrderReqDTO.getOrderNo());
        context.setRenterOrderEntity(renterOrderEntity);

        RenterGoodsDetailDTO renterGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        context.setRenterGoodsDetailDTO(renterGoodsDetailDTO);

        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(cancelOrderReqDTO.getOrderNo(),
                renterOrderEntity.getRenterOrderNo());
        context.setRenterOrderCostEntity(renterOrderCostEntity);

        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(cancelOrderReqDTO.getOrderNo());
        context.setOwnerOrderEntity(ownerOrderEntity);

        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
        context.setOwnerGoodsDetailDTO(ownerGoodsDetail);

        OrderCouponEntity ownerCouponEntity =
                orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(cancelOrderReqDTO.getOrderNo(),
                renterOrderEntity.getRenterOrderNo());
        context.setOwnerCouponEntity(ownerCouponEntity);

        return context;

    }

}
