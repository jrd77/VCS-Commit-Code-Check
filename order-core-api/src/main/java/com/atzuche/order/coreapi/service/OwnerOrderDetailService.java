package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.CostStatUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderConsoleCostDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderFineDeatailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderSubsidyDetailDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.coreapi.submitOrder.exception.OrderDetailException;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleCostDetailService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OwnerOrderDetailService {
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired
    private OrderConsoleCostDetailService orderConsoleCostDetailService;
    @Autowired
    private OrderSettleService orderSettleService;

    public ResponseData<OwnerRentDetailDTO> ownerRentDetail(String orderNo, String ownerOrderNo) {
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderDetailException();
        }
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, true);
        OwnerRentDetailDTO ownerRentDetailDTO = new OwnerRentDetailDTO();
        if(ownerGoodsDetail != null && ownerGoodsDetail.getOwnerGoodsPriceDetailDTOList()!=null && ownerGoodsDetail.getOwnerGoodsPriceDetailDTOList().size()>0){
            List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetail.getOwnerGoodsPriceDetailDTOList();
            ownerGoodsPriceDetailDTOList
                   .forEach(x->{
                       LocalDate carDay = x.getCarDay();
                       x.setCarDayStr(carDay!=null?LocalDateTimeUtils.localdateToString(carDay):null);
                   });
            ownerRentDetailDTO.setDayAverageAmt(ownerGoodsPriceDetailDTOList.get(0).getCarUnitPrice());
            ownerRentDetailDTO.setOwnerGoodsPriceDetailDTOS(ownerGoodsPriceDetailDTOList);
        }


        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);
        ownerRentDetailDTO.setReqTimeStr(orderDTO.getReqTime()!=null? LocalDateTimeUtils.localdateToString(orderDTO.getReqTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setRevertTimeStr(orderDTO.getExpRevertTime()!=null? LocalDateTimeUtils.localdateToString(orderDTO.getExpRevertTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setRentTimeStr(orderDTO.getExpRentTime()!=null?LocalDateTimeUtils.localdateToString(orderDTO.getExpRentTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        return ResponseData.success(ownerRentDetailDTO);
    }

    public ResponseData<RenterOwnerPriceDTO> renterOwnerPrice(String orderNo, String ownerOrderNo) {
        //车主补贴
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
        ownerOrderSubsidyDetailEntities.stream().forEach(x->{
            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
        });
        //补贴
        RenterOwnerPriceDTO renterOwnerPriceDTO = CostStatUtils.ownerRenterPrice(ownerOrderSubsidyDetailDTOS);
        return ResponseData.success(renterOwnerPriceDTO);
    }


    public ResponseData<ServiceDetailDTO> serviceDetail(String orderNo, String ownerOrderNo) {
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
        OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderNo);

        /**
         * 车主端代管车服务费
         */
        OwnerOrderPurchaseDetailEntity proxyExpense = ownerCosts.getProxyExpense();
        int proxyExpenseTotalAmount = 0;
        if(proxyExpense != null){
             proxyExpenseTotalAmount = proxyExpense.getTotalAmount();
        }
        /**
         * 车主端平台服务费
         */
       OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
       int serviceExpenseTotalAmount = 0;
       if(serviceExpense != null){
           serviceExpenseTotalAmount = serviceExpense.getTotalAmount();
       }
        ServiceDetailDTO serviceDetailDTO = new ServiceDetailDTO();
        serviceDetailDTO.setCarType(CarOwnerTypeEnum.getNameByCode(ownerGoodsDetail.getCarOwnerType()));
        serviceDetailDTO.setServiceRate(ownerGoodsDetail.getServiceRate());
        serviceDetailDTO.setServiceAmt(proxyExpenseTotalAmount + serviceExpenseTotalAmount);
        return ResponseData.success(serviceDetailDTO);
    }

    public ResponseData<PlatformToOwnerSubsidyDTO> platformToOwnerSubsidy(String orderNo, String ownerOrderNo) {
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
        ownerOrderSubsidyDetailEntities.stream().forEach(x->{
            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
        });
        PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = getPlatformToOwnerSubsidyDTO(ownerOrderSubsidyDetailDTOS);
        return ResponseData.success(platformToOwnerSubsidyDTO);
    }


    public ResponseData<FienAmtDetailDTO> fienAmtDetail(String orderNo, String ownerOrderNo) {
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOwnerOrderNo(ownerOrderNo);
        List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailDTOS = new ArrayList<>();
        Optional.ofNullable(ownerOrderFineDeatailList).orElseGet(ArrayList::new).forEach(x->{
            OwnerOrderFineDeatailDTO ownerOrderFineDeatailDTO = new OwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,ownerOrderFineDeatailDTO);
            ownerOrderFineDeatailDTOS.add(ownerOrderFineDeatailDTO);
        });
        int ownerFine = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.OWNER_FINE, ownerOrderFineDeatailDTOS);
        int ownerGetReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.GET_RETURN_CAR, ownerOrderFineDeatailDTOS);
        int ownerModifyAddrAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.MODIFY_ADDRESS_FINE, ownerOrderFineDeatailDTOS);
        int renterAdvanceReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.RENTER_ADVANCE_RETURN, ownerOrderFineDeatailDTOS);
        int renterDelayReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.RENTER_DELAY_RETURN, ownerOrderFineDeatailDTOS);

        FienAmtDetailDTO fienAmtDetailDTO = new FienAmtDetailDTO();
        fienAmtDetailDTO.setOwnerFienAmt(ownerFine);
        fienAmtDetailDTO.setOwnerGetReturnCarFienAmt(ownerGetReturnCarFienAmt);
        fienAmtDetailDTO.setOwnerModifyAddrAmt(ownerModifyAddrAmt);
        fienAmtDetailDTO.setRenterAdvanceReturnCarFienAmt(renterAdvanceReturnCarFienAmt);
        fienAmtDetailDTO.setRenterDelayReturnCarFienAmt(renterDelayReturnCarFienAmt);
        fienAmtDetailDTO.setOwnerGetReturnCarFienCashNo(FineTypeEnum.GET_RETURN_CAR.getFineType());
        fienAmtDetailDTO.setOwnerModifyAddrAmtCashNo(FineTypeEnum.MODIFY_ADDRESS_FINE.getFineType());
        return ResponseData.success(fienAmtDetailDTO);
    }


    private PlatformToOwnerSubsidyDTO getPlatformToOwnerSubsidyDTO(List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS ){
        int mileageAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_MILEAGE_COST_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int oilSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_OIL_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int washCarSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_WASH_CAR_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int carGoodsLossSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_GOODS_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int delaySubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_DELAY_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int trafficSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_TRAFFIC_SUBSIDY, ownerOrderSubsidyDetailDTOS);
        int incomeSubsidyAmt = CostStatUtils.ownerSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_INCOME_SUBSIDY, ownerOrderSubsidyDetailDTOS);

        List<OwnerOrderSubsidyDetailDTO> platformToOwnerSubsidyList = CostStatUtils.getPlatformToOwnerSubsidyList(ownerOrderSubsidyDetailDTOS);
        int platformToOwnerAmt = CostStatUtils.calAmt(platformToOwnerSubsidyList);
        int otherSubsidyAmt = platformToOwnerAmt - (
                + mileageAmt
                        + oilSubsidyAmt
                        + washCarSubsidyAmt
                        + carGoodsLossSubsidyAmt
                        + delaySubsidyAmt
                        + trafficSubsidyAmt
                        + incomeSubsidyAmt);
        PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = new PlatformToOwnerSubsidyDTO();
        platformToOwnerSubsidyDTO.setMileageAmt(mileageAmt);
        platformToOwnerSubsidyDTO.setOilSubsidyAmt(oilSubsidyAmt);
        platformToOwnerSubsidyDTO.setWashCarSubsidyAmt(washCarSubsidyAmt);
        platformToOwnerSubsidyDTO.setCarGoodsLossSubsidyAmt(carGoodsLossSubsidyAmt);
        platformToOwnerSubsidyDTO.setDelaySubsidyAmt(delaySubsidyAmt);
        platformToOwnerSubsidyDTO.setTrafficSubsidyAmt(trafficSubsidyAmt);
        platformToOwnerSubsidyDTO.setIncomeSubsidyAmt(incomeSubsidyAmt);
        platformToOwnerSubsidyDTO.setOtherSubsidyAmt(otherSubsidyAmt);
        platformToOwnerSubsidyDTO.setTotal(platformToOwnerAmt);
        return platformToOwnerSubsidyDTO;
    }

    public ResponseData<PlatformToOwnerDTO> platformToOwner(String orderNo, String ownerOrderNo) {
        List<OrderConsoleCostDetailEntity> list = orderConsoleCostDetailService.getOrderConsoleCostDetaiByOrderNo(orderNo);
        List<OrderConsoleCostDetailDTO> orderConsoleCostDetailDTOS = new ArrayList<>();
        Optional.ofNullable(list).orElseGet(ArrayList::new).forEach(x->{
            OrderConsoleCostDetailDTO orderConsoleCostDetailDTO = new OrderConsoleCostDetailDTO();
            BeanUtils.copyProperties(x,orderConsoleCostDetailDTO);
            orderConsoleCostDetailDTOS.add(orderConsoleCostDetailDTO);
        });
        
        int oilFee = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.OIL_FEE, orderConsoleCostDetailDTOS);
        int timeOut = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.TIME_OUT, orderConsoleCostDetailDTOS);
        int modifyOrderTimeAndAddrAmt = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.MODIFY_ADDR_TIME, orderConsoleCostDetailDTOS);
        int carWash = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.CAR_WASH, orderConsoleCostDetailDTOS);
        int dlayWait = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.DLAY_WAIT, orderConsoleCostDetailDTOS);
        int stopCar = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.STOP_CAR, orderConsoleCostDetailDTOS);
        int extraMileage = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.EXTRA_MILEAGE, orderConsoleCostDetailDTOS);
        //作为常量，不从计算的结果中获取。
