package com.atzuche.order.rentermem.service;

import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.RenterMemRightEnum;
import com.atzuche.order.rentermem.exception.CalDepositAmtException;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
     * @param renterMemberRightDTOList 权益集合
     * @param carDepositAmt 车辆押金
     * @param wzDepositAmt 违章押金
     * @return 如果CarDepositAmt和wzDepositAmt都不为null，优先选择车辆押金计算
     */
    public int calMemRightDepositAmt(List<RenterMemberRightDTO> renterMemberRightDTOList, Integer carDepositAmt, Integer wzDepositAmt){
        if(carDepositAmt == null && wzDepositAmt==null){
            CalDepositAmtException calDepositAmtException = new CalDepositAmtException();
            Cat.logError(calDepositAmtException);
            throw calDepositAmtException;
        }
        List<RenterMemberRightDTO> staff = renterMemberRightDTOList
                .stream()
                .filter(x -> RenterMemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());
        List<RenterMemberRightDTO> taskList = renterMemberRightDTOList
                .stream()
                .filter(x -> (
                        RenterMemRightEnum.BIND_WECHAT.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.INVITE_FRIENDS.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.SUCCESS_RENTCAR.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.MEMBER_LEVEL.getRightCode().equals(x.getRightCode())))
                .collect(Collectors.toList());

        if(staff!=null && staff.size()==1){
            if(carDepositAmt != null){
                return GlobalConstant.MEMBER_RIGHT_STAFF_CAR_DEPOSIT;
            }else if(wzDepositAmt != null){
                return GlobalConstant.MEMBER_RIGHT_STAFF_WZ_DEPOSIT;
            }
        }/*else if(taskStream != null && taskStream.count()>0){
            taskStream.collect(Coll)
            if(carDepositAmt != null){
                return
            }
        }*/else{
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
