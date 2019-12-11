package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountRenterDetainDetailMapper;
import com.atzuche.order.entity.AccountRenterDetainDetailEntity;



/**
 * 暂扣费用进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 */
@Service
public class AccountRenterDetainDetailService{
    @Autowired
    private AccountRenterDetainDetailMapper accountRenterDetainDetailMapper;


}
