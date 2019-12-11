package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterWzDepositMapper;
import com.atzuche.order.entity.AccountRenterWzDepositEntity;



/**
 * 违章押金状态及其总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositService{
    @Autowired
    private AccountRenterWzDepositMapper accountRenterWzDepositMapper;


}
