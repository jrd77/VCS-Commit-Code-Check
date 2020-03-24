package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.CancelSourceEnum;
import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.vo.req.AdminCancelOrderReqVO;
import com.atzuche.order.commons.vo.req.AdminOrderCancelJudgeDutyReqVO;
import com.atzuche.order.commons.vo.req.CancelOrderDelayRefundReqVO;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.CancelOrderReqContext;
import com.atzuche.order.coreapi.entity.dto.CancelOrderJudgeDutyResDTO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderReqDTO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.entity.dto.JudgeDutyResDTO;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderRefundRecordEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderCancelReasonService;
import com.atzuche.order.parentorder.service.OrderRefundRecordService;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.car.api.model.dto.OwnerCancelDTO;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;
import com.dianping.cat.Cat;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Autowired
    private CancelOrderJudgeDutyService cancelOrderJudgeDutyService;
    @Autowired
    private OrderCancelReasonService orderCancelReasonService;
    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private OrderRefundRecordService orderRefundRecordService;
    @Autowired
    private OwnerAgreeDelayRefundService ownerAgreeDelayRefundService;


    /**
     * 取消订单
     * <p>走自动判责逻辑</p>
     *
     * @param cancelOrderReqVO 请求参数
     */
    public void cancel(CancelOrderReqVO cancelOrderReqVO) {
        //参数转换处理
        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        BeanUtils.copyProperties(cancelOrderReqVO, cancelOrderReqDTO);
        //兼容定时任务校验
        cancelOrderReqDTO.setConsoleInvoke(false);
        CancelOrderReqContext reqContext = buildCancelOrderReqContext(cancelOrderReqDTO);
        //公共校验
        if (!StringUtils.equals(OrderConstant.SYSTEM_OPERATOR_JOB, cancelOrderReqVO.getOperatorName())) {
            cancelOrderCheckService.checkCancelOrder(reqContext);
        }
        //取消处理
        LocalDateTime cancelReqTime = LocalDateTime.now();
        CancelOrderResDTO res = null;
        if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), cancelOrderReqVO.getMemRole())) {
            //租客取消
            res = renterCancelOrderService.cancel(cancelReqTime, reqContext);
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqVO.getMemRole())) {
            //车主取消
            res = ownerCancelOrderService.cancel(cancelReqTime, reqContext);
        } else {
            logger.warn("Invalid cancel operation. param is,cancelOrderReqVO:[{}]", JSON.toJSONString(cancelOrderReqVO));
        }
        //取消责任处理
        if (null != res) {
            //判断是否补贴罚金
            CancelOrderJudgeDutyResDTO cancelOrderJudgeDutyRes = holidayService.isSubsidyFineAmt(reqContext, res.getWrongdoer());
            JudgeDutyResDTO judgeDutyResDTO = cancelOrderJudgeDutyService.judgeDuty(res.getWrongdoer(),
                    res.getIsDispatch(),
                    cancelOrderJudgeDutyRes.getIsSubsidyFineAmt(),
                    cancelReqTime, reqContext);
            //发送消息通知会员记录节假日取消次数
            if(null != judgeDutyResDTO.getIsNoticeOrderCancelMemHolidayDeduct() && judgeDutyResDTO.getIsNoticeOrderCancelMemHolidayDeduct()) {
                orderActionMqService.sendOrderCancelMemHolidayDeduct(cancelOrderReqVO.getOrderNo(),
                        cancelOrderJudgeDutyRes.getMemNo(), cancelOrderJudgeDutyRes.getHolidayId(), cancelOrderReqVO.getOperatorName());
            }

            if (judgeDutyResDTO.getIsNoticeSettle()) {
                //通知结算计算凹凸币和钱包等
                com.atzuche.order.settle.vo.req.CancelOrderReqDTO reqDTO =
                        orderCommonConver.buildCancelOrderReqDTO(cancelOrderReqVO.getOrderNo(),
                                reqContext.getRenterOrderEntity().getRenterOrderNo(),
                                reqContext.getOwnerOrderEntity().getOwnerOrderNo(), false, true);
                logger.info("取消订单责任判定后进行结算,reqDTO:[{}]", JSON.toJSONString(reqDTO));
                try {
                    orderSettleService.orderCancelSettleCombination(reqDTO);
                } catch (Exception e) {
                    logger.error("取消订单责任判定后进行结算异常. reqDTO:[{}]", JSON.toJSONString(reqDTO), e);
                    Cat.logError("取消订单责任判定后进行结算异常.reqDTO: " + JSON.toJSONString(reqDTO), e);
                }
            }
        }
        //后续处理
        cancelSuccessHandle(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getMemRole(), res);
        //发送MQ消息
        cancelSuccessSendMq(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getMemRole(), res);
    }


    /**
     * 管理后台代取消订单
     * <p>走手动判责逻辑</p>
     *
     * @param adminCancelOrderReqVO 请求参数
     * @param isConsoleInvoke       是否管理后台操作
     * @param appealFlag            是否申诉
     * @param appealReason          申诉原因
     */
    public void cancel(AdminCancelOrderReqVO adminCancelOrderReqVO, boolean isConsoleInvoke, Integer appealFlag,
                       String appealReason) {
        //参数转换处理
        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        BeanUtils.copyProperties(adminCancelOrderReqVO, cancelOrderReqDTO);
        cancelOrderReqDTO.setConsoleInvoke(isConsoleInvoke);
        cancelOrderReqDTO.setAppealFlag(appealFlag);
        cancelOrderReqDTO.setCancelReason(appealReason);
        CancelOrderReqContext reqContext = buildCancelOrderReqContext(cancelOrderReqDTO);
        //公共校验
        cancelOrderCheckService.checkCancelOrder(reqContext);
        //取消处理
        LocalDateTime cancelReqTime = LocalDateTime.now();
        CancelOrderResDTO res = null;
        if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), adminCancelOrderReqVO.getMemRole())) {
            //租客取消
            res = renterCancelOrderService.cancel(cancelReqTime, reqContext);
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), adminCancelOrderReqVO.getMemRole())) {
            //车主取消
            res = ownerCancelOrderService.cancel(cancelReqTime, reqContext);
        } else {
            logger.warn("Invalid cancel operation. param is,adminCancelOrderReqVO:[{}]", JSON.toJSONString(adminCancelOrderReqVO));
        }
        //后续处理
        cancelSuccessHandle(adminCancelOrderReqVO.getOrderNo(), adminCancelOrderReqVO.getMemRole(), res);
        //发送MQ消息
        cancelSuccessSendMq(adminCancelOrderReqVO.getOrderNo(), adminCancelOrderReqVO.getMemRole(), res);

    }


    /**
     * 取消订单手动判责
     *
     * @param reqVO 请求参数
     */
    public void orderCancelJudgeDuty(AdminOrderCancelJudgeDutyReqVO reqVO) {
        //参数转换处理
        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        BeanUtils.copyProperties(reqVO, cancelOrderReqDTO);
        cancelOrderReqDTO.setConsoleInvoke(true);
        CancelOrderReqContext reqContext = buildCancelOrderReqContext(cancelOrderReqDTO);
        OrderCancelReasonEntity orderCancelReasonEntity = orderCancelReasonService.selectByOrderNo(reqVO.getOrderNo()
                , reqContext.getRenterOrderEntity().getRenterOrderNo(), reqContext.getOwnerOrderEntity().getOwnerOrderNo());
        reqContext.setOrderCancelReasonEntity(orderCancelReasonEntity);
        //公共校验
        cancelOrderCheckService.checkOrderCancelJudgeDuty(reqContext);
        //判断是否补贴罚金
        CancelOrderJudgeDutyResDTO cancelOrderJudgeDutyRes = holidayService.isSubsidyFineAmt(reqContext, Integer.valueOf(reqVO.getWrongdoer()));
        //责任判定
        boolean isDispatch =
                reqContext.getOrderStatusEntity().getIsDispatch() == OrderConstant.YES && reqContext.getOrderStatusEntity().getDispatchStatus() != 3;
        JudgeDutyResDTO judgeDutyResDTO = cancelOrderJudgeDutyService.judgeDuty(Integer.valueOf(reqVO.getWrongdoer()),
                isDispatch,
                cancelOrderJudgeDutyRes.getIsSubsidyFineAmt(), orderCancelReasonEntity.getCancelReqTime(), reqContext);
        if (!isDispatch) {
            //发送消息通知会员记录节假日取消次数
            if(null != judgeDutyResDTO.getIsNoticeOrderCancelMemHolidayDeduct() && judgeDutyResDTO.getIsNoticeOrderCancelMemHolidayDeduct()) {
                orderActionMqService.sendOrderCancelMemHolidayDeduct(reqVO.getOrderNo(),
                        cancelOrderJudgeDutyRes.getMemNo(), cancelOrderJudgeDutyRes.getHolidayId(), reqVO.getOperatorName());
            }
            if (judgeDutyResDTO.getIsNoticeSettle()) {
                //通知结算计算凹凸币和钱包等
                com.atzuche.order.settle.vo.req.CancelOrderReqDTO reqDTO =
                        orderCommonConver.buildCancelOrderReqDTO(reqVO.getOrderNo(),
                                reqVO.getRenterOrderNo(),
                                reqVO.getOwnerOrderNo(), false, true);
                logger.info("取消订单责任判定后进行结算,reqDTO:[{}]", JSON.toJSONString(reqDTO));
                try {
                    orderSettleService.orderCancelSettleCombination(reqDTO);
                } catch (Exception e) {
                    logger.error("手动责任判定后进行结算异常. reqDTO:[{}]", JSON.toJSONString(reqDTO), e);
                    Cat.logError("手动责任判定后进行结算异常.reqDTO: " + JSON.toJSONString(reqDTO), e);
                }
            }
        }

    }

    /**
     * 车主同意取消订单违约罚金
     *
     * @param reqVO 请求参数
     */
    public void ownerAgreeDelayRefund(CancelOrderDelayRefundReqVO reqVO, Integer refundRecordStatus) {
        CancelOrderReqDTO cancelOrderReqDTO = new CancelOrderReqDTO();
        BeanUtils.copyProperties(reqVO, cancelOrderReqDTO);
        cancelOrderReqDTO.setConsoleInvoke(false);
        cancelOrderReqDTO.setRefundRecordStatus(refundRecordStatus);
        CancelOrderReqContext reqContext = buildSimpleCancelOrderReqContext(cancelOrderReqDTO);
        OrderRefundRecordEntity orderRefundRecordEntity = orderRefundRecordService.getByOrderNo(reqVO.getOrderNo());
        reqContext.setOrderRefundRecordEntity(orderRefundRecordEntity);
        //公共校验
        if(refundRecordStatus == OrderConstant.ONE) {
            //非定时任务校验
            cancelOrderCheckService.delayRefundCheck(reqContext);
        }

        //通知结算退款
        com.atzuche.order.settle.vo.req.CancelOrderReqDTO reqDTO =
                orderCommonConver.buildCancelOrderReqDTO(reqVO.getOrderNo(),
                        reqContext.getRenterOrderEntity().getRenterOrderNo(),
                        reqContext.getOwnerOrderEntity().getOwnerOrderNo(), false, true);
        orderSettleService.orderCancelSettleCombination(reqDTO);

        //更新orderRerundRecord、罚金记录、取消原因等
        boolean result = ownerAgreeDelayRefundService.agreeDelayRefund(reqContext);
        if (result) {
            //发送MQ通知 撤销节假日统计
            orderActionMqService.sendRevokeOrderCancelMemHolidayDeduct(reqVO.getOrderNo(), Integer.valueOf(reqVO.getMemNo()));
        }
    }


    /**
     * 取消成功后续操作
     *
     * @param orderNo 订单号
     * @param memRole 取消角色
     * @param res     取消逻辑返回数据
     */
    private void cancelSuccessHandle(String orderNo, String memRole, CancelOrderResDTO res) {
        logger.info("取消成功后续操作.orderNo:[{}],memRole:[{}],res:[{}]", orderNo, memRole, JSON.toJSONString(res));
        if (null != res) {
            //优惠券
            if (null != res.getIsDispatch() && !res.getIsDispatch()) {
                //退还优惠券(平台券+送取服务券)
                couponAndCoinHandleService.undoPlatformCoupon(orderNo);
                couponAndCoinHandleService.undoGetCarFeeCoupon(orderNo);

                //订单取消（租客取消、车主取消、平台取消）如果使用了车主券且未支付，则退回否则不处理
                int recover = null == res.getRentCarPayStatus() || res.getRentCarPayStatus() == 0 ?
                        OrderConstant.YES : OrderConstant.NO;
                couponAndCoinHandleService.undoOwnerCoupon(orderNo, res.getOwnerCouponNo(), String.valueOf(recover));

                //通知流程系统
                CancelOrderDeliveryVO cancelOrderDeliveryVO = orderCommonConver.buildCancelOrderDeliveryVO(orderNo, res);
                if (null != cancelOrderDeliveryVO) {
                    deliveryCarService.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO);
                }
            }

            //释放库存(trans_filter)
            stockService.releaseCarStock(orderNo, res.getCarNo());
            if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), memRole)) {
                //锁定car_filter
                stockService.ownerCancelStock(buildOwnerCancelDTO(orderNo, res));
            }
        }
    }

    /**
     * 取消成功后发送MQ消息
     *
     * @param orderNo 订单号
     * @param memRole 取消角色
     * @param res     取消逻辑返回结果
     */
    private void cancelSuccessSendMq(String orderNo, String memRole, CancelOrderResDTO res) {
        if (null != res) {
            //订单取消MQ
            Map map = Maps.newHashMap();
            CancelSourceEnum cancelSourceEnum = CancelSourceEnum.OWNER;
            NewOrderMQActionEventEnum actionEventEnum = NewOrderMQActionEventEnum.ORDER_FINISH;
            if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), memRole)) {
                cancelSourceEnum = CancelSourceEnum.RENTER;
                actionEventEnum = NewOrderMQActionEventEnum.ORDER_CANCEL;
                map = SmsParamsMapUtil.getParamsMap(orderNo, ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_RENTER.getValue(), ShortMessageTypeEnum.EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_OWNER.getValue(), null);
            }
            orderActionMqService.sendCancelOrderSuccess(orderNo, cancelSourceEnum, actionEventEnum, map);

            //订单状态变更MQ
            NewOrderMQStatusEventEnum newOrderMqStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_END;
            if (res.getStatus() == OrderStatusEnum.TO_DISPATCH.getStatus()) {
                newOrderMqStatusEventEnum = NewOrderMQStatusEventEnum.ORDER_PREDISPATCH;
            }
            orderStatusMqService.sendOrderStatusByOrderNo(orderNo, res.getStatus(), newOrderMqStatusEventEnum);
        }
    }


    /**
     * 构建OwnerCancelDTO
     *
     * @param orderNo 订单号
     * @param res     取消逻辑返回结果
     * @return OwnerCancelDTO
     */
    private OwnerCancelDTO buildOwnerCancelDTO(String orderNo, CancelOrderResDTO res) {
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
    private CancelOrderReqContext buildCancelOrderReqContext(CancelOrderReqDTO cancelOrderReqDTO) {
        CancelOrderReqContext context = new CancelOrderReqContext();
        context.setCancelOrderReqDTO(cancelOrderReqDTO);

        OrderEntity orderEntity = orderService.getOrderEntity(cancelOrderReqDTO.getOrderNo());
        context.setOrderEntity(orderEntity);

        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(cancelOrderReqDTO.getOrderNo());
        context.setOrderStatusEntity(orderStatusEntity);

        RenterOrderEntity renterOrderEntity;
        if (StringUtils.isBlank(cancelOrderReqDTO.getRenterOrderNo())) {
            renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(cancelOrderReqDTO.getOrderNo());
        } else {
            renterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(cancelOrderReqDTO.getRenterOrderNo());
        }
        context.setRenterOrderEntity(renterOrderEntity);

        RenterGoodsDetailDTO renterGoodsDetailDTO = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        context.setRenterGoodsDetailDTO(renterGoodsDetailDTO);

        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(cancelOrderReqDTO.getOrderNo(),
                renterOrderEntity.getRenterOrderNo());
        context.setRenterOrderCostEntity(renterOrderCostEntity);

        OwnerOrderEntity ownerOrderEntity;
        if (StringUtils.isBlank(cancelOrderReqDTO.getOwnerOrderNo())) {
            ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(cancelOrderReqDTO.getOrderNo());
        } else {
            ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(cancelOrderReqDTO.getOwnerOrderNo());
        }
        context.setOwnerOrderEntity(ownerOrderEntity);

        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
        context.setOwnerGoodsDetailDTO(ownerGoodsDetail);

        OrderCouponEntity ownerCouponEntity =
                orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(cancelOrderReqDTO.getOrderNo(),
                        renterOrderEntity.getRenterOrderNo());
        context.setOwnerCouponEntity(ownerCouponEntity);

        return context;

    }

    /**
     * 构建简洁公共请求参数
     *
     * @param cancelOrderReqDTO 请求参数
     * @return CancelOrderReqContext
     */
    private CancelOrderReqContext buildSimpleCancelOrderReqContext(CancelOrderReqDTO cancelOrderReqDTO) {
        CancelOrderReqContext context = new CancelOrderReqContext();
        context.setCancelOrderReqDTO(cancelOrderReqDTO);

        //租客订单信息
        RenterOrderEntity renterOrderEntity;
        if (StringUtils.isBlank(cancelOrderReqDTO.getRenterOrderNo())) {
            renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(cancelOrderReqDTO.getOrderNo());
        } else {
            renterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(cancelOrderReqDTO.getRenterOrderNo());
        }
        context.setRenterOrderEntity(renterOrderEntity);

        //车主订单信息
        OwnerOrderEntity ownerOrderEntity;
        if (StringUtils.isBlank(cancelOrderReqDTO.getOwnerOrderNo())) {
            ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(cancelOrderReqDTO.getOrderNo());
        } else {
            ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(cancelOrderReqDTO.getOwnerOrderNo());
        }
        context.setOwnerOrderEntity(ownerOrderEntity);


        return context;
    }


}
