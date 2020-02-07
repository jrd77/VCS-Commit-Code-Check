package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.*;
import com.atzuche.order.coreapi.common.conver.OrderCommonConver;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.service.remote.StockProxyService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.parentorder.entity.OrderCancelReasonEntity;
import com.atzuche.order.settle.service.OrderSettleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 平台取消
 *
 * @author pengcheng.fu
 * @date 2020/1/15 20:18
 */

@Service
public class PlatformCancelOrderService {

    private static Logger logger = LoggerFactory.getLogger(PlatformCancelOrderService.class);


    @Autowired
    private StockProxyService stockService;
    @Autowired
    private PlatformCancelOrderHandleService platformCancelOrderHandleService;
    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;
    @Autowired
    private OrderSettleService orderSettleService;
    @Autowired
    private DeliveryCarService deliveryCarService;
    @Autowired
    private OrderCommonConver orderCommonConver;


    /**
     * 取消订单
     *
     * @param orderNo          主订单号
     * @param operator         操作人
     * @param cancelReasonEnum 取消原因
     */
    public void cancel(String orderNo, String operator, PlatformCancelReasonEnum cancelReasonEnum) {
        //平台取消
        CancelOrderResDTO cancelOrderRes = platformCancelOrderHandleService.cancel(orderNo, operator, cancelReasonEnum);
        if (null != cancelOrderRes) {
            //撤销库存处理
            stockService.releaseCarStock(orderNo, cancelOrderRes.getCarNo());
            //订单取消（租客取消、车主取消、平台取消）如果使用了车主券且未支付，则退回否则不处理
            if (null != cancelOrderRes.getIsReturnOwnerCoupon() && cancelOrderRes.getIsReturnOwnerCoupon()) {
                //退还车主券
                String recover = null == cancelOrderRes.getRentCarPayStatus() || cancelOrderRes.getRentCarPayStatus() == 0 ? "1" : "0";
                couponAndCoinHandleService.undoOwnerCoupon(orderNo, cancelOrderRes.getOwnerCouponNo(), recover);
            }
            if (null != cancelOrderRes.getIsRefund() && cancelOrderRes.getIsRefund()) {
                //通知收银台退款以及退还凹凸币和钱包
                orderSettleService.settleOrderCancel(orderNo);
                //通知流程系统
                CancelOrderDeliveryVO cancelOrderDeliveryVO = orderCommonConver.buildCancelOrderDeliveryVO(orderNo,
                        cancelOrderRes);
                if (null != cancelOrderDeliveryVO) {
                    deliveryCarService.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO);
                }
            }
        }
        //TODO:平台取消消息发送
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
