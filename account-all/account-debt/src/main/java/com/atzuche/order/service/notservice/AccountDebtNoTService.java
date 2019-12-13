package com.atzuche.order.service.notservice;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.entity.AccountDebtEntity;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.mapper.AccountDebtMapper;
import com.atzuche.order.vo.res.AccountDebtResVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtNoTService {
    @Autowired
    private AccountDebtMapper accountDebtMapper;


    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     * @throws AccountDebtException
     */
    public AccountDebtResVO getAccountDebtByMemNo(Integer memNo) throws AccountDebtException {
        if(Objects.isNull(memNo)){
            throw new AccountDebtException(ErrorCode.PARAMETER_ERROR);
        }
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(memNo);
        AccountDebtResVO res = new AccountDebtResVO();
        if(Objects.nonNull(accountDebtEntity)){
            BeanUtils.copyProperties(accountDebtEntity,res);
        }
        return res;
    }

    public void updateAccountDebt(AccountDeductDebtDTO accountDeductDebtDTO) {

    }
}
