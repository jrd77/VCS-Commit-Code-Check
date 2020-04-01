/**
 * 
 */
package com.atzuche.order.coreapi.service;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderDTO;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.account.CostTypeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.exceptions.OwnerOrderDetailNotFoundException;
import com.atzuche.order.commons.exceptions.OwnerOrderNotFoundException;
import com.atzuche.order.commons.vo.OwnerFineVO;
import com.atzuche.order.commons.vo.OwnerSubsidyDetailVO;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailDataVO;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailReqVO;
import com.atzuche.order.commons.vo.res.OwnerCostDetailVO;
import com.atzuche.order.commons.vo.res.OwnerCostSettleDetailVO;
import com.atzuche.order.commons.vo.res.OwnerIncrementDetailVO;
import com.atzuche.order.ownercost.entity.*;
import com.atzuche.order.ownercost.service.*;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jing.huang
 *
 */
@Slf4j
@Service
public class OwnerCostFacadeService {
	
	@Autowired
	private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
	@Autowired
	private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;
	@Autowired
	private AccountDebtDetailNoTService  accountDebtDetailNoTService;
	@Autowired
	private OrderService orderService;
    @Autowired
    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    private OwnerOrderFineDeatailService ownerOrderFineDeatailService;
    @Autowired
    private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;

	public OwnerCostSettleDetailVO getOwnerCostSettleDetail(String orderNo, String ownerNo) {
		// 定义返回对象
		OwnerCostSettleDetailVO vo = new OwnerCostSettleDetailVO();
		//参数回写
		vo.setOrderNo(orderNo);
		vo.setMemNo(ownerNo);
		
		//查询车主罚金
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetails(orderNo,ownerNo);
        // 车主结算记录存在 且 车主收益 已审核通过  返回  罚金 金额
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
        	//车主端代管车服务费
            int proxyExpenseAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.PROXY_CHARGE);
            //车主端平台服务费
            int serviceExpenseAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SERVICE_CHARGE);
            //服务费
            vo.setPlatformSrvAmt(proxyExpenseAmt+serviceExpenseAmt);
            
            //GPS服务费
            int gpsSrvAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.GPS_SERVICE_AMT);
            vo.setGpsSrvAmt(gpsSrvAmt);
            
            //GPS押金
            int gpsDepositAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.HW_DEPOSIT_DEBT);
            vo.setGpsDepositAmt(gpsDepositAmt);
            
            //调价  //车主给租客的调价
            int adjustAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST);
            ////租客给车主的调价
            int adjustAmt2 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST);
            vo.setAdjustAmt(adjustAmt+adjustAmt2);
            
            //租金补贴
            int subsidyAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT);
            //车主券补贴  
           // int subsidyAmt2 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST);
            vo.setSubsidyToRenterAmt(subsidyAmt/*+subsidyAmt2*/);

            //3获取车主补贴
           /* int subsidyAmount = accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.OWNER_SUBSIDY.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();*/
            //8 管理后台补贴
            int consoleSubsidyAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.CONSOLE_SUBSIDY.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //平台给车主的补贴
            vo.setFromPlatformSubsidyAmt(consoleSubsidyAmt);
            
            //4获取车主费用
