package com.atzuche.order.rentermem.utils;

import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RenterMemUtils {
    /*
     * @Author ZhangBin
     * @Date 2020/4/17 10:56 
     * @Description:
     *
     **/
    public static RenterMemberRightDTO filterRight(List<RenterMemberRightDTO> list, RightTypeEnum rightTypeEnum, List<MemberFlagEnum> memberFlagEnumList, String rightValue){
        if(rightValue == null||rightValue.trim().length()<=0)return null;
        if(memberFlagEnumList==null|| memberFlagEnumList.size()<=0)return null;
        List<String> rightCodeList = memberFlagEnumList.stream().map(x -> x.getRightCode()).collect(Collectors.toList());
        Optional<RenterMemberRightDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> rightTypeEnum.getCode() == x.getRightType()
                        && rightCodeList.contains(x.getRightCode())
                        && rightValue.equals(x.getRightValue()))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }
}
