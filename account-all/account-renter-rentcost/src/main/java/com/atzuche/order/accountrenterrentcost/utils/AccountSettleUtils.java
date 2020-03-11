package com.atzuche.order.accountrenterrentcost.utils;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AccountSettleUtils {

    /**
     * 获取结算后车辆押金抵扣的历史欠款
     * @return
     */
    public static int getDepositSettleDeductionDebtAmt(List<AccountRenterCostSettleDetailEntity> all, RenterCashCodeEnum renterCashCodeEnum){
        int sum = Optional.ofNullable(all)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> renterCashCodeEnum.getCashNo().equals(x.getCostCode()))
                .mapToInt(x -> x.getAmt())
                .sum();
        return sum;
    }
}
