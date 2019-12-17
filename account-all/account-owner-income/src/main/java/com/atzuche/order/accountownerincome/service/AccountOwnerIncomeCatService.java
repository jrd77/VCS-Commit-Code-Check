package com.atzuche.order.accountownerincome.service;

import com.atzuche.order.accountownerincome.common.Constant;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
@Slf4j
public class AccountOwnerIncomeCatService {
    @Autowired
    private AccountOwnerIncomeService accountOwnerIncomeService;



    /**
     * 查询车主收益信息
     * @param memNo
     * @return
     */
    public int getOwnerIncomeAmt(String memNo){
        return accountOwnerIncomeService.getOwnerIncomeAmt(memNo);
    }

    /**
     * 结算后产生待审核收益 落库
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq){
        log.info("AccountOwnerIncomeCatService insertOwnerIncomeExamine start param [{}]", GsonUtils.toJson(accountOwnerIncomeExamineReq));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_INSERT_OWNER_INCOME_EXAMIN, Constant.CAT_TRANSACTION_INSERT_OWNER_INCOME_EXAMIN);
        try {
            accountOwnerIncomeService.insertOwnerIncomeExamine(accountOwnerIncomeExamineReq);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountOwnerIncomeCatService insertOwnerIncomeExamine error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountOwnerIncomeCatService insertOwnerIncomeExamine end param [{}]", GsonUtils.toJson(accountOwnerIncomeExamineReq));
    }

    /**
     * 收益审核通过 更新车主收益信息
     */
    public void examineOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq){
        log.info("AccountOwnerIncomeCatService examineOwnerIncomeExamine start param [{}]", GsonUtils.toJson(accountOwnerIncomeExamineOpReq));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_EXAMINE_OWNER_INCOME_EXAMINE, Constant.CAT_TRANSACTION_EXAMINE_OWNER_INCOME_EXAMINE);
        try {
            accountOwnerIncomeService.examineOwnerIncomeExamine(accountOwnerIncomeExamineOpReq);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountOwnerIncomeCatService examineOwnerIncomeExamine error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountOwnerIncomeCatService examineOwnerIncomeExamine end param [{}]", GsonUtils.toJson(accountOwnerIncomeExamineOpReq));
    }


}
