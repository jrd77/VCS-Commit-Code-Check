package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.service.mq.OrderActionMqService;
import com.atzuche.order.coreapi.service.mq.OrderStatusMqService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.event.rabbit.neworder.NewOrderMQStatusEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 调度取消
 *
 * @author pengcheng.fu
 * @date 2020/1/17 11:29
 */

@Service
public class OrderDispatchCancelService {

    @Autowired
    OrderDispatchCancelHandleService orderDispatchCancelHandleService;
    @Autowired
    CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    OrderSettleService orderSettleService;
    @Autowired
    OrderCommonConver orderCommonConver;
    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    OrderActionMqService orderActionMqService;
    @Autowired
    OrderStatusMqService orderStatusMqService;

    /**
     * 调度取消
     *
     * @param orderNo 订单号
     */
    public void dispatchCancelHandle(String orderNo) {
        //调取取消处理
        CancelOrderResDTO cancelOrderRes = orderDispatchCancelHandleService.cancelDispatch(orderNo);
        //优惠券
        if (null != cancelOrderRes) {
            if (null != cancelOrderRes.getIsReturnDisCoupon() && cancelOrderRes.getIsReturnDisCoupon()) {
                //退还优惠券(平台券+送取服务券)
                couponAndCoinHandleService.undoPlatformCoupon(orderNo);
                couponAndCoinHandleService.undoGetCarFeeCoupon(orderNo);
            }

            if (null != cancelOrderRes.getIsReturnOwnerCoupon() && cancelOrderRes.getIsReturnOwnerCoupon()) {
                //退还车主券
                String recover = null == cancelOrderRes.getRentCarPayStatus() || cancelOrderRes.getRentCarPayStatus() == 0 ? "1" : "0";
                couponAndCoinHandleService.undoOwnerCoupon(orderNo, cancelOrderRes.getOwnerCouponNo(), recover);
            }
            if (null != cancelOrderRes.getIsRefund() && cancelOrderRes.getIsRefund()) {
                //通知收银台退款以及退还凹凸币和钱包
                orderSettleService.settleOrderCancel(orderNo);

                //通知流程系统
                CancelOrderDeliveryVO cancelOrderDeliveryVO =
                        orderCommonConver.buildCancelOrderDeliveryVO(orderNo,
                                cancelOrderRes);
                if (null != cancelOrderDeliveryVO) {
                    deliveryCarService.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO);
                }
            }
        }

        //发送调度取消事件
        orderActionMqService.sendOrderDispatchCancelSuccess(orderNo);
        orderStatusMqService.sendOrderStatusByOrderNo(orderNo,cancelOrderRes.getStatus(),NewOrderMQStatusEventEnum.ORDER_END);
    }
}
