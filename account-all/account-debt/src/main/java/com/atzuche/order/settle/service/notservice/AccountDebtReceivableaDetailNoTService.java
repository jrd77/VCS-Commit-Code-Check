package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.settle.exception.AccountDeductDebtDBException;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.settle.mapper.AccountDebtReceivableaDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 个人历史欠款收款记录
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtReceivableaDetailNoTService {
    @Autowired
    private AccountDebtReceivableaDetailMapper accountDebtReceivableaDetailMapper;


    public void insertAlreadyReceivablea(List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetails) {
        for(int i=0;i<accountDebtReceivableaDetails.size();i++){
            AccountDebtReceivableaDetailEntity accountDebtReceivableaDetail =  accountDebtReceivableaDetails.get(i);
            int result = accountDebtReceivableaDetailMapper.insertSelective(accountDebtReceivableaDetail);
            if(result==0){
                throw new AccountDeductDebtDBException();
            }
        }
    }

    /**
     * 幂等校验
     * @param sourceCode
     * @param uniqueNo
     * @return
     */
    public boolean idempotentByUniqueAndSourceCode(Integer sourceCode, String uniqueNo) {
        AccountDebtReceivableaDetailEntity accountDebtReceivableaDetail = accountDebtReceivableaDetailMapper.selectByUniqueAndSourceCode(sourceCode,uniqueNo);
        return Objects.nonNull(accountDebtReceivableaDetail) && Objects.nonNull(accountDebtReceivableaDetail.getId());
    }

    /**
     * 根据 历史待还详情  返回 欠款收款信息
     *
     * @param accountDebtDetails 本次待还 欠款信息
     * @return
     */
    public List<AccountDebtReceivableaDetailEntity> getDebtReceivableaDetailsByDebtDetails(List<AccountDebtDetailEntity> accountDebtDetails, AccountDeductDebtReqVO accountDeductDebt) {
        List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetails = new ArrayList<>();
        for(int i =0;i<accountDebtDetails.size();i++){
            AccountDebtDetailEntity accountDebtDetailEntity = accountDebtDetails.get(i);
            AccountDebtReceivableaDetailEntity accountDebtReceivableaDetail = new AccountDebtReceivableaDetailEntity();
            BeanUtils.copyProperties(accountDeductDebt,accountDebtReceivableaDetail);
            accountDebtReceivableaDetail.setTime(LocalDateTime.now());
            accountDebtReceivableaDetail.setDebtDetailId(accountDebtDetailEntity.getId());
            accountDebtReceivableaDetail.setOrderNo(accountDebtDetailEntity.getOrderNo());
            accountDebtReceivableaDetails.add(accountDebtReceivableaDetail);
        }
        return accountDebtReceivableaDetails;
    }
}
