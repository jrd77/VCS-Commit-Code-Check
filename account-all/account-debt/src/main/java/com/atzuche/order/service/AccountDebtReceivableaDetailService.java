package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountDebtReceivableaDetailMapper;
import com.atzuche.order.entity.AccountDebtReceivableaDetailEntity;



/**
 * 个人历史欠款收款记录
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtReceivableaDetailService{
    @Autowired
    private AccountDebtReceivableaDetailMapper accountDebtReceivableaDetailMapper;


}
