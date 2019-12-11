package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterClaimCostDetailMapper;
import com.atzuche.order.entity.AccountRenterClaimCostDetailEntity;



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
