package com.atzuche.order.accountownerincome.service.notservice;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeDetailMapper;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeMapper;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeDetailType;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.entity.orderDetailDto.AccountOwnerIncomeListDTO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeNoTService {
    @Autowired
    private AccountOwnerIncomeMapper accountOwnerIncomeMapper;
    @Autowired
    private AccountOwnerIncomeDetailMapper accountOwnerIncomeDetailMapper;



    public AccountOwnerIncomeEntity getOwnerIncome(String memNo) {
        if(Objects.isNull(memNo)){
            Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        }
        AccountOwnerIncomeEntity accountOwnerIncome = accountOwnerIncomeMapper.selectByMemNo(memNo);
        if(Objects.isNull(accountOwnerIncome) || Objects.isNull(accountOwnerIncome.getId())){
            accountOwnerIncome = new AccountOwnerIncomeEntity();
            accountOwnerIncome.setMemNo(memNo);
            accountOwnerIncome.setVersion(NumberUtils.INTEGER_ONE);
            accountOwnerIncome.setIncomeAmt(NumberUtils.INTEGER_ZERO);
            accountOwnerIncomeMapper.insertSelective(accountOwnerIncome);
        }
        return accountOwnerIncome;
    }

    public AccountOwnerIncomeEntity getOwnerIncomeByMemNO(String memNo) {
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        AccountOwnerIncomeEntity accountOwnerIncome = accountOwnerIncomeMapper.selectByMemNo(memNo);
        if(Objects.isNull(accountOwnerIncome) || Objects.isNull(accountOwnerIncome.getId())){
            accountOwnerIncome = new AccountOwnerIncomeEntity();
            accountOwnerIncome.setIncomeAmt(OrderConstant.ZERO);
            accountOwnerIncome.setSecondaryIncomeAmt(OrderConstant.ZERO);
        }
        return accountOwnerIncome;
    }

    /**
     * 更新车主收益
     * @param accountOwnerIncomeDetail
     */
    public void updateOwnerIncomeAmt(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail) {
        AccountOwnerIncomeEntity accountOwnerIncome = getOwnerIncome(accountOwnerIncomeDetail.getMemNo());
        int amt = accountOwnerIncome.getIncomeAmt() + accountOwnerIncomeDetail.getAmt();
        if(amt<0){
            throw new AccountOwnerIncomeExamineException();
        }
        accountOwnerIncome.setIncomeAmt(amt);
        int result = accountOwnerIncomeMapper.updateByPrimaryKey(accountOwnerIncome);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
    }


    /**
     * 更新车主收益(提现)
     * @param accountOwnerIncomeEntity
     */
    public void updateOwnerIncomeAmtForCashWith(AccountOwnerIncomeEntity accountOwnerIncomeEntity) {
        if(accountOwnerIncomeEntity == null){
            throw new AccountOwnerIncomeExamineException();
        }
        int result = accountOwnerIncomeMapper.updateByPrimaryKey(accountOwnerIncomeEntity);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
    }

    public AccountOwnerIncomeEntity getOwnerIncomeByMemNo(String memNo) {
        if(Objects.isNull(memNo)){
            Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        }
        AccountOwnerIncomeEntity accountOwnerIncome = accountOwnerIncomeMapper.selectByMemNo(memNo);
        return accountOwnerIncome;
    }
    
    
    /**
     * 更新收益并保存明细
     * @param accountOwnerIncomeDetail
     */
    public void updateTotalIncomeAndSaveDetail(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail) {
    	// 更新收益
    	updateOwnerIncomeAmt(accountOwnerIncomeDetail);
    	// 保存收益详情
        accountOwnerIncomeDetailMapper.insertSelective(accountOwnerIncomeDetail);
    }

    public List<AccountOwnerIncomeListDTO> getIncomTotalListByMemNoList(List<Integer> memNoList) {
        List<AccountOwnerIncomeEntity> accountOwnerIncomeEntities = accountOwnerIncomeMapper.selectByMemNoList(memNoList);
        List<AccountOwnerIncomeListDTO> accountOwnerIncomeList=  new ArrayList<>();
        if(CollectionUtils.isNotEmpty(accountOwnerIncomeEntities)){
            for (AccountOwnerIncomeEntity accountOwnerIncomeEntity : accountOwnerIncomeEntities) {
                AccountOwnerIncomeListDTO accountOwnerIncomeListDTO = new AccountOwnerIncomeListDTO();
                accountOwnerIncomeListDTO.setIncomeAmt(accountOwnerIncomeEntity.getIncomeAmt());
                accountOwnerIncomeListDTO.setMemNo(accountOwnerIncomeEntity.getMemNo());
                accountOwnerIncomeList.add(accountOwnerIncomeListDTO);
            }
        }
        return accountOwnerIncomeList;
    }
}
