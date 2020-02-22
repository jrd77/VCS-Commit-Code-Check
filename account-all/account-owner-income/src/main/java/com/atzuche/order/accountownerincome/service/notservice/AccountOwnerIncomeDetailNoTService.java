package com.atzuche.order.accountownerincome.service.notservice;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeDetailMapper;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeDetailType;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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


    public AccountOwnerIncomeDetailEntity insertAccountOwnerIncomeDetail(AccountOwnerIncomeExamineOpReqVO vo) {
        //1 查询审核通过的  收益审核信息
        List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamines = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineByOrderNo(vo.getOrderNo(),vo.getMemNo());
        if(CollectionUtils.isEmpty(accountOwnerIncomeExamines)){
            throw new AccountOwnerIncomeExamineException();
        }
        int amt  = accountOwnerIncomeExamines.stream().mapToInt(AccountOwnerIncomeExamineEntity::getAmt).sum();
        Assert.isTrue(amt>0,"车主收益金额不合法");
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail = new AccountOwnerIncomeDetailEntity();
        accountOwnerIncomeDetail.setAmt(amt);
        accountOwnerIncomeDetail.setIncomeExamineId(vo.getAccountOwnerIncomeExamineId());
        accountOwnerIncomeDetail.setCreateOp(vo.getOpName());
        accountOwnerIncomeDetail.setDetail(vo.getDetail());
        accountOwnerIncomeDetail.setMemNo(vo.getMemNo());
        accountOwnerIncomeDetail.setOrderNo(vo.getOrderNo());
        accountOwnerIncomeDetail.setTime(now);
        accountOwnerIncomeDetail.setType(AccountOwnerIncomeDetailType.INCOME.getType());
        accountOwnerIncomeDetail.setCostCode(OwnerCashCodeEnum.ACCOUNT_OWNER_INCOME.getCashNo());
        accountOwnerIncomeDetail.setCostDetail(OwnerCashCodeEnum.ACCOUNT_OWNER_INCOME.getTxt());
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
