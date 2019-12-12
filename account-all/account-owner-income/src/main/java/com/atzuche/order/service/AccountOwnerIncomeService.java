package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountOwnerIncomeMapper;
import com.atzuche.order.entity.AccountOwnerIncomeEntity;



/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeService{
    @Autowired
    private AccountOwnerIncomeMapper accountOwnerIncomeMapper;


}
