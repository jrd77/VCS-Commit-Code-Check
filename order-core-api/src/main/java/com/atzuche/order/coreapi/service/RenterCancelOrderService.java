package com.atzuche.order.coreapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 租客取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:21
 */

@Service
public class RenterCancelOrderService {

    @Autowired
    private CouponAndCoinHandleService couponAndCoinHandleService;

    public void cancel() {
        //校验
        //todo

        //调度判定
        //todo

        //罚金计算
        //todo

        //优惠券、凹凸币退回(钱包收银台处理)
        //todo

        //订单状态更新
        //todo

        //去库存
        //todo

        //落库
        //todo

    }


}
