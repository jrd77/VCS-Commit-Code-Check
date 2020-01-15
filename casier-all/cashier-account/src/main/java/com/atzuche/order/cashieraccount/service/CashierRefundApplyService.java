package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.cashieraccount.mapper.CashierRefundApplyMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * CashierRefundApplyService
 *
 * @author shisong
 * @date 2020/1/14
 */
@Service
public class CashierRefundApplyService {

    @Resource
    private CashierRefundApplyMapper cashierRefundApplyMapper;

    public Date queryRefundTimeByOrderNo(String orderNo, String payKind) {
        return cashierRefundApplyMapper.queryRefundTimeByOrderNo(orderNo,payKind);
    }
}
