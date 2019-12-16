package com.atzuche.order.accountrenterclaim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterclaim.mapper.AccountRenterClaimCostSettleMapper;


/**
 * 理赔费用/及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Service
public class AccountRenterClaimCostSettleService{
    @Autowired
    private AccountRenterClaimCostSettleMapper accountRenterClaimCostSettleMapper;


}
