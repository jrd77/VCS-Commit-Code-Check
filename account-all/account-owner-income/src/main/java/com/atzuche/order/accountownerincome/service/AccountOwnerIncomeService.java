package com.atzuche.order.accountownerincome.service;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;


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
    public int getOwnerIncomeAmt(String memNo){
        return accountOwnerIncomeNoTService.getOwnerIncomeAmt(memNo);
    }

    /**
     * 结算后产生待审核收益 落库
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq){
        Assert.notNull(accountOwnerIncomeExamineReq, ErrorCode.PARAMETER_ERROR.getText());
        accountOwnerIncomeExamineReq.check();
        accountOwnerIncomeExamineNoTService.insertOwnerIncomeExamine(accountOwnerIncomeExamineReq);
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

    /**
     * 查询订单车主收益，明细
     */
    public List<AccountOwnerIncomeDetailEntity> getOwnerRealIncomeByOrder(String orderNo,String memNo) {
        List<AccountOwnerIncomeDetailEntity> list = accountOwnerIncomeDetailNoTService.selectByOrderNo(orderNo,memNo);
        return list;
    }

    /**
     * 查询订单车主收益待审核明细
     */
    public List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrder(String orderNo,String memNo) {
        List<AccountOwnerIncomeExamineEntity> list = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineByOrderNo(orderNo,memNo);
        return list;
    }
}
