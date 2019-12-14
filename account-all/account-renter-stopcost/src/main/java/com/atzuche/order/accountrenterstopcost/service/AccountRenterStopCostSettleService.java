package com.atzuche.order.accountrenterstopcost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterstopcost.mapper.AccountRenterStopCostSettleMapper;


/**
 * 停运费状态及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Service
public class AccountRenterStopCostSettleService{
    @Autowired
    private AccountRenterStopCostSettleMapper accountRenterStopCostSettleMapper;


}
