package com.atzuche.order.accountrenterdeposit.service;

import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租车押金资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:48:32
 */
@Service
public class AccountRenterDepositDetailService{
    @Autowired
    private AccountRenterDepositDetailMapper accountRenterDepositDetailMapper;


}