//        OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderNo);
//        OwnerOrderPurchaseDetailEntity renterOrderCostDetail = null;
//        if(ownerCosts != null){
//            renterOrderCostDetail = ownerCosts.getRenterOrderCostDetail();
//        }
        PlatformToOwnerDTO platformToOwnerDTO = new PlatformToOwnerDTO();
//        platformToOwnerDTO.setOliAmt(renterOrderCostDetail!=null?renterOrderCostDetail.getTotalAmount():0);
        platformToOwnerDTO.setOliAmt(oilFee);
        platformToOwnerDTO.setTimeOut(timeOut);
        platformToOwnerDTO.setModifyOrderTimeAndAddrAmt(modifyOrderTimeAndAddrAmt);
        platformToOwnerDTO.setCarWash(carWash);
        platformToOwnerDTO.setDlayWait(dlayWait);
        platformToOwnerDTO.setStopCar(stopCar);
        platformToOwnerDTO.setExtraMileage(extraMileage);
        return ResponseData.success(platformToOwnerDTO);
    }

    public ResponseData<?> updateFien(FienAmtUpdateReqDTO fienAmtUpdateReqDTO) {
        String ownerOrderNo = fienAmtUpdateReqDTO.getOwnerOrderNo();
        OwnerOrderFineDeatailEntity ownerOrderFineDeatailEntity = new OwnerOrderFineDeatailEntity();
        ownerOrderFineDeatailEntity.setOrderNo(fienAmtUpdateReqDTO.getOrderNo());
        ownerOrderFineDeatailEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderFineDeatailEntity.setMemNo(fienAmtUpdateReqDTO.getOwnerMemNo());
        ownerOrderFineDeatailEntity.setFineType(fienAmtUpdateReqDTO.getOwnerGetReturnCarFienCashNo());
        ownerOrderFineDeatailEntity.setFineAmount(fienAmtUpdateReqDTO.getOwnerGetReturnCarFienAmt());
        ownerOrderFineDeatailEntity.setFineType(FineTypeEnum.GET_RETURN_CAR.getFineType());
        ownerOrderFineDeatailEntity.setFineTypeDesc(FineTypeEnum.GET_RETURN_CAR.getFineTypeDesc());
        OwnerOrderFineDeatailEntity getOwnerGetReturnCarFienCashNo = ownerOrderFineDeatailService.getByCashNoAndOwnerOrderNo(ownerOrderNo, fienAmtUpdateReqDTO.getOwnerGetReturnCarFienCashNo());
        if(getOwnerGetReturnCarFienCashNo == null){
            /*if(fienAmtUpdateReqDTO.getOwnerGetReturnCarFienAmt() == 0){
                return ResponseData.success();
            }*/
            ownerOrderFineDeatailEntity.setFineSubsidyCode(FineSubsidyCodeEnum.PLATFORM.getFineSubsidyCode());
            ownerOrderFineDeatailEntity.setFineSubsidyDesc(FineSubsidyCodeEnum.PLATFORM.getFineSubsidyDesc());
            ownerOrderFineDeatailEntity.setFineSubsidySourceCode(FineSubsidyCodeEnum.OWNER.getFineSubsidyCode());
            ownerOrderFineDeatailEntity.setFineSubsidySourceDesc(FineSubsidyCodeEnum.OWNER.getFineSubsidyDesc());
            ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDeatailEntity);
        }else{
            ownerOrderFineDeatailService.updateByCashNoAndOwnerOrderNo(ownerOrderFineDeatailEntity);
        }
        ownerOrderFineDeatailEntity.setFineTypeDesc(FineTypeEnum.MODIFY_ADDRESS_FINE.getFineTypeDesc());
        ownerOrderFineDeatailEntity.setFineType(fienAmtUpdateReqDTO.getOwnerModifyAddrAmtCashNo());
        ownerOrderFineDeatailEntity.setFineAmount(fienAmtUpdateReqDTO.getOwnerModifyAddrAmt());
        OwnerOrderFineDeatailEntity byCashNoAndOwnerOrderNo = ownerOrderFineDeatailService.getByCashNoAndOwnerOrderNo(ownerOrderNo, fienAmtUpdateReqDTO.getOwnerModifyAddrAmtCashNo());
        if(byCashNoAndOwnerOrderNo == null){
           /* if(fienAmtUpdateReqDTO.getOwnerModifyAddrAmt() == 0){
                return ResponseData.success();
            }*/
            ownerOrderFineDeatailEntity.setFineSubsidyCode(FineSubsidyCodeEnum.PLATFORM.getFineSubsidyCode());
            ownerOrderFineDeatailEntity.setFineSubsidyDesc(FineSubsidyCodeEnum.PLATFORM.getFineSubsidyDesc());
            ownerOrderFineDeatailEntity.setFineSubsidySourceCode(FineSubsidyCodeEnum.OWNER.getFineSubsidyCode());
            ownerOrderFineDeatailEntity.setFineSubsidySourceDesc(FineSubsidyCodeEnum.OWNER.getFineSubsidyDesc());
            ownerOrderFineDeatailService.addOwnerOrderFineRecord(ownerOrderFineDeatailEntity);
        }else{
            ownerOrderFineDeatailService.updateByCashNoAndOwnerOrderNo(ownerOrderFineDeatailEntity);
        }
        return ResponseData.success();
    }
}