//            int purchaseAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_DEBT.getCashNo().equals(obj.getSourceCode());})
//                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
//            //5获取车主增值服务费用
//            int incrementAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo().equals(obj.getSourceCode());})
//                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();

            //车主罚金  违约罚金
            int fineAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.OWNER_FINE.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            int fineAmt2 =accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.CONSOLE_FINE.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            vo.setFineAmt(fineAmt+fineAmt2);
                   
            
        	//租金
        	int rentAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.RENT_AMT);
        	vo.setRentAmt(rentAmt);
        	
            //油费
            int oilCost =  getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.OIL_COST_OWNER);
            //7 获取车主油费(交接车油费)
            int oilAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST);
            vo.setOilAmt(oilCost+oilAmt);
            
            //平台加油服务费
            int platformOilCostAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE);
            vo.setPlatformOilCostAmt(platformOilCostAmt);
            //超里程费用
            int mileageCostAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.MILEAGE_COST_OWNER);
            vo.setMileageCostAmt(mileageCostAmt);  
            
            //配送服务费
            int getCarAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SRV_GET_COST_OWNER);
            int returnCarAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SRV_RETURN_COST_OWNER);
            vo.setGetReturnCarAmt(getCarAmt+returnCarAmt);

            //本订单收益
            int incomeAmt = accountOwnerIncomeExamineNoTService.getTotalAccountOwnerIncomeExamineByOrderNo(orderNo);
            vo.setIncomeAmt(incomeAmt);
            
            
            int payToPlatformAmt1 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.TIME_OUT);
            int payToPlatformAmt2 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.CAR_WASH);
            int payToPlatformAmt3 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.DLAY_WAIT);
            int payToPlatformAmt4 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.STOP_CAR);
            int payToPlatformAmt5 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.EXTRA_MILEAGE);
            int payToPlatformAmt6 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.MODIFY_ADDR_TIME);
            int payToPlatformAmt7 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.OIL_FEE);
            
            int payToPlatformAmt = payToPlatformAmt1 + payToPlatformAmt2 + payToPlatformAmt3 + payToPlatformAmt4 + payToPlatformAmt5 + payToPlatformAmt6 + payToPlatformAmt7;
            //车主需支付给平台的费用
            vo.setPayToPlatformAmt(payToPlatformAmt);
            
            //欠款原欠费金额
            //新欠费金额
            AccountDebtDetailEntity entity = accountDebtDetailNoTService.getTotalAccountDebtDetailEntity(orderNo, ownerNo);
            int oldDebtAmt = 0;
            int newDebtAmt = 0;
            if(entity != null) {
            	oldDebtAmt = entity.getOrderDebtAmt();
            	newDebtAmt = entity.getCurrentDebtAmt();
            }
            vo.setOldDebtAmt(oldDebtAmt);
            vo.setNewDebtAmt(newDebtAmt);
        }
        
		return vo;
	}
	
	/**
	 * 根据费用编码来获取
	 * @param accountOwnerCostSettleDetails
	 * @param cashCode
	 * @return
	 */
	private int getSettleAmtByCashCode(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails,OwnerCashCodeEnum cashCode) {
		int amt  = accountOwnerCostSettleDetails.stream().filter(obj ->{
            return cashCode.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
		return amt;
	}
	/**
	 * 方法重载
	 * @param accountOwnerCostSettleDetails
	 * @param cashCode
	 * @return
	 */
	private int getSettleAmtByCashCode(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails,RenterCashCodeEnum cashCode) {
		int amt  = accountOwnerCostSettleDetails.stream().filter(obj ->{
            return cashCode.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
		return amt;
	}
	private int getSettleAmtByCashCode(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails,ConsoleCashCodeEnum cashCode) {
		int amt  = accountOwnerCostSettleDetails.stream().filter(obj ->{
            return cashCode.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
		return amt;
	}
	
	
	/**
     * 判断车主结算明细 是否是罚金费用 记录
     */
    private boolean getOwnerFineCostType(Integer costType){
        boolean result = false;
        if(CostTypeEnum.CONSOLE_FINE.getCode().equals(costType)){
            result = true;
        }
        if(CostTypeEnum.OWNER_FINE.getCode().equals(costType)){
            result = true;
        }
        if(CostTypeEnum.RENTER_FINE.getCode().equals(costType)){
            result = true;
        }
        return result;
    }
    
	public List<OwnerCostSettleDetailVO> listOwnerCostSettleDetail(OwnerCostSettleDetailReqVO req) {
		List<OwnerCostSettleDetailVO> backList = Lists.newArrayList();
		List<OwnerCostSettleDetailDataVO> listOwnerCostSettleDetailDataVO = req.getListOwnerCostSettleDetailDataVO();
		if(listOwnerCostSettleDetailDataVO==null){
			throw new InputErrorException();
		}
		
		for (OwnerCostSettleDetailDataVO ownerCostSettleDetailDataVO : listOwnerCostSettleDetailDataVO) {
			String orderNo = ownerCostSettleDetailDataVO.getOrderNo();
			String ownerNo = ownerCostSettleDetailDataVO.getOwnerNo();

			OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
			if(orderEntity==null){
				//默认对象
				OwnerCostSettleDetailVO vo = new OwnerCostSettleDetailVO();
				vo.setOrderNo(orderNo);
				vo.setMemNo(ownerNo);
				backList.add(vo);
			}else {
				//查询
				backList.add(getOwnerCostSettleDetail(orderNo, ownerNo));
			}
		}
		
		return backList;
	}

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
        List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList = consoleOwnerOrderFineDeatailService.selectByOrderNo(orderNo,ownerMemNo);
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
        //ownerFineVO.setRenterDelayReturn(getOwnerFileByType(consoleFineList, orderFineList, FineTypeEnum.RENTER_DELAY_RETURN));
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
    private Integer getIncrement(List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailEntities, OwnerCashCodeEnum ownerCashCodeEnum){
        Integer incrementAmt = ownerOrderIncrementDetailEntities.stream()
                .filter(x -> ownerCashCodeEnum.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.summingInt(OwnerOrderIncrementDetailEntity::getTotalAmount));
        return incrementAmt==null?0:incrementAmt;
    }

    private Integer getOwnerSubsidy(List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailEntities, List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList, String ownerCashCode){
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
    private Integer getOwnerFileByType(List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailList, List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailList, FineTypeEnum fineType){
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
