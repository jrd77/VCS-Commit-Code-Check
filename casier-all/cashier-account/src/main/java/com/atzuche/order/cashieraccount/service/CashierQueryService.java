package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 收银台操作 查询专用
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierQueryService {
    @Autowired
    private AccountRenterWzDepositCostNoTService accountRenterWzDepositCostNoTService;
    @Autowired
    private AccountRenterDepositNoTService accountRenterDepositNoTService;


    /**
     * 查询违章押金
     */
    public AccountRenterWzDepositCostEntity queryWzDeposit(String orderNo){
        AccountRenterWzDepositCostEntity entity = accountRenterWzDepositCostNoTService.queryWzDeposit(orderNo);
        return entity;
    }

    /**
     * 查询违章押金实付
     */
    public int queryWzDepositShifuAmt(String orderNo){
        AccountRenterWzDepositCostEntity entity = queryWzDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getShifuAmt())){
            return 0;
        }
        return entity.getShifuAmt();
    }
    /**
     * 查询违章押金应付
     */
    public int queryWzDepositYingfuAmt(String orderNo){
        AccountRenterWzDepositCostEntity entity = queryWzDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getYingfuAmt())){
            return 0;
        }
        return entity.getYingfuAmt();
    }

    /**
     * 查询车辆押金信息
     */
    public AccountRenterDepositEntity queryDeposit(String orderNo){
        AccountRenterDepositEntity entity = accountRenterDepositNoTService.queryDeposit(orderNo);
        return entity;
    }

    /**
     * 查询车辆押金实付
     */
    public int queryDepositShifuAmt(String orderNo){
        AccountRenterDepositEntity entity = queryDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getShifuDepositAmt())){
            return 0;
        }
        return entity.getShifuDepositAmt();
    }
    /**
     * 查询车辆押金应付
     */
    public int queryDepositYingfuAmt(String orderNo){
        AccountRenterDepositEntity entity = queryDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getYingfuDepositAmt())){
            return 0;
        }
        return entity.getYingfuDepositAmt();
    }
}
