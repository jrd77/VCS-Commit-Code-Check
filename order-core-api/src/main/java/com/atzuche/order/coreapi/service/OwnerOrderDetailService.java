package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.CostStatUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.entity.ownerOrderDetail.*;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.ownercost.service.*;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleCostDetailService;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.settle.service.OrderSettleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车主费用明细
 * @author jing.huang
 *
 */
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
    @Autowired
    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired
    private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    
    public OwnerRentDetailDTO ownerRentDetail(String orderNo, String ownerOrderNo) {
        //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity == null){
            log.error("获取订单数据为空orderNo={}",orderNo);
            throw new OrderNotFoundException(orderNo);
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
            //bugfix 不能从某天来获取，从owner_order_purchase_detail获取。20200205 huangjing
//            ownerRentDetailDTO.setDayAverageAmt(ownerGoodsPriceDetailDTOList.get(0).getCarUnitPrice());
            ownerRentDetailDTO.setOwnerGoodsPriceDetailDTOS(ownerGoodsPriceDetailDTOList);
        }
        
        //111
        ///车主租金组成 日均价的获取。20200205 huangjing
        List<OwnerOrderPurchaseDetailEntity> lstPurchaseDetail = ownerOrderPurchaseDetailService.listOwnerOrderPurchaseDetail(orderNo, ownerOrderNo);
        for (OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity : lstPurchaseDetail) {
			if(OwnerCashCodeEnum.RENT_AMT.getCashNo().equals(ownerOrderPurchaseDetailEntity.getCostCode())) {
				ownerRentDetailDTO.setDayAverageAmt(ownerOrderPurchaseDetailEntity.getUnitPrice());
				break;
			}
		}
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);
        ownerRentDetailDTO.setReqTimeStr(ownerOrderEntity.getCreateTime()!=null? LocalDateTimeUtils.localdateToString(ownerOrderEntity.getCreateTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setRevertTimeStr(ownerOrderEntity.getExpRevertTime()!=null? LocalDateTimeUtils.localdateToString(ownerOrderEntity.getExpRevertTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setRentTimeStr(ownerOrderEntity.getExpRentTime()!=null?LocalDateTimeUtils.localdateToString(ownerOrderEntity.getExpRentTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        ownerRentDetailDTO.setCarPlateNum(ownerGoodsDetail.getCarPlateNum());
        return ownerRentDetailDTO;
    }

    public RenterOwnerPriceDTO renterOwnerPrice(String orderNo, String ownerOrderNo) {
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
        return renterOwnerPriceDTO;
    }


    public ServiceDetailDTO serviceDetail(String orderNo, String ownerOrderNo) {
        OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderNo, false);
        //OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo, ownerOrderNo);
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailList = ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(orderNo, ownerOrderNo);

        List<OwnerOrderIncrementDetailEntity> collect = Optional.of(ownerOrderIncrementDetailList)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.toList());
        Integer serviceAmt = 0;
        if(collect != null && collect.size()>=1){
            serviceAmt = collect.get(0).getTotalAmount();
        }
        /**
         * 车主端代管车服务费
         */
        /*OwnerOrderPurchaseDetailEntity proxyExpense = ownerCosts.getProxyExpense();
        int proxyExpenseTotalAmount = 0;
        if(proxyExpense != null){
             proxyExpenseTotalAmount = proxyExpense.getTotalAmount();
        }*/
        /**
         * 车主端平台服务费
         */
     /*  OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
       int serviceExpenseTotalAmount = 0;
       if(serviceExpense != null){
           serviceExpenseTotalAmount = serviceExpense.getTotalAmount();
       }*/
        ServiceDetailDTO serviceDetailDTO = new ServiceDetailDTO();
        serviceDetailDTO.setCarType(CarOwnerTypeEnum.getNameByCode(ownerGoodsDetail.getCarOwnerType()));
        serviceDetailDTO.setServiceRate(ownerGoodsDetail.getServiceRate());
        //serviceDetailDTO.setServiceAmt(proxyExpenseTotalAmount + serviceExpenseTotalAmount);
        serviceDetailDTO.setServiceAmt(serviceAmt);
        return serviceDetailDTO;
    }

    public PlatformToOwnerSubsidyDTO platformToOwnerSubsidy(String orderNo, String ownerOrderNo,String memNo) {
    	//old code
//        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOS = new ArrayList<>();
//        List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities = ownerOrderSubsidyDetailService.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
//        ownerOrderSubsidyDetailEntities.stream().forEach(x->{
//            OwnerOrderSubsidyDetailDTO ownerOrderSubsidyDetailDTO = new OwnerOrderSubsidyDetailDTO();
//            BeanUtils.copyProperties(x,ownerOrderSubsidyDetailDTO);
//            ownerOrderSubsidyDetailDTOS.add(ownerOrderSubsidyDetailDTO);
//        });
//      PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = getPlatformToOwnerSubsidyDTO(ownerOrderSubsidyDetailDTOS);
    	
    	
        ///order_console_subsidy_detail
        List<OrderConsoleSubsidyDetailEntity> lst = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo, memNo);
        
        //转换
        List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity> lstReal = new ArrayList<>();
        lst.stream().forEach(x->{
        	com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity entity = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity();
		      BeanUtils.copyProperties(x,entity);
		      lstReal.add(entity);
		  });

        PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO = getPlatformToOwnerSubsidyList(lstReal);
        return platformToOwnerSubsidyDTO;
    }


    public FienAmtDetailDTO fienAmtDetail(String orderNo, String ownerOrderNo) {
        List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList = ownerOrderFineDeatailService.getOwnerOrderFineDeatailByOwnerOrderNo(ownerOrderNo);
        List<OwnerOrderFineDeatailDTO> ownerOrderFineDeatailDTOS = new ArrayList<>();
        Optional.ofNullable(ownerOrderFineDeatailList).orElseGet(ArrayList::new).forEach(x->{
            OwnerOrderFineDeatailDTO ownerOrderFineDeatailDTO = new OwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,ownerOrderFineDeatailDTO);
            ownerOrderFineDeatailDTOS.add(ownerOrderFineDeatailDTO);
        });
//        int ownerFine = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.OWNER_FINE, ownerOrderFineDeatailDTOS);  //CANCEL_FINE
        int ownerFine = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.CANCEL_FINE, ownerOrderFineDeatailDTOS);
        int ownerGetReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.GET_RETURN_CAR, ownerOrderFineDeatailDTOS);
        int ownerModifyAddrAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.MODIFY_ADDRESS_FINE, ownerOrderFineDeatailDTOS);
        
