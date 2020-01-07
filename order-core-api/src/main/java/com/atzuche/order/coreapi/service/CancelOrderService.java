package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.vo.req.CancelOrderReqVO;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
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
            res = renterCancelOrderService.cancel();
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), cancelOrderReqVO.getMemRole())) {
            res = ownerCancelOrderService.cancel();
        }

        //优惠券、凹凸币退回(钱包收银台处理)
        //todo

        //通知收银台退款
        //todo

        //通知流程系统
        //todo

        //消息发送
        //todo

    }


    public void check() {
        //todo

    }

}
