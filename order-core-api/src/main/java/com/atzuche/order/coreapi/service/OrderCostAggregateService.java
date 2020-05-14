package com.atzuche.order.coreapi.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.FineTypeCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.PlatformToOwnerSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.PlatformToRenterSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderRenterOrderNotFindException;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderCostAggregateService {

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private OwnerOrderService ownerOrderService;
	@Autowired
	private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
	@Autowired
	private ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
	@Autowired
	private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;

	/**
	 * 违约罚金 修改违约罚金
	 * @param renterCostReqVO
	 */
	public void updatefineAmtListByOrderNo(RenterFineCostReqVO renterCostReqVO) {

		CostBaseDTO costBaseDTO = new CostBaseDTO();
		CostBaseDTO ownerCostDTO = new CostBaseDTO();
		// 根据订单号查询会员号
		// 主订单
		OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
		if (orderEntity == null) {
			log.error("获取订单数据为空orderNo={}", renterCostReqVO.getOrderNo());
			throw new ModifyOrderRenterOrderNotFindException();
		}
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(renterCostReqVO.getOrderNo());
		if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus()
				|| orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
			log.error("已经结算不允许编辑orderNo={}", renterCostReqVO.getOrderNo());
			throw new NotAllowedEditException();
		}

//    	车主会员号查询。
		OwnerOrderEntity orderEntityOwner = null;
		// 否则根据主订单号查询,有效的车主记录。
		orderEntityOwner = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(renterCostReqVO.getOrderNo());

		// 封装订单号和会员号
		costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getMemNoRenter());

		if (orderEntityOwner != null) {
			ownerCostDTO.setOrderNo(renterCostReqVO.getOrderNo());
			ownerCostDTO.setMemNo(orderEntityOwner.getMemNo());
		}

		// 租客提前还车罚金
		String renterBeforeReturnCarFineAmt = renterCostReqVO.getRenterBeforeReturnCarFineAmt();
		// 租客延迟还车罚金
		String renterDelayReturnCarFineAmt = renterCostReqVO.getRenterDelayReturnCarFineAmt();
		// 统一设置修改人名称。20200205 huangjing
		String userName = renterCostReqVO.getOperatorName(); // 获取的管理后台的用户名。
		// 租客收益处理
		if (StringUtils.isNotBlank(renterBeforeReturnCarFineAmt)) {
			ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity = consoleRenterOrderFineDeatailService
					.fineDataConvert(costBaseDTO, -Integer.valueOf(renterBeforeReturnCarFineAmt),
							FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER,
							FineTypeCashCodeEnum.MODIFY_ADVANCE);

			
			if (consoleRenterOrderFineDeatailEntity != null) {
				consoleRenterOrderFineDeatailEntity.setUpdateOp(userName);
				consoleRenterOrderFineDeatailEntity.setCreateOp(userName);
				consoleRenterOrderFineDeatailEntity.setOperatorId(userName);
				consoleRenterOrderFineDeatailService
						.saveOrUpdateConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
			}

			// 当前存在的情况下，否则归平台。
			if (orderEntityOwner != null) {
				// 同时增加反向记录，算车主的收益 200217 通过平台中转
				ConsoleOwnerOrderFineDeatailEntity ownerEntity = consoleOwnerOrderFineDeatailService.fineDataConvert(
						ownerCostDTO, Integer.valueOf(renterBeforeReturnCarFineAmt), FineSubsidyCodeEnum.OWNER,
						FineSubsidySourceCodeEnum.PLATFORM, FineTypeCashCodeEnum.MODIFY_ADVANCE);
				if (ownerEntity != null) {
					ownerEntity.setUpdateOp(userName);
					ownerEntity.setCreateOp(userName);
					ownerEntity.setOperatorId(userName);
					consoleOwnerOrderFineDeatailService.saveOrUpdateConsoleRenterOrderFineDeatail(ownerEntity);
				}

			}

		}

		// 保存入库为负数，来源为租客。
		if (StringUtils.isNotBlank(renterDelayReturnCarFineAmt)) {
			ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity = consoleRenterOrderFineDeatailService
					.fineDataConvert(costBaseDTO, -Integer.valueOf(renterDelayReturnCarFineAmt),
							FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER,
							FineTypeCashCodeEnum.DELAY_FINE);

			if (consoleRenterOrderFineDeatailEntity != null) {

				consoleRenterOrderFineDeatailEntity.setUpdateOp(userName);
				consoleRenterOrderFineDeatailEntity.setCreateOp(userName);
				consoleRenterOrderFineDeatailEntity.setOperatorId(userName);

				consoleRenterOrderFineDeatailService
						.saveOrUpdateConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);

			}

			// 当前存在的情况下，否则归平台。
			if (orderEntityOwner != null) {
				// 同时增加反向记录，算车主的收益 200217 通过平台中转
				ConsoleOwnerOrderFineDeatailEntity ownerEntity = consoleOwnerOrderFineDeatailService.fineDataConvert(
						ownerCostDTO, Integer.valueOf(renterDelayReturnCarFineAmt), FineSubsidyCodeEnum.OWNER,
						FineSubsidySourceCodeEnum.PLATFORM, FineTypeCashCodeEnum.DELAY_FINE);
				if (ownerEntity != null) {
					ownerEntity.setUpdateOp(userName);
					ownerEntity.setCreateOp(userName);
					ownerEntity.setOperatorId(userName);
					consoleOwnerOrderFineDeatailService.saveOrUpdateConsoleRenterOrderFineDeatail(ownerEntity);
				}
			}

		}

	}
	
	
	/**
	 * 平台给租客的补贴
	 * @param renterCostReqVO
	 */
	public void updatePlatFormToRenterListByOrderNo(PlatformToRenterSubsidyReqVO renterCostReqVO) {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		// 根据订单号查询会员号
		// 主订单
		OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
		if (orderEntity == null) {
			log.error("获取订单数据为空orderNo={}", renterCostReqVO.getOrderNo());
			throw new ModifyOrderRenterOrderNotFindException();
		}
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(renterCostReqVO.getOrderNo());
		if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus()
				|| orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
			log.error("已经结算不允许编辑orderNo={}", renterCostReqVO.getOrderNo());
			throw new NotAllowedEditException();
		}
		int dispatching = renterCostReqVO.getDispatchingSubsidy() != null
				? Integer.valueOf(renterCostReqVO.getDispatchingSubsidy())
				: 0;
		int oil = renterCostReqVO.getOilSubsidy() != null ? Integer.valueOf(renterCostReqVO.getOilSubsidy()) : 0;
		int cleancar = renterCostReqVO.getCleanCarSubsidy() != null
				? Integer.valueOf(renterCostReqVO.getCleanCarSubsidy())
				: 0;
		int getReturnDelay = renterCostReqVO.getGetReturnDelaySubsidy() != null
				? Integer.valueOf(renterCostReqVO.getGetReturnDelaySubsidy())
				: 0;
		int delay = renterCostReqVO.getDelaySubsidy() != null ? Integer.valueOf(renterCostReqVO.getDelaySubsidy()) : 0;

		int traffic = renterCostReqVO.getTrafficSubsidy() != null ? Integer.valueOf(renterCostReqVO.getTrafficSubsidy())
				: 0;
		int insure = renterCostReqVO.getInsureSubsidy() != null ? Integer.valueOf(renterCostReqVO.getInsureSubsidy())
				: 0;

		int rentamt = renterCostReqVO.getRentAmtSubsidy() != null ? Integer.valueOf(renterCostReqVO.getRentAmtSubsidy())
				: 0;
		int other = renterCostReqVO.getOtherSubsidy() != null ? Integer.valueOf(renterCostReqVO.getOtherSubsidy()) : 0;

		int abatement = renterCostReqVO.getAbatementSubsidy() != null
				? Integer.valueOf(renterCostReqVO.getAbatementSubsidy())
				: 0;
		int fee = renterCostReqVO.getFeeSubsidy() != null ? Integer.valueOf(renterCostReqVO.getFeeSubsidy()) : 0;

		// 封装订单号和会员号
		costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getMemNoRenter());

		SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.PLATFORM; // 固定
		SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
		// 获取的管理后台的用户名。
		String userName = renterCostReqVO.getOperatorName();
		// 升级车辆补贴
		if (dispatching != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO,
					dispatching, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT,
					RenterCashCodeEnum.SUBSIDY_DISPATCHING_AMT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);

			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (oil != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, oil,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OIL);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (cleancar != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, cleancar,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_CLEANCAR);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (getReturnDelay != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO,
					getReturnDelay, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT,
					RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (delay != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, delay,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_DELAY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (traffic != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, traffic,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_TRAFFIC);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (insure != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, insure,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_INSURE);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (rentamt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, rentamt,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_RENTAMT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (other != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, other,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OTHER);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (abatement != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, abatement,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_ABATEMENT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

		if (fee != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, fee,
					targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_FEE);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}

	}
	
	
	/**
	 * 租金补贴
	 * @param ownerCostReqVO
	 */
	public void ownerToRenterRentAmtSubsidy(OwnerToRenterSubsidyReqVO ownerCostReqVO) {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(ownerCostReqVO.getOrderNo());
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus() || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()){
            log.error("已经结算不允许编辑orderNo={}",ownerCostReqVO.getOrderNo());
            throw new NotAllowedEditException();
        }
		/**
		 * 查询有效的租客子订单
		 */
		RenterOrderEntity orderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(ownerCostReqVO.getOrderNo());
		if(orderEntity == null){
	    	log.error("获取订单数据(租客)为空orderNo={}",ownerCostReqVO.getOrderNo());
	        throw new ModifyOrderRenterOrderNotFindException();
	    }
	    
	    OwnerOrderEntity orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
        if(orderEntityOwner == null){
        	log.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
            throw new ModifyOrderRenterOrderNotFindException();
        }
        
        int rentAmt = ownerCostReqVO.getOwnerSubsidyRentAmt()!=null?Integer.valueOf(ownerCostReqVO.getOwnerSubsidyRentAmt()):0;
        //不管输入正负号，按系统逻辑都按正数，保存的按负数来处理。逻辑转换
        rentAmt = NumberUtils.convertNumberToZhengshu(rentAmt);
        
        SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.OWNER; //固定
    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
		//封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
		///
		costBaseDTO.setOwnerOrderNo(orderEntityOwner.getOwnerOrderNo());
		//修改金额为0的情况,前端传什么值，保存什么值。
		OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity  =  ownerOrderSubsidyDetailService.buildData(costBaseDTO, -rentAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT);
    	ownerOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(ownerOrderSubsidyDetailEntity);
		
    	//反向记录
		//封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getRenterMemNo());
		///
		costBaseDTO.setRenterOrderNo(orderEntity.getRenterOrderNo());
    	RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, rentAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT);
		renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
	}
	
	
	/**
	 * 平台给车主的补贴
	 * @param ownerCostReqVO
	 */
	public void updatePlatFormToOwnerListByOrderNo(PlatformToOwnerSubsidyReqVO ownerCostReqVO) {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		//根据订单号查询会员号
		//主订单
		OwnerOrderEntity orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
        if(orderEntityOwner == null){
        	log.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
            throw new ModifyOrderRenterOrderNotFindException();
        }
	    int mileageAmt = ownerCostReqVO.getMileageAmt()!=null?Integer.valueOf(ownerCostReqVO.getMileageAmt()):0;
	    int oilSubsidyAmt = ownerCostReqVO.getOilSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getOilSubsidyAmt()):0;
	    int washCarSubsidyAmt = ownerCostReqVO.getWashCarSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getWashCarSubsidyAmt()):0;
	    int carGoodsLossSubsidyAmt = ownerCostReqVO.getCarGoodsLossSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getCarGoodsLossSubsidyAmt()):0;
	    int delaySubsidyAmt = ownerCostReqVO.getDelaySubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getDelaySubsidyAmt()):0;
	    int trafficSubsidyAmt = ownerCostReqVO.getTrafficSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getTrafficSubsidyAmt()):0;
	    int incomeSubsidyAmt = ownerCostReqVO.getIncomeSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getIncomeSubsidyAmt()):0;
	    int otherSubsidyAmt = ownerCostReqVO.getOtherSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getOtherSubsidyAmt()):0;
		//封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
		SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.PLATFORM; //固定
    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.OWNER;
    	//获取的管理后台的用户名
    	String userName = ownerCostReqVO.getOperatorName();  
		//车主超里程补贴
		if(mileageAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, mileageAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_MILEAGE_COST_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主油费补贴
		if(oilSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, oilSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_OIL_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主物品损失补贴
		if(carGoodsLossSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, carGoodsLossSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_GOODS_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主延时补贴
		if(delaySubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, delaySubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_DELAY_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主交通费补贴
		if(trafficSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, trafficSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_TRAFFIC_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主收益补贴
		if(incomeSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, incomeSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_INCOME_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主洗车补贴
		if(washCarSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, washCarSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_WASH_CAR_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//其他补贴
		if(otherSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, otherSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_OTHER_SUBSIDY);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
	}
}
