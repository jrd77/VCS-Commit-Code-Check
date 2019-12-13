package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterCostSettleDetailMapper;
import com.atzuche.order.entity.AccountRenterCostSettleDetailEntity;



/**
 * 租车费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleDetailService{
    @Autowired
    private AccountRenterCostSettleDetailMapper accountRenterCostSettleDetailMapper;


}
