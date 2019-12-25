package com.atzuche.order.rentermem.service;

import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.RenterMemRightEnum;
import com.atzuche.order.rentermem.exception.CalCarDepositAmtException;
import com.atzuche.order.rentermem.exception.CalWzDepositAmtException;
import com.dianping.cat.Cat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 租客端会员权益表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Service
public class RenterMemberRightService{

    /**
     *
     * @param renterMemberRightDTOList 权益集合
     * @param carDepositAmt 车辆押金
     * @return CarDepositAmt
     */
    public int carDepositAmt(List<RenterMemberRightDTO> renterMemberRightDTOList, Integer carDepositAmt){
        if(carDepositAmt == null){
            CalCarDepositAmtException calDepositAmtException = new CalCarDepositAmtException();
            Cat.logError(calDepositAmtException);
            throw calDepositAmtException;
        }
        List<RenterMemberRightDTO> staff = renterMemberRightDTOList
                .stream()
                .filter(x -> RenterMemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());

        if(staff!=null && staff.size()==1){
           return GlobalConstant.MEMBER_RIGHT_STAFF_CAR_DEPOSIT;
        }

        List<RenterMemberRightDTO> taskList = renterMemberRightDTOList
                .stream()
                .filter(x -> (
                        RenterMemRightEnum.BIND_WECHAT.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.INVITE_FRIENDS.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.SUCCESS_RENTCAR.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.MEMBER_LEVEL.getRightCode().equals(x.getRightCode())))
                .collect(Collectors.toList());

        if(taskList != null && taskList.size()>0){
            AtomicInteger rightValueTotal = new AtomicInteger(0);
            taskList.forEach(x->{
                rightValueTotal.addAndGet(Integer.valueOf(x.getRightValue() == null ? "0" : x.getRightValue()));
            });
            if(rightValueTotal.get() >= 70){
                return (int) (carDepositAmt*0.7);
            }else{
                return (int)(rightValueTotal.get()/100*carDepositAmt);
            }
        }
        return carDepositAmt;
    }

    /**
     *
     * @param renterMemberRightDTOList 权益集合
     * @param wzDepositAmt 车辆押金
     * @return 会员权益违章押金计算
     */
    public int wzDepositAmt(List<RenterMemberRightDTO> renterMemberRightDTOList, Integer wzDepositAmt){
        if(wzDepositAmt == null){
            CalWzDepositAmtException calWzDepositAmtException = new CalWzDepositAmtException();
            Cat.logError(calWzDepositAmtException);
            throw calWzDepositAmtException;
        }
        List<RenterMemberRightDTO> staff = renterMemberRightDTOList
                .stream()
                .filter(x -> RenterMemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());
        if(staff!=null && staff.size()==1){
            return GlobalConstant.MEMBER_RIGHT_STAFF_WZ_DEPOSIT;
        }
        return wzDepositAmt;
    }

}
