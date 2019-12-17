package com.atzuche.order.accountdebt.service;

import com.atzuche.order.accountdebt.common.Constant;
import com.atzuche.order.accountdebt.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.accountdebt.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.accountdebt.vo.res.AccountDebtResVO;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
@Slf4j
public class AccountDebtCatService {
    @Autowired
    private AccountDebtService accountDebtService;

    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     */
    public AccountDebtResVO getAccountDebtByMemNo(Integer memNo) {
        log.info("AccountDebtCatService getAccountDebtByMemNo start param [{}]", memNo);
        AccountDebtResVO result = accountDebtService.getAccountDebtByMemNo(memNo);
        log.info("AccountDebtCatService getAccountDebtByMemNo end  param [{}] ，result [{}]", memNo,result);
        return result;
    }

    /**
     * 查看账户欠款总和
     * @param memNo
     * @return
     */
    public Integer getAccountDebtNumByMemNo(Integer memNo){
        log.info("AccountDebtCatService getAccountDebtNumByMemNo start param [{}]", memNo);
        Integer result = accountDebtService.getAccountDebtNumByMemNo(memNo);
        log.info("AccountDebtCatService getAccountDebtNumByMemNo end  param [{}] ，result [{}]", memNo,result);
        return result;
    }

    /**
     * 抵扣历史欠款
     * @return
     */
    public void deductDebt(AccountDeductDebtReqVO accountDeductDebt) {
        log.info("AccountDebtCatService deductDebt start param [{}]", GsonUtils.toJson(accountDeductDebt));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_DEDUCT_DEBT, Constant.CAT_TRANSACTION_DEDUCT_DEBT);
        try {
            accountDebtService.deductDebt(accountDeductDebt);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountDebtCatService deductDebt error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountDebtCatService deductDebt end param [{}]", GsonUtils.toJson(accountDeductDebt));
    }

    /**
     * 记录用户历史欠款
     */
    public void insertDebt(AccountInsertDebtReqVO accountInsertDebt){
        log.info("AccountDebtCatService insertDebt start param [{}]", GsonUtils.toJson(accountInsertDebt));
        Transaction t = Cat.newTransaction(Constant.CAT_TRANSACTION_INSERT_DEBT, Constant.CAT_TRANSACTION_INSERT_DEBT);
        try {
            accountDebtService.insertDebt(accountInsertDebt);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            log.error("AccountDebtCatService insertDebt error e [{}]",e);
        } finally {
            t.complete();
        }
        log.info("AccountDebtCatService insertDebt end param [{}]", GsonUtils.toJson(accountInsertDebt));
    }
}
