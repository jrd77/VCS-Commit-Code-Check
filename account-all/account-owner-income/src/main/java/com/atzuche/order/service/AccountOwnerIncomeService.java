package com.atzuche.order.service;

import com.atzuche.order.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.enums.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.service.notservice.AccountOwnerIncomeDetailNoTService;
import com.atzuche.order.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.vo.req.AccountOwnerIncomeExamineReqVO;
import com.autoyol.commons.web.ErrorCode;
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


}
