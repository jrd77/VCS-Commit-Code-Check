package com.atzuche.order.accountdebt.service.notservice;

import com.atzuche.order.accountdebt.dto.AccountDeductDebtDTO;
import com.atzuche.order.accountdebt.entity.AccountDebtDetailEntity;
import com.atzuche.order.accountdebt.exception.AccountDebtException;
import com.atzuche.order.accountdebt.mapper.AccountDebtDetailMapper;
import com.atzuche.order.accountdebt.vo.req.AccountInsertDebtReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
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
              throw new AccountDebtException(ErrorCode.FAILED);
          }
       }
    }


    public void insertDebtDetail(AccountInsertDebtReqVO accountInsertDebt) {
        AccountDebtDetailEntity accountDebtDetail = new AccountDebtDetailEntity();
        BeanUtils.copyProperties(accountInsertDebt,accountDebtDetail);
        LocalDateTime now = LocalDateTime.now();
        accountDebtDetail.setUpdateTime(now);
        accountDebtDetail.setCreateTime(now);
        accountDebtDetail.setCurrentDebtAmt(accountInsertDebt.getAmt());
        accountDebtDetail.setOrderDebtAmt(accountInsertDebt.getAmt());
        accountDebtDetail.setRepaidDebtAmt(NumberUtils.INTEGER_ZERO);
        int result = accountDebtDetailMapper.insert(accountDebtDetail);
        if(result==0){
            throw new AccountDebtException(ErrorCode.FAILED);
        }
    }
}
