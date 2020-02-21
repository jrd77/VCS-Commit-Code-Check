package com.atzuche.order.accountownerincome.service.notservice;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeDetailMapper;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeDetailType;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.OrderCoseSourceCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * 车主收益资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeDetailNoTService {
    @Autowired
    private AccountOwnerIncomeDetailMapper accountOwnerIncomeDetailMapper;
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;


    public AccountOwnerIncomeDetailEntity insertAccountOwnerIncomeDetail(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq) {
        //1 查询审核通过的  收益审核信息
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamine = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineById(accountOwnerIncomeExamineOpReq.getAccountOwnerIncomeExamineId());
        if(Objects.isNull(accountOwnerIncomeExamine) || Objects.isNull(accountOwnerIncomeExamine.getId())){
            throw new AccountOwnerIncomeExamineException();
        }
        //1.1 审核不通过 抛异常 不能增加收益
        if(!AccountOwnerIncomeExamineStatus.PASS_EXAMINE.equals(accountOwnerIncomeExamine.getStatus())){
            throw new AccountOwnerIncomeExamineException();
        }
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail = new AccountOwnerIncomeDetailEntity();
        accountOwnerIncomeDetail.setAmt(accountOwnerIncomeExamine.getAmt());
        accountOwnerIncomeDetail.setIncomeExamineId(accountOwnerIncomeExamine.getId());
        accountOwnerIncomeDetail.setCreateOp(accountOwnerIncomeExamineOpReq.getOpName());
        accountOwnerIncomeDetail.setDetail(accountOwnerIncomeExamineOpReq.getDetail());
        accountOwnerIncomeDetail.setMemNo(accountOwnerIncomeExamine.getMemNo());
        accountOwnerIncomeDetail.setOrderNo(accountOwnerIncomeExamine.getOrderNo());
        accountOwnerIncomeDetail.setTime(now);
        accountOwnerIncomeDetail.setType(AccountOwnerIncomeDetailType.INCOME.getType());
        accountOwnerIncomeDetail.setCostCode(OrderCoseSourceCode.OWNER_COST_SETTLE.getCode());
        accountOwnerIncomeDetail.setCostDetail(OrderCoseSourceCode.OWNER_COST_SETTLE.getDesc());
        int result = accountOwnerIncomeDetailMapper.insertSelective(accountOwnerIncomeDetail);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
        return accountOwnerIncomeDetail;
    }


    public List<AccountOwnerIncomeDetailEntity> selectByOrderNo(String orderNo,String memNo){
        return accountOwnerIncomeDetailMapper.selectByOrderNo(orderNo,memNo);
    }
}
