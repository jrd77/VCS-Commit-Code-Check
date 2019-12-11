package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountOwnerIncomeExamineMapper;
import com.atzuche.order.entity.AccountOwnerIncomeExamineEntity;



/**
 * 车主收益待审核表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeExamineService{
    @Autowired
    private AccountOwnerIncomeExamineMapper accountOwnerIncomeExamineMapper;


}
