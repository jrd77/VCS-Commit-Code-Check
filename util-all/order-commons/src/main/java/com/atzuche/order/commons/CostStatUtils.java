package com.atzuche.order.commons;
/*
 * @Author ZhangBin
 * @Date 2020/1/15 15:54
 * @Description: 费用统计类
 *
 **/

import com.atzuche.order.commons.entity.orderDetailDto.ConsoleOwnerOrderFineDeatailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderFineDeatailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderPurchaseDetailDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterOwnerPriceDTO;
import com.atzuche.order.commons.enums.OwnerCashCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CostStatUtils {

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 15:55
     * @Description: 获取车主租金
     *
     **/
    public static int getOwnerRentAmt(List<OwnerOrderPurchaseDetailDTO> list){
        Integer totalAmt = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> OwnerCashCodeEnum.RENT_AMT.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(OwnerOrderPurchaseDetailDTO::getTotalAmount));
        return totalAmt==null?0:totalAmt;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/15 16:09
     * @Description: 车主罚金统计
     *
     **/
    public static int getOwnerFienAmt(List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailDTOS,List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOList){
        Integer ownerOrderFineAmt = Optional.ofNullable(ownerOrderFineDeatailDTOS)
                .orElseGet(ArrayList::new)
                .stream()
                .collect(Collectors.summingInt(OwnerOrderFineDeatailDTO::getFineAmount));

        Integer consoleOwnerOrderFineAmt = Optional.ofNullable(consoleOwnerOrderFineDeatailDTOList)
                .orElseGet(ArrayList::new)
                .stream()
                .collect(Collectors.summingInt(ConsoleOwnerOrderFineDeatailDTO::getFineAmount));

        if(ownerOrderFineAmt == null){
            ownerOrderFineAmt = 0;
        }
        if(consoleOwnerOrderFineAmt == null){
            consoleOwnerOrderFineAmt = 0;
        }
        return ownerOrderFineAmt + consoleOwnerOrderFineAmt;
    }


    public static RenterOwnerPriceDTO ownerRenterPrice(){
        return null;
    }

}
