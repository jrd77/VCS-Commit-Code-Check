package com.atzuche.order.accountownercost.util;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountOwnerCostSettleUtil {

    /*
     * @Author ZhangBin
     * @Date 2020/7/14 17:01
     * @Description: 通过费用编码过滤
     *
     **/
    public static AccountOwnerCostSettleDetailEntity filterBySourceCode(OwnerCashCodeEnum ownerCashCodeEnum, List<AccountOwnerCostSettleDetailEntity> list){
        if(ownerCashCodeEnum == null){
            return new AccountOwnerCostSettleDetailEntity();
        }
        Optional<AccountOwnerCostSettleDetailEntity> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> ownerCashCodeEnum.getCashNo().equals(x.getSourceCode()))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return new AccountOwnerCostSettleDetailEntity();
    }

}
