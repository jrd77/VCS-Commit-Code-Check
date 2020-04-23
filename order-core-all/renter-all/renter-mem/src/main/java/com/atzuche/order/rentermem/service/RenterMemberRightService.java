package com.atzuche.order.rentermem.service;

import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtReqDTO;
import com.atzuche.order.rentermem.entity.dto.MemRightCarDepositAmtRespDTO;
import com.atzuche.order.rentermem.exception.CalCarDepositAmtException;
import com.atzuche.order.rentermem.exception.CalWzDepositAmtException;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;
import com.atzuche.order.rentermem.utils.RenterMemUtils;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private RenterMemberRightMapper RenterMemberRightMapper;


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

        List<RenterMemberRightDTO> renterMemberRightDTOList = memRightCarDepositAmtReqDTO.getRenterMemberRightDTOList();
        if(renterMemberRightDTOList == null || renterMemberRightDTOList.size()<=0){
            memRightCarDepositAmtRespDTO.setReductionRate(0D);
            memRightCarDepositAmtRespDTO.setReductionDepositAmt(0);
            return memRightCarDepositAmtRespDTO;
        }
        //企业用户
        RenterMemberRightDTO renterMemberRightDTO = RenterMemUtils.filterRight(renterMemberRightDTOList, RightTypeEnum.MEMBER_FLAG, MemberFlagEnum.QYYH, "1");
        if(renterMemberRightDTO != null){
            if(memRightCarDepositAmtReqDTO.getOrderCategory()!= null && memRightCarDepositAmtReqDTO.getOrderCategory().equals(CategoryEnum.ORDINARY.getCode())){
                memRightCarDepositAmtRespDTO.setReductionRate(GlobalConstant.ENTERPRISE_REDUCTION_RATE);
                memRightCarDepositAmtRespDTO.setReductionDepositAmt((int)(originalDepositAmt*GlobalConstant.ENTERPRISE_REDUCTION_RATE));
                return memRightCarDepositAmtRespDTO;
            }
        }

        List<RenterMemberRightDTO> staff = renterMemberRightDTOList
                .stream()
                .filter(x -> MemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());
        //内部员工
        if(staff!=null && staff.size()==1 && MemberRightValueEnum.OWN.getCode().equals(staff.get(0).getRightValue())){
            memRightCarDepositAmtRespDTO.setReductionDepositAmt(originalDepositAmt - GlobalConstant.MEMBER_RIGHT_STAFF_CAR_DEPOSIT);
            memRightCarDepositAmtRespDTO.setReductionRate(0D);
           return memRightCarDepositAmtRespDTO;
        }
        //其他用户
        List<RenterMemberRightDTO> taskList = renterMemberRightDTOList
                .stream()
                .filter(x -> (
                        MemRightEnum.BIND_WECHAT.getRightCode().equals(x.getRightCode())
                                || MemRightEnum.INVITE_FRIENDS.getRightCode().equals(x.getRightCode())
                                || MemRightEnum.SUCCESS_RENTCAR.getRightCode().equals(x.getRightCode())
                                || MemRightEnum.MEMBER_LEVEL.getRightCode().equals(x.getRightCode())))
                .collect(Collectors.toList());
        double reductionRate = 0;
        if(taskList != null && taskList.size()>0){
            AtomicInteger rightValueTotal = new AtomicInteger(0);
            taskList.forEach(x->{
                rightValueTotal.addAndGet(Integer.valueOf(x.getRightValue() == null ? "0" : x.getRightValue()));
            });
            Integer guidPrice = memRightCarDepositAmtReqDTO.getGuidPrice();
            if(guidPrice != null && guidPrice > GlobalConstant.GUIDE_PRICE){
                reductionRate = 0;
            }else if(rightValueTotal.get() >= GlobalConstant.REDUCTION_RATE_MAX){
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
    public int wzDepositAmt(List<RenterMemberRightDTO> renterMemberRightDTOList, Integer wzDepositAmt,String orderCategory){
        if(wzDepositAmt == null){
            CalWzDepositAmtException calWzDepositAmtException = new CalWzDepositAmtException();
            Cat.logError(calWzDepositAmtException);
            throw calWzDepositAmtException;
        }
        //企业用户
        RenterMemberRightDTO renterMemberRightDTO = RenterMemUtils.filterRight(renterMemberRightDTOList, RightTypeEnum.MEMBER_FLAG, MemberFlagEnum.QYYH, "1");
        if(renterMemberRightDTO != null){
            if(orderCategory!= null && orderCategory.equals(CategoryEnum.ORDINARY.getCode())){
                return GlobalConstant.MEMBER_RIGHT_QYYH_WZ_DEPOSIT;
            }
        }
        //内部员工
        List<RenterMemberRightDTO> staff = renterMemberRightDTOList
                .stream()
                .filter(x -> MemRightEnum.STAFF.getRightCode().equals(x.getRightCode()))
                .limit(1)
                .collect(Collectors.toList());
        if(staff!=null && staff.size()==1 && MemberRightValueEnum.OWN.getCode().equals(staff.get(0).getRightValue())){
            return GlobalConstant.MEMBER_RIGHT_STAFF_WZ_DEPOSIT;
        }
        return wzDepositAmt;
    }

    /**
     * 返回订单相关的会员权益
     * @param orderNo
     * @return
     */
    public List<RenterMemberRightEntity> findByOrderNoAndMemNo(String orderNo){
        List<RenterMemberRightEntity> rightEntities = RenterMemberRightMapper.selectByRenterOrderNo(orderNo);
        return rightEntities;
    }

}
