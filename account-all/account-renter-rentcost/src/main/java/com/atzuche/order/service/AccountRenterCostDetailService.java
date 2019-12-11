package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterCostDetailMapper;
import com.atzuche.order.entity.AccountRenterCostDetailEntity;



/**
 * 租车费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:53:40
 */
@Service
public class AccountRenterCostDetailService{
    @Autowired
    private AccountRenterCostDetailMapper accountRenterCostDetailMapper;


}
