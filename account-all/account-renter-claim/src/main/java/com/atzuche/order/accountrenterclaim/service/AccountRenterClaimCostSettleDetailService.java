package com.atzuche.order.accountrenterclaim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterclaim.mapper.AccountRenterClaimCostSettleDetailMapper;


/**
 * 理赔费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Service
public class AccountRenterClaimCostSettleDetailService{
    @Autowired
    private AccountRenterClaimCostSettleDetailMapper accountRenterClaimCostSettleDetailMapper;


}
