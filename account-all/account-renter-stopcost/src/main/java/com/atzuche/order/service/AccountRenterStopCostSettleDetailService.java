package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterStopCostSettleDetailMapper;
import com.atzuche.order.entity.AccountRenterStopCostSettleDetailEntity;



/**
 * 停运费结算明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:54:29
 */
@Service
public class AccountRenterStopCostSettleDetailService{
    @Autowired
    private AccountRenterStopCostSettleDetailMapper accountRenterStopCostSettleDetailMapper;


}
