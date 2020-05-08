package com.atzuche.order.accountownerincome.utils;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountOwnerIncomeExamineUtil {

    public static AccountOwnerIncomeExamineEntity filterByType(List<AccountOwnerIncomeExamineEntity> list, AccountOwnerIncomeExamineType accountOwnerIncomeExamineType){
        Optional<AccountOwnerIncomeExamineEntity> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> accountOwnerIncomeExamineType.getStatus() == x.getType())
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    public static List<AccountOwnerIncomeExamineEntity> filterByStatus(List<AccountOwnerIncomeExamineEntity> list, AccountOwnerIncomeExamineStatus accountOwnerIncomeExamineStatus){
        List<AccountOwnerIncomeExamineEntity> collect = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> accountOwnerIncomeExamineStatus==null?true:(accountOwnerIncomeExamineStatus.getStatus() == x.getStatus()))
                .collect(Collectors.toList());
        return collect;
    }

    public static int statisticsAmt(List<AccountOwnerIncomeExamineEntity> list){
        int totalAmt = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .collect(Collectors.summingInt(x -> x.getAmt() == null ? 0 : x.getAmt()));
        return totalAmt;
    }

}