//        int renterAdvanceReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.RENTER_ADVANCE_RETURN, ownerOrderFineDeatailDTOS);
//        int renterDelayReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.RENTER_DELAY_RETURN, ownerOrderFineDeatailDTOS);
        
        int renterAdvanceReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.MODIFY_ADVANCE, ownerOrderFineDeatailDTOS);
        int renterDelayReturnCarFienAmt = CostStatUtils.calOwnerFineByCashNo(FineTypeEnum.DELAY_FINE, ownerOrderFineDeatailDTOS);
        
        //add by huangjing 200217
        OwnerOrderEntity entity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
        int consoleRenterAdvanceReturnCarFienAmt = 0;
        int consoleRenterDelayReturnCarFienAmt = 0;
        if(entity != null) {
	        List<ConsoleOwnerOrderFineDeatailEntity>  consoleOwnerOrderFineDeatailEntityList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo,entity.getMemNo());
	        List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOS = new ArrayList<>();
	        Optional.ofNullable(consoleOwnerOrderFineDeatailEntityList).orElseGet(ArrayList::new).forEach(x->{
	        	ConsoleOwnerOrderFineDeatailDTO consoleOwnerOrderFineDeatailDTO = new ConsoleOwnerOrderFineDeatailDTO();
	            BeanUtils.copyProperties(x,consoleOwnerOrderFineDeatailDTO);
	            consoleOwnerOrderFineDeatailDTOS.add(consoleOwnerOrderFineDeatailDTO);
	        });
	        
	        //费用编码不对
	        consoleRenterAdvanceReturnCarFienAmt = CostStatUtils.calConsoleOwnerFineByCashNo(FineTypeEnum.MODIFY_ADVANCE, consoleOwnerOrderFineDeatailDTOS);
	        consoleRenterDelayReturnCarFienAmt = CostStatUtils.calConsoleOwnerFineByCashNo(FineTypeEnum.DELAY_FINE, consoleOwnerOrderFineDeatailDTOS);
        }
        
        FienAmtDetailDTO fienAmtDetailDTO = new FienAmtDetailDTO();
        fienAmtDetailDTO.setOwnerFienAmt(ownerFine); //??如何取值
        
        //取值OK
        fienAmtDetailDTO.setOwnerGetReturnCarFienAmt(ownerGetReturnCarFienAmt);
        fienAmtDetailDTO.setOwnerModifyAddrAmt(ownerModifyAddrAmt);
        
        //需要从console_owner_order_fine_deatail获取。 是租客端添加的费用。
        fienAmtDetailDTO.setRenterAdvanceReturnCarFienAmt(renterAdvanceReturnCarFienAmt+consoleRenterAdvanceReturnCarFienAmt);
        fienAmtDetailDTO.setRenterDelayReturnCarFienAmt(renterDelayReturnCarFienAmt+consoleRenterDelayReturnCarFienAmt);
        
        fienAmtDetailDTO.setOwnerGetReturnCarFienCashNo(FineTypeEnum.GET_RETURN_CAR.getFineType());
        fienAmtDetailDTO.setOwnerModifyAddrAmtCashNo(FineTypeEnum.MODIFY_ADDRESS_FINE.getFineType());
        return fienAmtDetailDTO;
    }

    /*方法重载*/
    private PlatformToOwnerSubsidyDTO getPlatformToOwnerSubsidyList(List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity> list){
        int mileageAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_MILEAGE_COST_SUBSIDY, list);
        int oilSubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_OIL_SUBSIDY, list);
        int washCarSubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_WASH_CAR_SUBSIDY, list);
        int carGoodsLossSubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_GOODS_SUBSIDY, list);
        int delaySubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_DELAY_SUBSIDY, list);
        int trafficSubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_TRAFFIC_SUBSIDY, list);
        int incomeSubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_INCOME_SUBSIDY, list);
        int otherSubsidyAmt = CostStatUtils.orderConsoleSubsidtyAmtFilterByCashNo(OwnerCashCodeEnum.OWNER_OTHER_SUBSIDY, list);

