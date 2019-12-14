package com.atzuche.order.accountrenterclaim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterclaim.mapper.AccountRenterClaimCostDetailMapper;


/**
 * 理赔费用资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Service
public class AccountRenterClaimCostDetailService{
    @Autowired
    private AccountRenterClaimCostDetailMapper accountRenterClaimCostDetailMapper;


}
