package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterCostSettleMapper;
import com.atzuche.order.entity.AccountRenterCostSettleEntity;



/**
 * 租客费用及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleService{
    @Autowired
    private AccountRenterCostSettleMapper accountRenterCostSettleMapper;


}
