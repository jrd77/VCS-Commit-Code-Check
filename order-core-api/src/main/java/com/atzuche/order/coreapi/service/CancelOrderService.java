package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.settle.service.OrderSettleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private StockService stockService;

    @Autowired
    private DeliveryCarService deliveryCarService;

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

        //扣库存
        if (null != res) {
            stockService.releaseCarStock(cancelOrderReqVO.getOrderNo(), res.getCarNo());
        }

        //通知收银台退款
        if (null != res && null != res.getIsRefund() && res.getIsRefund()) {
            orderSettleService.settleOrderCancel(cancelOrderReqVO.getOrderNo());
        }

        //通知流程系统
        if (null != res) {
            CancelOrderDeliveryVO cancelOrderDeliveryVO = buildCancelOrderDeliveryVO(cancelOrderReqVO.getOrderNo(),
                    res);
            if (null != cancelOrderDeliveryVO) {
                deliveryCarService.cancelRenYunFlowOrderInfo(cancelOrderDeliveryVO);
            }
        }

        //消息发送
        //todo

    }


    public void check() {
        //todo

    }


    /**
     * 仁云流程系统请求信息处理
     *
     * @param orderNo 主订单号
     * @param res     取消订单返回信息
     * @return CancelOrderDeliveryVO 仁云流程系统请求信息
     */
    public CancelOrderDeliveryVO buildCancelOrderDeliveryVO(String orderNo, CancelOrderResDTO res) {
        if (!res.getSrvGetFlag() && !res.getSrvReturnFlag()) {
            return null;
        }
        String servicetype = "";
        if (res.getSrvGetFlag() && res.getSrvReturnFlag()) {
            servicetype = "all";
        } else if (res.getSrvGetFlag()) {
            servicetype = "take";
        } else if (res.getSrvReturnFlag()) {
            servicetype = "back";
        }
        CancelOrderDeliveryVO cancelOrderDeliveryVO = new CancelOrderDeliveryVO();

        CancelFlowOrderDTO cancelFlowOrderDTO = new CancelFlowOrderDTO();
        cancelFlowOrderDTO.setOrdernumber(orderNo);
        cancelFlowOrderDTO.setServicetype(servicetype);

        cancelOrderDeliveryVO.setRenterOrderNo(res.getRenterOrderNo());
        cancelOrderDeliveryVO.setCancelFlowOrderDTO(cancelFlowOrderDTO);
        return cancelOrderDeliveryVO;
    }

}
