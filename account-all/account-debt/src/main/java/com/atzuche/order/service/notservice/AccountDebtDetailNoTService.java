package com.atzuche.order.service.notservice;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.entity.AccountDebtDetailEntity;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.mapper.AccountDebtDetailMapper;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;


/**
 * 个人历史欠款明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtDetailNoTService {
    @Autowired
    private AccountDebtDetailMapper accountDebtDetailMapper;


    /**
     * 查询待抵扣历史欠款
     * @return
     */
    public List<AccountDebtDetailEntity> getDebtListByMemNo(int memNo){
        List<AccountDebtDetailEntity> result = accountDebtDetailMapper.getDebtListByMemNo(memNo);
        if(CollectionUtils.isEmpty(result)){
            return Collections.emptyList();
        }
        return result;
    }

    public void updateAlreadyDeductDebt(AccountDeductDebtDTO accountDeductDebtDTO) {
       for(int i=0;i<accountDeductDebtDTO.getAccountDebtDetailTodos().size();i++){
          int result = accountDebtDetailMapper.updateByPrimaryKeySelective(accountDeductDebtDTO.getAccountDebtDetailTodos().get(i));
          if(result==0){
              throw new AccountDebtException(ErrorCode.PARAMETER_ERROR);
          }
       }
    }
}
