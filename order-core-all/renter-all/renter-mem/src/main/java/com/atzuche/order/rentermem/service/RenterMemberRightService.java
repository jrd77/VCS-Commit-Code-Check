package com.atzuche.order.rentermem.service;

import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.RenterMemRightEnum;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtReqDTO;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtRespDTO;
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

    private static final int guidePrice = 1500000;

    /**
     *
     * @param renterMemberRightDTOList 权益集合
     * @param carDepositAmt 车辆押金
     * @return  会员权益车辆押金计算
     */
    public MemRightCarDepositAmtRespDTO carDepositAmt(MemRightCarDepositAmtReqDTO memRightCarDepositAmtReqDTO){
        Integer originalDepositAmt = memRightCarDepositAmtReqDTO.getOriginalDepositAmt();
        if(memRightCarDepositAmtReqDTO == null || originalDepositAmt == null){
            CalCarDepositAmtException calDepositAmtException = new CalCarDepositAmtException();
            Cat.logError(calDepositAmtException);
            throw calDepositAmtException;
        }
        MemRightCarDepositAmtRespDTO memRightCarDepositAmtRespDTO = new MemRightCarDepositAmtRespDTO();
        memRightCarDepositAmtRespDTO.setOriginalDepositAmt(originalDepositAmt);
        List<RenterMemberRightDTO> staff = memRightCarDepositAmtReqDTO.getRenterMemberRightDTOList()
                .stream()
                .filter(x -> RenterMemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());
        //内部员工
        if(staff!=null && staff.size()==1){
            memRightCarDepositAmtRespDTO.setReductionDepositAmt(originalDepositAmt - GlobalConstant.MEMBER_RIGHT_STAFF_CAR_DEPOSIT);
            memRightCarDepositAmtRespDTO.setReductionRate(0D);
           return memRightCarDepositAmtRespDTO;
        }

        List<RenterMemberRightDTO> taskList = memRightCarDepositAmtReqDTO.getRenterMemberRightDTOList()
                .stream()
                .filter(x -> (
                        RenterMemRightEnum.BIND_WECHAT.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.INVITE_FRIENDS.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.SUCCESS_RENTCAR.getRightCode().equals(x.getRightCode())
                                || RenterMemRightEnum.MEMBER_LEVEL.getRightCode().equals(x.getRightCode())))
                .collect(Collectors.toList());
        //外部员工
        double reductionRate = 0;
        if(taskList != null && taskList.size()>0){
            AtomicInteger rightValueTotal = new AtomicInteger(0);
            taskList.forEach(x->{
                rightValueTotal.addAndGet(Integer.valueOf(x.getRightValue() == null ? "0" : x.getRightValue()));
            });
            Integer guidPrice = memRightCarDepositAmtReqDTO.getGuidPrice();
            if(guidPrice != null && guidPrice > guidePrice){
                reductionRate = 0;
            }else if(rightValueTotal.get() >= 70){
                reductionRate = 0.7;
            }else{
                reductionRate = rightValueTotal.get()/100;
            }
        }
        memRightCarDepositAmtRespDTO.setReductionRate(reductionRate);
        memRightCarDepositAmtRespDTO.setReductionDepositAmt((int)(originalDepositAmt*reductionRate));
        return memRightCarDepositAmtRespDTO;
    }

    /**
     *
     * @param renterMemberRightDTOList 权益集合
     * @param wzDepositAmt 违章押金
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
