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

    public List<AccountOwnerIncomeExamineEntity> filterByType(List<AccountOwnerIncomeExamineEntity> list, AccountOwnerIncomeExamineType accountOwnerIncomeExamineType){
        List<AccountOwnerIncomeExamineEntity> collect = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> accountOwnerIncomeExamineType.getStatus() == x.getType())
                .collect(Collectors.toList());
        return collect;
    }

    public List<AccountOwnerIncomeExamineEntity> filterByStatus(List<AccountOwnerIncomeExamineEntity> list, AccountOwnerIncomeExamineStatus accountOwnerIncomeExamineStatus){
        List<AccountOwnerIncomeExamineEntity> collect = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> accountOwnerIncomeExamineStatus.getStatus() == x.getType())
                .collect(Collectors.toList());
        return collect;
    }

}
