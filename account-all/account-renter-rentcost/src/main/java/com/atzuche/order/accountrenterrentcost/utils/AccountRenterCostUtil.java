package com.atzuche.order.accountrenterrentcost.utils;

import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountRenterCostUtil {
    /*
     * @Author ZhangBin
     * @Date 2020/4/24 11:46
     * @Description: 过滤租车费用记录
     *
     **/
    public static AccountRenterCostDetailEntity filterRenterCost(List<AccountRenterCostDetailEntity> list,
                                                          PaySourceEnum paySourceEnum,
                                                          PayTypeEnum payTypeEnum,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime){
        Optional<AccountRenterCostDetailEntity> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> paySourceEnum.getCode().equals(x.getPaySourceCode()))
                .filter(x -> payTypeEnum.getCode().equals(x.getPayTypeCode()))
                .filter(x -> startTime == null ? true : (x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x -> endTime == null ? true : (x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime)))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }
}
