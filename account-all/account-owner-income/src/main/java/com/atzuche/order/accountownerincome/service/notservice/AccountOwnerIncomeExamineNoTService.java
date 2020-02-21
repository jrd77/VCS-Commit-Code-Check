package com.atzuche.order.accountownerincome.service.notservice;

import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeSettleException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeExamineMapper;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * 车主收益待审核表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeExamineNoTService {
    @Autowired
    private AccountOwnerIncomeExamineMapper accountOwnerIncomeExamineMapper;


    /**
     * 结算后产生待审核收益 落库
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq) {
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntity = new AccountOwnerIncomeExamineEntity();
        BeanUtils.copyProperties(accountOwnerIncomeExamineReq,accountOwnerIncomeExamineEntity);
        accountOwnerIncomeExamineEntity.setAmt(accountOwnerIncomeExamineReq.getAmt());
        accountOwnerIncomeExamineEntity.setStatus(accountOwnerIncomeExamineReq.getStatus().getStatus());
        accountOwnerIncomeExamineEntity.setType(accountOwnerIncomeExamineReq.getType().getStatus());
        accountOwnerIncomeExamineEntity.setVersion(NumberUtils.INTEGER_ONE);
        accountOwnerIncomeExamineEntity.setIsDelete(NumberUtils.INTEGER_ZERO);

        int result = accountOwnerIncomeExamineMapper.insertSelective(accountOwnerIncomeExamineEntity);
        if(result==0){
            throw new AccountOwnerIncomeSettleException();
        }
    }

    /**
     * 审核 更新车主收益审核状态
     * @param accountOwnerIncomeExamineOpReq
     */
    public void updateOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq) {
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamine =  accountOwnerIncomeExamineMapper.selectByPrimaryKey(accountOwnerIncomeExamineOpReq.getAccountOwnerIncomeExamineId());
        if(Objects.isNull(accountOwnerIncomeExamine) || Objects.isNull(accountOwnerIncomeExamine.getId())){
            throw new AccountOwnerIncomeExamineException();
        }
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineUpdate = new AccountOwnerIncomeExamineEntity();
        BeanUtils.copyProperties(accountOwnerIncomeExamineOpReq,accountOwnerIncomeExamineUpdate);
        accountOwnerIncomeExamineUpdate.setVersion(accountOwnerIncomeExamine.getVersion());
        accountOwnerIncomeExamineUpdate.setId(accountOwnerIncomeExamine.getId());
        accountOwnerIncomeExamineUpdate.setStatus(accountOwnerIncomeExamineOpReq.getStatus().getStatus());
        accountOwnerIncomeExamineUpdate.setTime(now);
        int result = accountOwnerIncomeExamineMapper.updateByPrimaryKeySelective(accountOwnerIncomeExamineUpdate);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
    }

    public AccountOwnerIncomeExamineEntity getAccountOwnerIncomeExamineById(Integer accountOwnerIncomeExamineId) {
       return accountOwnerIncomeExamineMapper.selectByPrimaryKey(accountOwnerIncomeExamineId);
    }
    
    public List<AccountOwnerIncomeExamineEntity> getAccountOwnerIncomeExamineByOrderNo(String orderNo,String memNo) {
        return accountOwnerIncomeExamineMapper.selectByOrderNo(orderNo,memNo);
     }

	public Integer getTotalAccountOwnerIncomeExamineByOrderNo(String orderNo) {
		return accountOwnerIncomeExamineMapper.getTotalAccountOwnerIncomeExamineByOrderNo(orderNo);
	}

    /**
     * 查询收益类型
     * @param orderNo
     * @param memNo
     * @param type
     * @return
     */
    public List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrderAndType(String orderNo, String memNo, int type) {
        return accountOwnerIncomeExamineMapper.getOwnerIncomeByOrderAndType(orderNo,memNo,type);
    }
}
