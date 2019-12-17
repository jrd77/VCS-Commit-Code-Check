package com.atzuche.order.accountownercost.service;

import com.atzuche.order.accountownercost.common.Constant;
import com.atzuche.order.accountownercost.vo.req.AccountOwnerCostSettleReqVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *   车主结算费用总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:41:37
 */
@Service
@Slf4j
public class AccountOwnerCostSettleCatService {
    @Autowired
    private AccountOwnerCostSettleService accountOwnerCostSettleService;


    /**
     * 车主结算信息插入
     * @param accountOwnerCostSettleReqVO
     */
    public void insertAccountOwnerCostSettle(AccountOwnerCostSettleReqVO accountOwnerCostSettleReqVO){
        log.info("AccountOwnerCostSettleCatService insertAccountOwnerCostSettle start param [{}]", GsonUtils.toJson(accountOwnerCostSettleReqVO));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_INSERT_ACCOUNT_OWNER_COST_STTLE, Constant.CAT_TRANSACTION_INSERT_ACCOUNT_OWNER_COST_STTLE);
        try {
            accountOwnerCostSettleService.insertAccountOwnerCostSettle(accountOwnerCostSettleReqVO);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountOwnerCostSettleCatService insertAccountOwnerCostSettle error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountOwnerCostSettleCatService insertAccountOwnerCostSettle end param [{}]", GsonUtils.toJson(accountOwnerCostSettleReqVO));

    }
}
