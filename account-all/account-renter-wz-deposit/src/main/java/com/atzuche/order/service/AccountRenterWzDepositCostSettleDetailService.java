package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterWzDepositCostSettleDetailMapper;
import com.atzuche.order.entity.AccountRenterWzDepositCostSettleDetailEntity;



/**
 * 违章费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositCostSettleDetailService{
    @Autowired
    private AccountRenterWzDepositCostSettleDetailMapper accountRenterWzDepositCostSettleDetailMapper;


}
