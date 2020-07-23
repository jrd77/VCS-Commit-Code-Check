package com.atzuche.order.accountplatorm.util;

import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountPlatformProfitUtil {

    /*
     * @Author ZhangBin
     * @Date 2020/7/14 17:01
     * @Description: 通过费用编码过滤
     *
     **/
    public static AccountPlatformProfitDetailEntity filterBySourceCode(OwnerCashCodeEnum ownerCashCodeEnum, List<AccountPlatformProfitDetailEntity> list){
        if(ownerCashCodeEnum == null){
            return new AccountPlatformProfitDetailEntity();
        }
        Optional<AccountPlatformProfitDetailEntity> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> ownerCashCodeEnum.getCashNo().equals(x.getSourceCode()))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return new AccountPlatformProfitDetailEntity();
    }

}
