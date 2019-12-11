package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountOwnerIncomeDetailMapper;
import com.atzuche.order.entity.AccountOwnerIncomeDetailEntity;



/**
 * 车主收益资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeDetailService{
    @Autowired
    private AccountOwnerIncomeDetailMapper accountOwnerIncomeDetailMapper;


}
