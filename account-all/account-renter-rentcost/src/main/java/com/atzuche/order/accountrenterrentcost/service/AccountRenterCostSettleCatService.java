package com.atzuche.order.accountrenterrentcost.service;

import com.atzuche.order.accountrenterrentcost.common.Constant;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客费用及其结算总表
 *
 * @author ZhangBin
 * @date 2019-12-13 16:49:57
 */
@Service
@Slf4j
public class AccountRenterCostSettleCatService {
    @Autowired
    private AccountRenterCostSettleService accountRenterCostSettleService;

    /**
     * 查询订单 已付租车费用
     */
    public int getCostPaidRent(String orderNo,String memNo) {
        return accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
    }

    /**
     * 收银台支付成功  实收租车费用落库
     */
    public void insertRenterCostDetail(AccountRenterCostDetailReqVO accountRenterCostDetail){
        log.info("AccountRenterCostSettleCatService insertRenterCostDetail start param [{}]", GsonUtils.toJson(accountRenterCostDetail));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_INSERT_RENTER_COST_DETAIL, Constant.CAT_TRANSACTION_INSERT_RENTER_COST_DETAIL);
        try {
            accountRenterCostSettleService.insertRenterCostDetail(accountRenterCostDetail);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountRenterCostSettleCatService deductDebt error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountRenterCostSettleCatService insertRenterCostDetail end param [{}]", GsonUtils.toJson(accountRenterCostDetail));
    }



}
