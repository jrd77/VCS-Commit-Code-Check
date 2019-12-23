package com.atzuche.order.accountplatorm.service;

import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitNoTService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformSubsidyDetailNoTService;
import com.atzuche.order.accountplatorm.vo.req.AccountPlatformProfitReqVO;
import com.atzuche.order.accountplatorm.vo.res.AccountPlatformProfitResVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


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

    /**
     * 平台结算 结算信息落库
     */
    public void insertAccountPlat(AccountPlatformProfitReqVO accountPlatformProfit){
        //1参数校验
        Assert.notNull(accountPlatformProfit, ErrorCode.PARAMETER_ERROR.getText());
        accountPlatformProfit.check();
        //2 平台结算主表落库
        accountPlatformProfitNoTService.insertAccountPlatformProfit(accountPlatformProfit);
        //3订单补贴信息落库
        accountPlatformSubsidyDetailNoTService.insertAccountPlatformSubsidyDetail(accountPlatformProfit.getAccountPlatformSubsidyDetails());
        //4 结算收益信息落库
        accountPlatformProfitDetailNotService.insertAccountPlatformProfitDetail(accountPlatformProfit.getAccountPlatformProfitDetail());
    }
}
