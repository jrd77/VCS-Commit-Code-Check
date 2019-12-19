package com.atzuche.order.rentermem.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.entity.dto.MemberRightStaffDto;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDto;
import com.atzuche.order.commons.enums.RenterMemRightEnum;
import com.atzuche.order.rentermem.exception.CalDepositAmtException;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * 租客端会员权益表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Service
public class RenterMemberRightService{
    @Autowired
    private RenterMemberRightMapper renterMemberRightMapper;

    /**
     *
     * @param renterMemberRightDtoList 权益集合
     * @param carDepositAmt 车辆押金
     * @param wzDepositAmt 违章押金
     * @return 如果CarDepositAmt和wzDepositAmt都不为null，优先选择车辆押金计算
     */
    public int calDepositAmt(List<RenterMemberRightDto> renterMemberRightDtoList,Integer carDepositAmt,Integer wzDepositAmt){
        if(carDepositAmt == null && wzDepositAmt==null){
            CalDepositAmtException calDepositAmtException = new CalDepositAmtException();
            Cat.logError(calDepositAmtException);
            throw calDepositAmtException;
        }
        List<RenterMemberRightDto> collect = renterMemberRightDtoList
                .stream()
                .filter(x -> RenterMemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());
        if(collect!=null && collect.size()==1){
            if(carDepositAmt != null){
                return GlobalConstant.MEMBER_RIGHT_STAFF_CAR_DEPOSIT;
            }else if(wzDepositAmt != null){
                return GlobalConstant.MEMBER_RIGHT_STAFF_WZ_DEPOSIT;
            }
        }else{
            if(carDepositAmt != null){
                return carDepositAmt;
            }else if(wzDepositAmt != null){
                return wzDepositAmt;
            }
        }
        CalDepositAmtException calDepositAmtException = new CalDepositAmtException();
        Cat.logError(calDepositAmtException);
        throw calDepositAmtException;
    }

}
