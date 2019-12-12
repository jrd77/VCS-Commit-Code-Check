package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterDepositMapper;
import com.atzuche.order.entity.AccountRenterDepositEntity;



/**
 * 租车押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:48:32
 */
@Service
public class AccountRenterDepositService{
    @Autowired
    private AccountRenterDepositMapper accountRenterDepositMapper;


}
