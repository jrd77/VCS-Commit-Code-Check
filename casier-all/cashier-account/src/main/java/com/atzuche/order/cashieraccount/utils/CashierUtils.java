package com.atzuche.order.cashieraccount.utils;

import com.atzuche.order.cashieraccount.common.PayCashTypeEnum;
import com.atzuche.order.commons.entity.orderDetailDto.AccountVirtualPayDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.CashierRefundApplyDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OfflineRefundApplyDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CashierUtils {
    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:16
     * @Description: 过滤虚拟支付记录
     *
     **/
    public static AccountVirtualPayDetailDTO filterByPayCashTypeAndPayType(List<AccountVirtualPayDetailDTO> list,
                                                                           PayCashTypeEnum payCashTypeEnum,
                                                                           PayTypeEnum payTypeEnum,
                                                                           LocalDateTime startTime,
                                                                           LocalDateTime endTime){
        Optional<AccountVirtualPayDetailDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x ->payCashTypeEnum.getValue().equals(x.getPayCashType()))
                .filter(x->payTypeEnum.getCode().equals(x.getPayType()))
                .filter(x->startTime==null?true:(x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x->endTime==null?true:x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }
    
    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:23
     * @Description: 过滤线下支付
     * 
     **/
    public static OfflineRefundApplyDTO filterBySourceCode(List<OfflineRefundApplyDTO> list,
                                                           List<RenterCashCodeEnum> renterCashCodeEnum,
                                                           LocalDateTime startTime,
                                                           LocalDateTime endTime){
        List<String> renterCashCodeEnumList = Optional.ofNullable(renterCashCodeEnum)
                .orElseGet(ArrayList::new).stream()
                .map(x -> x.getCashNo()).collect(Collectors.toList());
        Optional<OfflineRefundApplyDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> renterCashCodeEnumList.contains(x.getSourceCode()))
                .filter(x -> startTime == null ? true : (x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x -> endTime == null ? true : (x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime)))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:23
     * @Description: 退款申请表
     *
     **/
    public static CashierRefundApplyDTO filterCashierRefound(List<CashierRefundApplyDTO> list,
                                                             String dataPayKindConstant,
                                                             PayTypeEnum payTypeEnum,
                                                             LocalDateTime startTime,
                                                             LocalDateTime endTime){
        Optional<CashierRefundApplyDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x ->dataPayKindConstant==null?true:dataPayKindConstant.equals(x.getPayKind()))
                .filter(x->payTypeEnum.getCode().equals(x.getPayType()))
                .filter(x->startTime==null?true:(x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x->endTime==null?true:x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }


}
