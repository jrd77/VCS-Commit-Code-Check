package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountOwnerCostSettleMapper;
import com.atzuche.order.entity.AccountOwnerCostSettleEntity;



/**
 *   车主结算费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
 */
@Service
public class AccountOwnerCostSettleService{
    @Autowired
    private AccountOwnerCostSettleMapper accountOwnerCostSettleMapper;


}
