package com.atzuche.order.service;

import com.atzuche.order.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.service.notservice.AccountRenterCostSettleNoTService;
import com.atzuche.order.vo.req.AccountRenterCostReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * 租客费用及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
public class AccountRenterCostSettleService{
    @Autowired
    private AccountRenterCostSettleNoTService accountRenterCostSettleNoTService;
    @Autowired
    private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;

    /**
     * 收银台收款支付成功  实收费用落库
     */
    public void insertRenterCostSettle(AccountRenterCostReqVO accountRenterCost){
        //1 参数校验
        Assert.notNull(accountRenterCost, ErrorCode.PARAMETER_ERROR.getText());
        accountRenterCost.check();
        //2费用明细落库
        accountRenterCostDetailNoTService.insertAccountRenterCostDetail(accountRenterCost.getAccountRenterCostDetailReqVO());
        //3费用信息落库
        accountRenterCostSettleNoTService.insertOrUpdateRenterCostSettle(accountRenterCost);

    }



}
