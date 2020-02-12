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


}
