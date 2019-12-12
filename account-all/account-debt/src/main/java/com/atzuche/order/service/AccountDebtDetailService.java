package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountDebtDetailMapper;
import com.atzuche.order.entity.AccountDebtDetailEntity;



/**
 * 个人历史欠款明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtDetailService{
    @Autowired
    private AccountDebtDetailMapper accountDebtDetailMapper;


}
