package com.atzuche.order.service.notservice;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.mapper.AccountDebtReceivableaDetailMapper;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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


    public void insertAlreadyReceivablea(AccountDeductDebtDTO accountDeductDebtDTO) {
        for(int i=0;i<accountDeductDebtDTO.getAccountDebtReceivableaDetails().size();i++){
            AccountDebtReceivableaDetailEntity accountDebtReceivableaDetail =  accountDeductDebtDTO.getAccountDebtReceivableaDetails().get(i);
            int result = accountDebtReceivableaDetailMapper.insert(accountDebtReceivableaDetail);
            if(result==0){
                throw new AccountDebtException(ErrorCode.PARAMETER_ERROR);
            }
        }
    }
}
