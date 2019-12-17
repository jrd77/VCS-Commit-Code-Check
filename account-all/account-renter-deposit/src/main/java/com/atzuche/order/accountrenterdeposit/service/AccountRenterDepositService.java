package com.atzuche.order.accountrenterdeposit.service;

import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 租车押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-17 17:09:45
 */
@Service
public class AccountRenterDepositService{
    @Autowired
    private AccountRenterDepositMapper accountRenterDepositMapper;


}
