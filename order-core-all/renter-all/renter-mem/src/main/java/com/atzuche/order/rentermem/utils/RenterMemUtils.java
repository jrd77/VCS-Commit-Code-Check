package com.atzuche.order.rentermem.utils;

import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RenterMemUtils {
    /*
     * @Author ZhangBin
     * @Date 2020/4/17 10:56 
     * @Description:
     *
     **/
    public static RenterMemberRightDTO filterRight(List<RenterMemberRightDTO> list, RightTypeEnum rightTypeEnum, MemberFlagEnum memberFlagEnum, String rightValue){
        if(rightValue == null||rightValue.trim().length()<=0)return null;
        Optional<RenterMemberRightDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> rightTypeEnum.getCode() == x.getRightType()
                        && memberFlagEnum.getRightCode().equals(x.getRightCode())
                        && rightValue.equals(x.getRightValue()))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }
}
