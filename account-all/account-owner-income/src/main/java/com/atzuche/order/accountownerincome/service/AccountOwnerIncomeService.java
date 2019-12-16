package com.atzuche.order.accountownerincome.service;

import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.enums.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.autoyol.commons.web.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeService{
    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;
    @Autowired
    private AccountOwnerIncomeDetailNoTService accountOwnerIncomeDetailNoTService;
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;


    /**
     * 查询车主收益信息
     * @param memNo
     * @return
     */
    public int getOwnerIncomeAmt(Integer memNo){
        return accountOwnerIncomeNoTService.getOwnerIncomeAmt(memNo);
    }

    /**
     * 结算后产生待审核收益 落库
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq){
        Assert.notNull(accountOwnerIncomeExamineReq, ErrorCode.PARAMETER_ERROR.getText());
        Transaction t = Cat.newTransaction("URL", "pageName");
        try {
            Cat.logEvent("URL.Server", "serverIp", Event.SUCCESS, "ip=${serverIp}");
            Cat.logMetricForCount("metric.key");
            Cat.logMetricForDuration("metric.key", 5);

            accountOwnerIncomeExamineReq.check();
            accountOwnerIncomeExamineNoTService.insertOwnerIncomeExamine(accountOwnerIncomeExamineReq);

            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
    }

    /**
     * 收益审核通过 更新车主收益信息
     */
    public void examineOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq){
        //1参数校验
        Assert.notNull(accountOwnerIncomeExamineOpReq, ErrorCode.PARAMETER_ERROR.getText());
        accountOwnerIncomeExamineOpReq.check();
        //2 更新审核表状态
        accountOwnerIncomeExamineNoTService.updateOwnerIncomeExamine(accountOwnerIncomeExamineOpReq);
        //3 更新车主收益表
        if(AccountOwnerIncomeExamineStatus.PASS_EXAMINE.equals(accountOwnerIncomeExamineOpReq.getStatus())){
            //3.1 新增收益明细并返回
            AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail = accountOwnerIncomeDetailNoTService.insertAccountOwnerIncomeDetail(accountOwnerIncomeExamineOpReq);
            //3.2 更新车主收益总和
            accountOwnerIncomeNoTService.updateOwnerIncomeAmt(accountOwnerIncomeDetail);
        }

    }


}
