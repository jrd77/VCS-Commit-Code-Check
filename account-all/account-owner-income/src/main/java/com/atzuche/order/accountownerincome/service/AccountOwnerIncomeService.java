package com.atzuche.order.accountownerincome.service;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;


/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Slf4j
@Service
public class AccountOwnerIncomeService{
    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;
    @Autowired
    private AccountOwnerIncomeDetailNoTService accountOwnerIncomeDetailNoTService;
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;


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
     *
     * @param accountOwnerIncomeExamineOpReq 车主收益信息
     * @param isSecondFlag                   是否二清订单
     * @return int 收益明细ID
     */
    @Transactional(rollbackFor=Exception.class)
    public int examineOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq, boolean isSecondFlag) {
        //1参数校验
        Assert.notNull(accountOwnerIncomeExamineOpReq, ErrorCode.PARAMETER_ERROR.getText());
        accountOwnerIncomeExamineOpReq.check();
        //2 更新审核表状态
        accountOwnerIncomeExamineNoTService.updateOwnerIncomeExamine(accountOwnerIncomeExamineOpReq);
        //3 更新车主收益表
        if (AccountOwnerIncomeExamineStatus.PASS_EXAMINE.getStatus() == accountOwnerIncomeExamineOpReq.getStatus()) {
            //3.1 新增收益明细并返回
            AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail = accountOwnerIncomeDetailNoTService.insertAccountOwnerIncomeDetail(accountOwnerIncomeExamineOpReq);
            //3.2 更新车主收益总和
            accountOwnerIncomeNoTService.updateOwnerIncomeAmt(accountOwnerIncomeDetail, isSecondFlag);
            return accountOwnerIncomeDetail.getId();
        }
        return 0;
    }

    /**
     * 查询订单车主收益，明细
     */
    public List<AccountOwnerIncomeDetailEntity> getOwnerRealIncomeByOrder(String orderNo,String memNo) {
        return accountOwnerIncomeDetailNoTService.selectByOrderNo(orderNo,memNo);
    }

    /**
     * 查询订单车主收益待审核明细
     */
    public List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrder(String orderNo,String memNo) {
        return accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineByOrderNo(orderNo,memNo);
    }

    /**
     * 查询订单车主收益待审核明细
     */
    public List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrderAndType(String orderNo,String memNo,int type) {
        return accountOwnerIncomeExamineNoTService.getOwnerIncomeByOrderAndType(orderNo,memNo,type);
    }
}
