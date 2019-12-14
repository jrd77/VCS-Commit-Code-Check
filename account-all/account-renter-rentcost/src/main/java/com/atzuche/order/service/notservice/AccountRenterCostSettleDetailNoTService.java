package com.atzuche.order.service.notservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterCostSettleDetailMapper;



/**
 * 租车费用结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleDetailNoTService {
    @Autowired
    private AccountRenterCostSettleDetailMapper accountRenterCostSettleDetailMapper;


}
