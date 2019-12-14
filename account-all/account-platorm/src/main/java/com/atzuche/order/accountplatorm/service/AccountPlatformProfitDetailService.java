package com.atzuche.order.accountplatorm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountplatorm.mapper.AccountPlatformProfitDetailMapper;


/**
 * 平台结算收益明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 */
@Service
public class AccountPlatformProfitDetailService{
    @Autowired
    private AccountPlatformProfitDetailMapper accountPlatformProfitDetailMapper;


}
