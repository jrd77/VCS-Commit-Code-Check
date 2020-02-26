package com.atzuche.order.accountrenterclaim.service;

import com.atzuche.order.accountrenterclaim.service.notservice.AccountRenterClaimCostDetailNoTService;
import com.atzuche.order.accountrenterclaim.service.notservice.AccountRenterClaimCostSettleNoTService;
import com.atzuche.order.accountrenterclaim.vo.req.AccountRenterClaimDetailReqVO;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.accountrenterclaim.mapper.AccountRenterClaimCostSettleMapper;
import org.springframework.util.Assert;


/**
 * 理赔费用/及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:47:25
 */
@Service
public class AccountRenterClaimCostSettleService{
    @Autowired
    private AccountRenterClaimCostSettleNoTService accountRenterClaimCostSettleNoTService;
    @Autowired
    private AccountRenterClaimCostDetailNoTService accountRenterClaimCostDetailNoTService;

    /**
     * 查询理赔费用总和
     */
    public int getRenterClaimCostAmt(String orderNo ,String memNo){
        return accountRenterClaimCostSettleNoTService.getRenterClaimCostAmt(orderNo,memNo);
    }

    /**
     * 理赔费用资金进出
     */
    public void changeRenterClaimCostCost(AccountRenterClaimDetailReqVO accountRenterClaimDetail){
        //1校验
        Assert.notNull(accountRenterClaimDetail, ErrorCode.PARAMETER_ERROR.getText());
        accountRenterClaimDetail.check();
        //2 更新理赔费用总额
        accountRenterClaimCostSettleNoTService.changeRenterClaimCost(accountRenterClaimDetail);
        //3 记录理赔费用账户费用流水
        accountRenterClaimCostDetailNoTService.insertCostDetail(accountRenterClaimDetail);
    }

}
