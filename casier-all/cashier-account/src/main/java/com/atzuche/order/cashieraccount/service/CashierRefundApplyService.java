package com.atzuche.order.cashieraccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.atzuche.order.cashieraccount.mapper.CashierRefundApplyMapper;



/**
 * 退款申请表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierRefundApplyService {
    @Autowired
    private CashierRefundApplyMapper cashierRefundApplyMapper;


}