//        List<OwnerOrderSubsidyDetailDTO> platformToOwnerSubsidyList = CostStatUtils.getPlatformToOwnerSubsidyList(list);
//        int platformToOwnerAmt = CostStatUtils.calAmt(platformToOwnerSubsidyList);
//        int otherSubsidyAmt = platformToOwnerAmt - (
//                + mileageAmt
//                        + oilSubsidyAmt
//                        + washCarSubsidyAmt
//                        + carGoodsLossSubsidyAmt
//                        + delaySubsidyAmt
//                        + trafficSubsidyAmt
//                        + incomeSubsidyAmt);
       
        int platformToOwnerAmt = 
       mileageAmt
      + oilSubsidyAmt
      + washCarSubsidyAmt
      + carGoodsLossSubsidyAmt
      + delaySubsidyAmt
      + trafficSubsidyAmt
      + incomeSubsidyAmt + otherSubsidyAmt;
        
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

    public PlatformToOwnerDTO platformToOwner(String orderNo, String ownerOrderNo) {
    	OwnerOrderEntity entity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
    	String ownerNo = "0";
    	if(entity != null) {
    		ownerNo = entity.getMemNo();
    	}
        List<OrderConsoleCostDetailEntity> list = orderConsoleCostDetailService.selectByOrderNoAndMemNo(orderNo,ownerNo);
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
        platformToOwnerDTO.setOliAmt(String.valueOf(NumberUtils.convertNumberToZhengshu(oilFee)));
        platformToOwnerDTO.setTimeOut(String.valueOf(NumberUtils.convertNumberToZhengshu(timeOut)));
        platformToOwnerDTO.setModifyOrderTimeAndAddrAmt(String.valueOf(NumberUtils.convertNumberToZhengshu(modifyOrderTimeAndAddrAmt)));
        platformToOwnerDTO.setCarWash(String.valueOf(NumberUtils.convertNumberToZhengshu(carWash)));
        platformToOwnerDTO.setDlayWait(String.valueOf(NumberUtils.convertNumberToZhengshu(dlayWait)));
        platformToOwnerDTO.setStopCar(String.valueOf(NumberUtils.convertNumberToZhengshu(stopCar)));
        platformToOwnerDTO.setExtraMileage(String.valueOf(NumberUtils.convertNumberToZhengshu(extraMileage)));
        return platformToOwnerDTO;
    }

    public void updateFien(FienAmtUpdateReqDTO fienAmtUpdateReqDTO) {
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
        
        //两处的金额前端录入什么值就保存什么值。
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
    }

    public List<ConsoleOwnerOrderFineDeatailDTO> fienAmtDetailList(String orderNo, String ownerMemNo) {
        List<ConsoleOwnerOrderFineDeatailEntity> list = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo, ownerMemNo);
        List<ConsoleOwnerOrderFineDeatailDTO> consoleOwnerOrderFineDeatailDTOS = new ArrayList<>();
        Optional.ofNullable(list).orElseGet(ArrayList::new)
                .stream()
                .filter(x-> FineTypeEnum.MODIFY_ADDRESS_FINE.getFineType().equals(x.getFineType()))
                .forEach(x->{
            ConsoleOwnerOrderFineDeatailDTO dto = new ConsoleOwnerOrderFineDeatailDTO();
            BeanUtils.copyProperties(x,dto);
            consoleOwnerOrderFineDeatailDTOS.add(dto);
        });
        return consoleOwnerOrderFineDeatailDTOS;
    }
}
