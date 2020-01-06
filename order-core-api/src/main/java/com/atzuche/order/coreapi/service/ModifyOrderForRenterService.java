package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.RenterChildStatusEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyCompareDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderRenterOrderNotFindException;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.platformcost.CommonUtils;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderForRenterService {

	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterOrderChangeApplyService renterOrderChangeApplyService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private ModifyOrderConfirmService modifyOrderConfirmService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	@Autowired
	private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
	@Autowired
	private OrderService orderService;
	/**
	 * 无效
	 */
	public static final Integer INVALID_FLAG = 0;
	/**
	 * 有效
	 */
	public static final Integer EFFECTIVE_FLAG = 1;
	/**
	 * 未处理
	 */
	public static final Integer AUDIT_STATUS_WAIT = 0;
	/**
	 * 已同意
	 */
	public static final Integer AUDIT_STATUS_AGREE = 1;
	/**
	 * 已拒绝
	 */
	public static final Integer AUDIT_STATUS_REFUSE = 2;
	
	/**
	 * 车主同意后修改租客子单状态
	 * @param orderNo 主订单号
	 * @param renterOrderNo 租客修改申请表中已同意的租客子订单号
	 * @param renterOrderEntity 当前有效的租客子单
	 */
	public void updateRenterOrderStatus(String orderNo, String renterOrderNo, RenterOrderEntity initRenterOrderEntity) {
		log.info("车主同意后修改租客子单状态orderNo=[{}],renterOrderNo=[{}],renterOrderEntity=[{}]",orderNo, renterOrderNo, initRenterOrderEntity);
		// 获取租客修改申请表中已同意的租客子订单
		RenterOrderEntity agreeRenterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
		if (agreeRenterOrderEntity == null) {
			log.error("ModifyOrderForRenterService.updateRenterOrderStatus agreeRenterOrderEntity is null orderNo=[{}],renterOrderNo=[{}]",orderNo, renterOrderNo);
			Cat.logError("ModifyOrderForRenterService.updateRenterOrderStatus", new ModifyOrderRenterOrderNotFindException());
			throw new ModifyOrderRenterOrderNotFindException();
		}
		// 修改租客上笔子单状态失效
		renterOrderService.updateRenterOrderEffective(initRenterOrderEntity.getId(), INVALID_FLAG);
		// 修改租客待确认的子单状态为有效
		renterOrderService.updateRenterOrderEffective(agreeRenterOrderEntity.getId(), EFFECTIVE_FLAG);
		// 获取申请记录
		RenterOrderChangeApplyEntity renterOrderChangeApplyEntity = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(renterOrderNo);
		if (renterOrderChangeApplyEntity != null) {
			// 修改租客申请状态为已同意
			renterOrderChangeApplyService.updateRenterOrderChangeApplyStatus(renterOrderChangeApplyEntity.getId(), AUDIT_STATUS_AGREE);
		}
		// 修改主订单取还车时间
		orderService.saveOrderInfo(getOrderDTO(agreeRenterOrderEntity));
		// 对取还车违约金进行处理
		transferRenterFine(orderNo, renterOrderNo);
	}
	
	/**
	 * 同意后对租客取还车违约金进行处理
	 * @param orderNo
	 * @param renterOrderNo
	 */
	public void transferRenterFine(String orderNo, String renterOrderNo) {
		// 获取违约金列表
		List<RenterOrderFineDeatailEntity> fineList = renterOrderFineDeatailService.listRenterOrderFineDeatail(orderNo, renterOrderNo);
		if (fineList == null || fineList.isEmpty()) {
			return;
		}
		for (RenterOrderFineDeatailEntity fine:fineList) {
			if (FineTypeEnum.MODIFY_GET_FINE.getFineType().equals(fine.getFineType()) || 
					FineTypeEnum.MODIFY_RETURN_FINE.getFineType().equals(fine.getFineType())) {
				// 先删
				renterOrderFineDeatailService.deleteGetReturnFineAfterAgree(fine.getId(), "转移到租客全局罚金表中");
				// 后转移到租客全局罚金表中
				ConsoleRenterOrderFineDeatailEntity consoleFine = new ConsoleRenterOrderFineDeatailEntity();
				BeanUtils.copyProperties(fine, consoleFine);
				consoleFine.setId(null);
				consoleFine.setCreateTime(null);
				consoleFine.setUpdateTime(null);
				consoleFine.setIsDelete(0);
				consoleFine.setRemark("由租客罚金表转移过来");
				consoleRenterOrderFineDeatailService.saveConsoleRenterOrderFineDeatail(consoleFine);
			}
		}
		
	}
	
	
	/**
	 * 保存修改记录
	 * @param orderNo
	 * @param renterOrderNo
	 * @param before
	 * @param after
	 */
	public void addRenterOrderChangeApply(String orderNo, String renterOrderNo, ModifyCompareDTO before, ModifyCompareDTO after) {
		if (before == null || after == null) {
			return;
		}
		RenterOrderChangeApplyEntity renterApply = new RenterOrderChangeApplyEntity();
		renterApply.setApplyTime(LocalDateTime.now());
		renterApply.setApplyType(1);
		renterApply.setAuditStatus(AUDIT_STATUS_WAIT);
		renterApply.setAuditTime(LocalDateTime.now());
		renterApply.setGetCarAfterAddr(after.getGetAddr());
		renterApply.setGetCarBeforeAddr(before.getGetAddr());
		renterApply.setGetCarBeforeAddrLat(before.getGetLat());
		renterApply.setGetCarBeforeAddrLon(before.getGetLon());
		renterApply.setGetCarBeforeAfterLat(after.getGetLat());
		renterApply.setGetCarBeforeAfterLon(after.getGetLon());
		renterApply.setOrderNo(orderNo);
		if (after.getRentTime() != null && after.getRevertTime() != null) {
			String strRentTime = CommonUtils.formatTime(after.getRentTime(), CommonUtils.FORMAT_STR_DEFAULT);
			String strRevertTime = CommonUtils.formatTime(after.getRevertTime(), CommonUtils.FORMAT_STR_DEFAULT);
			renterApply.setRentAfterTime(strRentTime+"至"+strRevertTime);
		}
		if (before.getRentTime() != null && before.getRevertTime() != null) {
			String strRentTime = CommonUtils.formatTime(before.getRentTime(), CommonUtils.FORMAT_STR_DEFAULT);
			String strRevertTime = CommonUtils.formatTime(before.getRevertTime(), CommonUtils.FORMAT_STR_DEFAULT);
			renterApply.setRentBeforeTime(strRentTime+"至"+strRevertTime);
		}
		renterApply.setRenterOrderNo(renterOrderNo);
		renterApply.setRentTimeFlag(1);
		renterApply.setReturnCarAfterAddr(after.getReturnAddr());
		renterApply.setReturnCarBeforeAddr(before.getReturnAddr());
		renterApply.setReturnCarBeforeAddrLat(before.getReturnLat());
		renterApply.setReturnCarBeforeAddrLon(before.getReturnLon());
		renterApply.setReturnCarBeforeAfterLat(after.getReturnLat());
		renterApply.setReturnCarBeforeAfterLon(after.getReturnLon());
		renterOrderChangeApplyService.saveRenterOrderChangeApply(renterApply);
	}
	
	/**
	 * 修改订单补付成功后调用
	 * @param orderNo 主订单号
	 * @param renterOrderNo 最新修改租客子订单号
	 */
	public void supplementPayPostProcess(String orderNo, String renterOrderNo, ModifyOrderDTO modifyOrderDTO, List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList) {
		// 获取当前有效的子订单
		RenterOrderEntity initRenterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> initDeliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrderEntity.getRenterOrderNo());
		ModifyCompareDTO before = getModifyCompareDTO(initRenterOrderEntity, initDeliveryList);
		
		// 获取租客修改订单
		RenterOrderEntity updRenterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> updDeliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
		ModifyCompareDTO after = getModifyCompareDTO(updRenterOrderEntity, updDeliveryList);
		// 判断是否自动同意
		boolean autoAgree = !checkAutoAgree(initRenterOrderEntity.getExpRentTime(), initRenterOrderEntity.getExpRevertTime(), updRenterOrderEntity.getExpRentTime(), updRenterOrderEntity.getExpRevertTime());
		if (autoAgree) {
			// 自动同意
			modifyOrderConfirmService.agreeModifyOrder(modifyOrderDTO, updRenterOrderEntity, initRenterOrderEntity, renterOrderSubsidyDetailDTOList);
		} else {
			// 保存修改申请记录
			addRenterOrderChangeApply(orderNo, renterOrderNo, before, after);
			// 修改订单子状态:1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束 
			renterOrderService.updateRenterOrderChildStatus(updRenterOrderEntity.getId(), RenterChildStatusEnum.PROCESS_ING.getCode());
		}
	}
	
	/**
	 * 判断非自动同意
	 * @param initRentTime
	 * @param initRevertTime
	 * @param updRentTime
	 * @param updRevertTime
	 * @return
	 */
	public boolean checkAutoAgree(LocalDateTime initRentTime, LocalDateTime initRevertTime, LocalDateTime updRentTime, LocalDateTime updRevertTime) {
		// 是否修改取车时间
		boolean rentTimeFlag = ModifyOrderUtils.getModifyRentTimeFlag(initRentTime, updRentTime);
		// 是否修改还车时间
		boolean revertTimeFlag = ModifyOrderUtils.getModifyRentTimeFlag(initRevertTime, updRevertTime);
		return rentTimeFlag || revertTimeFlag;
	}
	
	
	/**
	 * 对象转换
	 * @param renterOrder
	 * @param deliveryList
	 * @return ModifyCompareDTO
	 */
	public ModifyCompareDTO getModifyCompareDTO(RenterOrderEntity renterOrder, List<RenterOrderDeliveryEntity> deliveryList) {
		ModifyCompareDTO modifyCompareDTO = new ModifyCompareDTO();
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				modifyCompareDTO.setGetAddr(srvGetDelivery.getRenterGetReturnAddr());
				modifyCompareDTO.setGetLat(srvGetDelivery.getRenterGetReturnAddrLat());
				modifyCompareDTO.setGetLon(srvGetDelivery.getRenterGetReturnAddrLon());
			}
			if (srvReturnDelivery != null) {
				modifyCompareDTO.setReturnAddr(srvReturnDelivery.getRenterGetReturnAddr());
				modifyCompareDTO.setReturnLat(srvReturnDelivery.getRenterGetReturnAddrLat());
				modifyCompareDTO.setReturnLon(srvReturnDelivery.getRenterGetReturnAddrLon());
			}
		}
		if (renterOrder != null) {
			modifyCompareDTO.setRentTime(renterOrder.getExpRentTime());
			modifyCompareDTO.setRevertTime(renterOrder.getExpRevertTime());
		}
		return modifyCompareDTO;
	}
	
	/**
	 * 封装主订单信息
	 * @param agreeRenterOrderEntity
	 * @return OrderDTO
	 */
	public OrderDTO getOrderDTO(RenterOrderEntity agreeRenterOrderEntity) {
		if (agreeRenterOrderEntity == null) {
			return null;
		}
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderNo(agreeRenterOrderEntity.getOrderNo());
		orderDTO.setExpRentTime(agreeRenterOrderEntity.getExpRentTime());
		orderDTO.setExpRevertTime(agreeRenterOrderEntity.getExpRevertTime());
		return orderDTO;
	}
}
