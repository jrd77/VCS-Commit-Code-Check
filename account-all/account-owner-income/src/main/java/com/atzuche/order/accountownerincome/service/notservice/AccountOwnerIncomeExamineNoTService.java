package com.atzuche.order.accountownerincome.service.notservice;

import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeException;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.enums.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeSettleException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeExamineMapper;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.accountownerincome.vo.req.AccountOwnerIncomeExamineReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(accountOwnerIncomeExamineReq,accountOwnerIncomeExamineEntity);
        accountOwnerIncomeExamineEntity.setCreateTime(now);
        accountOwnerIncomeExamineEntity.setUpdateTime(now);
        accountOwnerIncomeExamineEntity.setStatus(AccountOwnerIncomeExamineStatus.WAIT_EXAMINE.getStatus());
        accountOwnerIncomeExamineEntity.setVersion(NumberUtils.INTEGER_ONE);
        accountOwnerIncomeExamineEntity.setIsDelete(NumberUtils.INTEGER_ZERO);

        int result = accountOwnerIncomeExamineMapper.insert(accountOwnerIncomeExamineEntity);
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
            throw new AccountOwnerIncomeException(ErrorCode.FAILED);
        }
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineUpdate = new AccountOwnerIncomeExamineEntity();
        BeanUtils.copyProperties(accountOwnerIncomeExamineOpReq,accountOwnerIncomeExamineUpdate);
        accountOwnerIncomeExamineUpdate.setVersion(accountOwnerIncomeExamine.getVersion());
        accountOwnerIncomeExamineUpdate.setId(accountOwnerIncomeExamine.getId());
        accountOwnerIncomeExamineUpdate.setStatus(accountOwnerIncomeExamineOpReq.getStatus().getStatus());
        accountOwnerIncomeExamineUpdate.setUpdateTime(now);
        accountOwnerIncomeExamineUpdate.setTime(now);
        int result = accountOwnerIncomeExamineMapper.updateByPrimaryKeySelective(accountOwnerIncomeExamineUpdate);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
    }

    public AccountOwnerIncomeExamineEntity getAccountOwnerIncomeExamineById(Integer accountOwnerIncomeExamineId) {
       return accountOwnerIncomeExamineMapper.selectByPrimaryKey(accountOwnerIncomeExamineId);
    }
}
