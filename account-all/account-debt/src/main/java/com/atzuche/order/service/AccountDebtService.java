package com.atzuche.order.service;

import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.service.notservice.AccountDebtNoTService;
import com.atzuche.order.vo.res.AccountDebtResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtService{
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;


    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     * @throws AccountDebtException
     */
    public AccountDebtResVO getAccountDebtByMemNo(Integer memNo) throws AccountDebtException {
        return accountDebtNoTService.getAccountDebtByMemNo(memNo);
    }
}
