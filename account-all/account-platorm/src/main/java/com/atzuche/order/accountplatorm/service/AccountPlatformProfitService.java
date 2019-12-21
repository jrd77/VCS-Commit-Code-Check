package com.atzuche.order.accountplatorm.service;

import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitNoTService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformSubsidyDetailNoTService;
import com.atzuche.order.accountplatorm.vo.res.AccountPlatformProfitResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 *  平台订单收益结算表
 *
 * @author ZhangBin
 * @date 2019-12-21 14:38:34
 */
@Service
public class AccountPlatformProfitService{
    @Autowired
    private AccountPlatformProfitNoTService accountPlatformProfitNoTService;
    @Autowired
    private AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
    @Autowired
    private AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;

    /**
     *  查询订单结算信息
     * @param orderNo
     * @return
     */
    public AccountPlatformProfitResVO getAccountPlatformProfit(String orderNo){
        return accountPlatformProfitNoTService.getAccountPlatformProfit(orderNo);
    }
}
