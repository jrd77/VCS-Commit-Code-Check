package com.atzuche.order.coreapi.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * 订单取消
     *
     * @param cancelOrderReqVO 请求参数
     */
    public void cancel(CancelOrderReqVO cancelOrderReqVO) {
        //公共校验
        boolean isConsoleInvoke = StringUtils.isNotBlank(cancelOrderReqVO.getOperatorName());
        cancelOrderCheckService.checkCancelOrder(cancelOrderReqVO, isConsoleInvoke);
        //取消处理
        CancelOrderResDTO res = null;
        if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), cancelOrderReqVO.getMemRole())) {
            //租客取消
            res = renterCancelOrderService.cancel(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getCancelReason(),isConsoleInvoke);
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqVO.getMemRole())) {
            //车主取消
            res = ownerCancelOrderService.cancel(cancelOrderReqVO.getOrderNo(), cancelOrderReqVO.getCancelReason(), isConsoleInvoke);
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

        //消息发送
        //TODO:发送订单取消事件

    }

    private OwnerCancelDTO buildOwnerCancelDTO(String orderNo, CancelOrderResDTO res){
        OwnerCancelDTO ownerCancelDTO = new OwnerCancelDTO();
        ownerCancelDTO.setOrderNo(orderNo);
        ownerCancelDTO.setCarNo(res.getCarNo());
        ownerCancelDTO.setCityCode(Integer.valueOf(res.getCityCode()));
        ownerCancelDTO.setSource(2);
        ownerCancelDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(res.getRentTime()));
        ownerCancelDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(res.getRevertTime()));
        return ownerCancelDTO;
    }

}
