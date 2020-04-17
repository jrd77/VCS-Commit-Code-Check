package com.atzuche.order.accountrenterdeposit.utils;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AccountRenterDepositUtils {

    /**
     * 获取结算后车辆押金抵扣的历史欠款
     * @return
     */
    public static int getDepositSettleDeductionDebtAmt(List<AccountRenterDepositDetailEntity> all, RenterCashCodeEnum renterCashCodeEnum){
        int sum = Optional.ofNullable(all)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> renterCashCodeEnum.getCashNo().equals(x.getSourceCode()))
                .mapToInt(x -> x.getAmt()==null?0:x.getAmt())
                .sum();
        return sum;
    }
}
