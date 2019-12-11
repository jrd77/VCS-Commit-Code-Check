package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterWzDepositCostMapper;
import com.atzuche.order.entity.AccountRenterWzDepositCostEntity;



/**
 * 违章费用总表及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:58:17
 */
@Service
public class AccountRenterWzDepositCostService{
    @Autowired
    private AccountRenterWzDepositCostMapper accountRenterWzDepositCostMapper;


}
