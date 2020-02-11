package com.atzuche.order.accountrenterdetain.service;

import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainCostNoTService;
import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainDetailNoTService;
import com.atzuche.order.accountrenterdetain.vo.req.ChangeDetainRenterDepositReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * 暂扣费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:51:17
 */
@Service
public class AccountRenterDetainService {
    @Autowired
    private AccountRenterDetainCostNoTService accountRenterDetainCostNoTService;
    @Autowired
    private AccountRenterDetainDetailNoTService accountRenterDetainDetailNoTService;

    /**
     * 查询暂扣费用总和
     */
    public int getRenterDetainAmt(String orderNo ,String memNo){
        return accountRenterDetainCostNoTService.getRenterDetainAmt(orderNo,memNo);
    }

    /**
     * 暂扣费用资金进出
     */
    public void changeRenterDetainCost(ChangeDetainRenterDepositReqVO detainRenterDeposit){
        //1校验
        Assert.notNull(detainRenterDeposit, ErrorCode.PARAMETER_ERROR.getText());
        detainRenterDeposit.check();
        //2 更新违章押金总额
        accountRenterDetainCostNoTService.changeRenterDetainCost(detainRenterDeposit);
        //3 记录违章费用账户费用流水
        accountRenterDetainDetailNoTService.insertCostDetail(detainRenterDeposit);
    }
}
