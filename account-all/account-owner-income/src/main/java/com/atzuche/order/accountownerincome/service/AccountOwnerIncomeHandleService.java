package com.atzuche.order.accountownerincome.service;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.commons.constant.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pengcheng.fu
 * @date 2020/7/23 15:35
 */

@Service
@Slf4j
public class AccountOwnerIncomeHandleService {

    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;


    public int oldIncomeCompensateHandle(AccountOwnerIncomeDetailEntity record, int addIncomeAmt) {
        if (addIncomeAmt >= OrderConstant.ZERO) {
            return addIncomeAmt;
        }


        return OrderConstant.ZERO;
    }


    public int newIncomeCompensateHandle(AccountOwnerIncomeDetailEntity record, int addIncomeAmt) {
        if (addIncomeAmt >= OrderConstant.ZERO) {
            return addIncomeAmt;
        }

        return OrderConstant.ZERO;
    }


    public int secondaryIncomeCompensateHandle(AccountOwnerIncomeDetailEntity record, int addIncomeAmt) {
        if (addIncomeAmt >= OrderConstant.ZERO) {
            return addIncomeAmt;
        }

        return OrderConstant.ZERO;
    }

}
