package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterDetainCostMapper;
import com.atzuche.order.entity.AccountRenterDetainCostEntity;



/**
 * 暂扣费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 */
@Service
public class AccountRenterDetainCostService{
    @Autowired
    private AccountRenterDetainCostMapper accountRenterDetainCostMapper;


}
