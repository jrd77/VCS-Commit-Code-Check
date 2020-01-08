package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.settle.service.OrderSettleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * 订单取消操作
 *
 * @author pengcheng.fu
 * @date 2020/1/7 15:45
 */

@Service
public class CancelOrderService {

    @Autowired
    private RenterCancelOrderService renterCancelOrderService;

    @Autowired
    private OwnerCancelOrderService ownerCancelOrderService;

    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;

    @Autowired
    private OrderSettleService orderSettleService;

    /**
     * 订单取消
     *
     * @param cancelOrderReqVO 请求参数
     */
    public void cancel(CancelOrderReqVO cancelOrderReqVO) {
        //公共校验
        check();

        //取消处理
        CancelOrderResDTO res = null;
        if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), cancelOrderReqVO.getMemRole())) {
            //租客取消
            res = renterCancelOrderService.cancel(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getCancelReason());
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqVO.getMemRole())) {
            //车主取消
            res = ownerCancelOrderService.cancel(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getCancelReason());
        }

        //优惠券
        if (null != res && null != res.getIsReturnDisCoupon() && res.getIsReturnDisCoupon()) {
            //退还优惠券(平台券+送取服务券)
            couponAndCoinHandleService.undoPlatformCoupon(cancelOrderReqVO.getOrderNo());
            couponAndCoinHandleService.undoPlatformCoupon(cancelOrderReqVO.getOrderNo());
        }
        //订单取消（租客取消、车主取消、平台取消）如果使用了车主券且未支付，则退回否则不处理
        if (null != res && null != res.getIsReturnOwnerCoupon() && res.getIsReturnOwnerCoupon()) {
            //退还车主券
            String recover = null == res.getRentCarPayStatus() || res.getRentCarPayStatus() == 0 ? "1" : "0";
            couponAndCoinHandleService.undoOwnerCoupon(cancelOrderReqVO.getOrderNo(), res.getOwnerCouponNo(), recover);
        }

        //通知收银台退款
        //todo
        orderSettleService.settleOrderCancel(cancelOrderReqVO.getOrderNo());
        //通知流程系统
        //todo

        //消息发送
        //todo

    }


    public void check() {
        //todo

    }

}
