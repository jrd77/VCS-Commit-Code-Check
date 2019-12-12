package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterWzDepositCostDetailMapper;
import com.atzuche.order.entity.AccountRenterWzDepositCostDetailEntity;



/**
 * 违章费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositCostDetailService{
    @Autowired
    private AccountRenterWzDepositCostDetailMapper accountRenterWzDepositCostDetailMapper;


}
