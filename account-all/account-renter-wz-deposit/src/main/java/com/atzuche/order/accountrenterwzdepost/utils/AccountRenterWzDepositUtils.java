package com.atzuche.order.accountrenterwzdepost.utils;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AccountRenterWzDepositUtils {

    /**
     * 获取结算后车辆押金抵扣的历史欠款
     * @return
     */
    public static int getWzDepositSettleDeductionDebtAmt(List<AccountRenterWzDepositDetailEntity> all, RenterCashCodeEnum renterCashCodeEnum){
        int sum = Optional.ofNullable(all)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> renterCashCodeEnum.getCashNo().equals(x.getSourceCode()))
                .mapToInt(x -> x.getAmt())
                .sum();
        return sum;
    }
}
