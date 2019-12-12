package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterStopCostDetailMapper;
import com.atzuche.order.entity.AccountRenterStopCostDetailEntity;



/**
 * 停运费资金进出明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Service
public class AccountRenterStopCostDetailService{
    @Autowired
    private AccountRenterStopCostDetailMapper accountRenterStopCostDetailMapper;


}
