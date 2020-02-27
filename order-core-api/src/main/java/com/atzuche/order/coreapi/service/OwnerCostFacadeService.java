package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OwnerOrderDetailNotFoundException;
import com.atzuche.order.commons.exceptions.OwnerOrderNotFoundException;
import com.atzuche.order.commons.vo.OwnerFineVO;
import com.atzuche.order.commons.vo.OwnerSubsidyDetailVO;
import com.atzuche.order.commons.vo.res.OwnerCostDetailVO;
import com.atzuche.order.commons.vo.res.OwnerIncrementDetailVO;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.ownercost.service.*;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OwnerCostFacadeService {
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired
    private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    private OwnerOrderCostService ownerOrderCostService;
    @Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    @Autowired
    private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;

    public OwnerCostDetailVO getOwnerCostFullDetail(String orderNo, String ownerOrderNo, String ownerMemNo) {

        //车主订单
        String newOwnerOrderNo = ownerOrderNo;
        String newOwnerMemNo = ownerMemNo;
        OwnerOrderDTO ownerOrderDTO = new OwnerOrderDTO();
        if(ownerOrderNo != null && ownerOrderNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空ownerOrderNo={}",ownerOrderNo);
                throw new OwnerOrderNotFoundException(ownerOrderNo);
            }
            BeanUtils.copyProperties(ownerOrderEntity,ownerOrderDTO);
            newOwnerMemNo = ownerOrderEntity.getMemNo();
        }else if(ownerMemNo != null && ownerMemNo.trim().length()>0){
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerByMemNoAndOrderNo(orderNo,ownerMemNo);
            if(ownerOrderEntity == null){
                log.error("获取车主订单信息为空orderNo={},ownerMemNo={}",orderNo,ownerMemNo);
                throw new OwnerOrderDetailNotFoundException(orderNo,ownerMemNo);
            }
            newOwnerOrderNo = ownerOrderEntity.getOwnerOrderNo();
        }
        OwnerCostDetailVO ownerCostDetailVO = new OwnerCostDetailVO();
        ownerCostDetailVO.setOrderNo(orderNo);
        ownerCostDetailVO.setOwnerOrderNo(newOwnerOrderNo);
        ownerCostDetailVO.setOwnerMemNo(newOwnerMemNo);

        //车主租金(代收代付的租金)
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(orderNo, ownerOrderNo);
        ownerCostDetailVO.setRentAmt(getPurchaseRentAmt(ownerOrderPurchaseDetailList));

        //全局的车主订单罚金明细
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo);
        //车主罚金
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOwnerOrderNo(newOwnerOrderNo);
        OwnerFineVO ownerFile = getOwnerFile(consoleOwnerOrderFineDeatailList, ownerOrderFineDeatailList);
        ownerCostDetailVO.setFineDetail(ownerFile);

        //车主补贴
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, newOwnerOrderNo);
        //管理后台补贴
        List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetailEntities = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo, newOwnerMemNo);
        OwnerSubsidyDetailVO ownerSubsidy = getOwnerSubsidy(ownerOrderSubsidyDetailEntities, orderConsoleSubsidyDetailEntities);
        ownerCostDetailVO.setSubsidyDetail(ownerSubsidy);

        //车主配送服务费（车主增值订单）
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailEntities = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(orderNo, newOwnerOrderNo);
        OwnerIncrementDetailVO increment = getIncrement(ownerOrderIncrementDetailEntities);
        ownerCostDetailVO.setIncrementDetail(increment);

        return ownerCostDetailVO;
    }

    private Integer getPurchaseRentAmt(List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList){
            Integer totalAmt = Optional.ofNullable(ownerOrderPurchaseDetailList)
                    .orElseGet(ArrayList::new)
                    .stream()
                    .filter(x -> OwnerCashCodeEnum.RENT_AMT.getCashNo().equals(x.getCostCode()))
                    .collect(Collectors.summingInt(OwnerOrderPurchaseDetailEntity::getTotalAmount));
            return totalAmt==null?0:totalAmt;
    }

    private OwnerFineVO getOwnerFile(List<ConsoleOwnerOrderFineDeatailEntity> consoleFineList, List<OwnerOrderFineDeatailEntity> orderFineList){
        OwnerFineVO ownerFineVO = new OwnerFineVO();
        ownerFineVO.setModifyGetFine(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.MODIFY_GET_FINE));
        ownerFineVO.setModifyReturnFine(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.MODIFY_RETURN_FINE));
        ownerFineVO.setModifyAdvance(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.MODIFY_ADVANCE));
        ownerFineVO.setCancelFine(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.CANCEL_FINE));
        ownerFineVO.setDelayFine(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.DELAY_FINE));
        ownerFineVO.setModifyAddressFine(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.MODIFY_ADDRESS_FINE));
        ownerFineVO.setRenterAdvanceReturn(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.RENTER_ADVANCE_RETURN));
        ownerFineVO.setRenterDelayReturn(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.RENTER_DELAY_RETURN));
        ownerFineVO.setOwnerFine(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.OWNER_FINE));
        ownerFineVO.setGetReturnCar(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.GET_RETURN_CAR));
        return ownerFineVO;
    }


    private OwnerSubsidyDetailVO getOwnerSubsidy(List<OwnerOrderSubsidyDetailEntity> subsidyList,List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList){
        OwnerSubsidyDetailVO ownerSubsidyDetailVO = new OwnerSubsidyDetailVO();
        ownerSubsidyDetailVO.setRealCouponOffset(getOwnerSubsidy(subsidyList,consoleSubsidyList, RenterCashCodeEnum.REAL_COUPON_OFFSET.getCashNo()));
        ownerSubsidyDetailVO.setOwnerCouponOffsetCost(getOwnerSubsidy(subsidyList,consoleSubsidyList, RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo()));
        ownerSubsidyDetailVO.setSubsidyOwnerTorenterRentamt(getOwnerSubsidy(subsidyList,consoleSubsidyList, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT.getCashNo()));
        ownerSubsidyDetailVO.setOwnerGoodsSubsidy(getOwnerSubsidy(subsidyList,consoleSubsidyList,OwnerCashCodeEnum.OWNER_GOODS_SUBSIDY.getCashNo()));
        ownerSubsidyDetailVO.setOwnerDelaySubsidy(getOwnerSubsidy(subsidyList,consoleSubsidyList,OwnerCashCodeEnum.OWNER_DELAY_SUBSIDY.getCashNo()));
        ownerSubsidyDetailVO.setOwnerTrafficSubsidy(getOwnerSubsidy(subsidyList,consoleSubsidyList,OwnerCashCodeEnum.OWNER_TRAFFIC_SUBSIDY.getCashNo()));
        ownerSubsidyDetailVO.setOwnerIncomeSubsidy(getOwnerSubsidy(subsidyList,consoleSubsidyList,OwnerCashCodeEnum.OWNER_INCOME_SUBSIDY.getCashNo()));
        ownerSubsidyDetailVO.setOwnerWashCarSubsidy(getOwnerSubsidy(subsidyList,consoleSubsidyList,OwnerCashCodeEnum.OWNER_WASH_CAR_SUBSIDY.getCashNo()));
        ownerSubsidyDetailVO.setOwnerOtherSubsidy(getOwnerSubsidy(subsidyList,consoleSubsidyList,OwnerCashCodeEnum.OWNER_OTHER_SUBSIDY.getCashNo()));
        return ownerSubsidyDetailVO;
    }

    private OwnerIncrementDetailVO getIncrement(List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailEntities){
        OwnerIncrementDetailVO ownerIncrementDetailVO = new OwnerIncrementDetailVO();
        ownerIncrementDetailVO.setGpsServiceAmt(getIncrement(ownerOrderIncrementDetailEntities,OwnerCashCodeEnum.GPS_SERVICE_AMT));
        ownerIncrementDetailVO.setServiceCharge(getIncrement(ownerOrderIncrementDetailEntities,OwnerCashCodeEnum.SERVICE_CHARGE));
        ownerIncrementDetailVO.setSrvGetCostOwner(getIncrement(ownerOrderIncrementDetailEntities,OwnerCashCodeEnum.SRV_GET_COST_OWNER));
        ownerIncrementDetailVO.setSrvReturnCostOwner(getIncrement(ownerOrderIncrementDetailEntities,OwnerCashCodeEnum.SRV_RETURN_COST_OWNER));
        return ownerIncrementDetailVO;
    }
    private Integer getIncrement(List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailEntities,OwnerCashCodeEnum ownerCashCodeEnum){
        Integer incrementAmt = ownerOrderIncrementDetailEntities.stream()
                .filter(x -> ownerCashCodeEnum.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(OwnerOrderIncrementDetailEntity::getTotalAmount));
        return incrementAmt==null?0:incrementAmt;
    }

    private Integer getOwnerSubsidy(List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities,List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList,String ownerCashCode){
        Integer subsidyAmt = ownerOrderSubsidyDetailEntities
                .stream()
                .filter(x -> ownerCashCode.equals(x.getSubsidyCostCode()))
                .collect(Collectors.summingInt(OwnerOrderSubsidyDetailEntity::getSubsidyAmount));
        Integer consoleSubsidyAmt = consoleSubsidyList
                .stream()
                .filter(x-> ownerCashCode.equals(x.getSubsidyCostCode()))
                .collect(Collectors.summingInt(OrderConsoleSubsidyDetailEntity::getSubsidyAmount));
        return (subsidyAmt==null?0:subsidyAmt) + (consoleSubsidyAmt==null?0:consoleSubsidyAmt);
    }
    private Integer getOwnerFileByType(List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList, List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList,FineTypeEnum fineType){
        Integer consoleFineAmt = consoleOwnerOrderFineDeatailList
                .stream()
                .filter(x -> fineType.getFineType().equals(x.getFineType()))
                .collect(Collectors.summingInt(ConsoleOwnerOrderFineDeatailEntity::getFineAmount));
        Integer orderFineAmt = ownerOrderFineDeatailList
                .stream()
                .filter(x-> fineType.getFineType().equals(x.getFineType()))
                .collect(Collectors.summingInt(OwnerOrderFineDeatailEntity::getFineAmount));
        return (consoleFineAmt==null?0:consoleFineAmt) + (orderFineAmt==null?0:orderFineAmt);
    }

}
