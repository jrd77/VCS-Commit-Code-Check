package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterDepositDetailMapper;
import com.atzuche.order.entity.AccountRenterDepositDetailEntity;



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
