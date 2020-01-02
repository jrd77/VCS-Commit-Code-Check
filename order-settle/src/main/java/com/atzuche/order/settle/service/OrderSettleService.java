package com.atzuche.order.settle.service;

import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车辆结算
 */
@Service
public class OrderSettleService {
    @Autowired private OrderSettleNoTService orderSettleNoTService;
    /**
     * 车辆押金结算
     *
     */
    @Async
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(String orderNo) {
        //1 查询租客所有费用（包含：租车费用，租客补贴费用，罚金费用，交接车产生费用）
        //2 查询车主费用 （包含 车主补贴明细表，车主罚金 车主订单采购费用，增值订单费用）
        //3
    }
}
