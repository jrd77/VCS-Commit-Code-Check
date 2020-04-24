package com.atzuche.order.cashieraccount.utils;

import com.atzuche.order.cashieraccount.common.PayCashTypeEnum;
import com.atzuche.order.cashieraccount.entity.AccountVirtualPayDetailEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.entity.OfflineRefundApplyEntity;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CashierUtils {
    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:16
     * @Description: 过滤虚拟支付记录
     *
     **/
    public static AccountVirtualPayDetailEntity filterByPayCashTypeAndPayType(List<AccountVirtualPayDetailEntity> list,
                                                                              PayCashTypeEnum payCashTypeEnum,
                                                                              PayTypeEnum payTypeEnum,
                                                                              LocalDateTime startTime,
                                                                              LocalDateTime endTime){
        Optional<AccountVirtualPayDetailEntity> first = Optional.ofNullable(list)
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
    public static OfflineRefundApplyEntity filterBySourceCode(List<OfflineRefundApplyEntity> list,
                                                              RenterCashCodeEnum renterCashCodeEnum,
                                                              LocalDateTime startTime,
                                                              LocalDateTime endTime){
        Optional<OfflineRefundApplyEntity> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> renterCashCodeEnum.getCashNo().equals(x.getSourceCode()))
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
    public static CashierRefundApplyEntity filterCashierRefound(List<CashierRefundApplyEntity> list,
                                                                String dataPayKindConstant,
                                                                PayTypeEnum payTypeEnum,
                                                              LocalDateTime startTime,
                                                              LocalDateTime endTime){
        Optional<CashierRefundApplyEntity> first = Optional.ofNullable(list)
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
