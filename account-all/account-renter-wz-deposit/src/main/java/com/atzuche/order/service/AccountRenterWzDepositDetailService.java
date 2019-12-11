package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterWzDepositDetailMapper;
import com.atzuche.order.entity.AccountRenterWzDepositDetailEntity;



/**
 * 违章押金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositDetailService{
    @Autowired
    private AccountRenterWzDepositDetailMapper accountRenterWzDepositDetailMapper;


}
