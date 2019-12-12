package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountOwnerCostSettleDetailMapper;
import com.atzuche.order.entity.AccountOwnerCostSettleDetailEntity;



/**
 * 车主费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:41:36
 */
@Service
public class AccountOwnerCostSettleDetailService{
    @Autowired
    private AccountOwnerCostSettleDetailMapper accountOwnerCostSettleDetailMapper;


}
