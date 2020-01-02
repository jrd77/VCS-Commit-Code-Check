package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyCompareDTO;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.atzuche.order.renterorder.service.RenterOrderService;

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
	private ModifyOrderForOwnerService modifyOrderForOwnerService;
	
	public Integer INVALID_FLAG = 0;
	
	public Integer EFFECTIVE_FLAG = 1;
	
	public Integer AUDIT_STATUS_WAIT = 0;
	
	public Integer AUDIT_STATUS_AGREE = 1;
	
	public Integer AUDIT_STATUS_REFUSE = 2;
	
	/**
	 * 车主同意后修改租客子单状态
	 * @param orderNo 主订单号
	 * @param renterOrderNo 租客修改申请表中已同意的租客子订单号
	 * @param renterOrderEntity 当前有效的租客子单
	 */
	public void updateRenterOrderStatus(String orderNo, String renterOrderNo, RenterOrderEntity initRenterOrderEntity) {
		log.info("车主同意后修改租客子单状态orderNo=[{}],renterOrderNo=[{}],renterOrderEntity=[{}]",orderNo, renterOrderNo, initRenterOrderEntity);
		// 获取当前有效的子订单
		if (initRenterOrderEntity == null) {
			initRenterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		}
		// 获取租客修改申请表中已同意的租客子订单
		RenterOrderEntity agreeRenterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
		// 修改租客上笔子单状态失效
		renterOrderService.updateRenterOrderEffective(initRenterOrderEntity.getId(), INVALID_FLAG);
		// 修改租客待确认的子单状态为有效
		renterOrderService.updateRenterOrderEffective(agreeRenterOrderEntity.getId(), EFFECTIVE_FLAG);
		// 修改订单子状态:1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束 
		renterOrderService.updateRenterOrderChildStatus(agreeRenterOrderEntity.getId(), 3);
		// 获取申请记录
		RenterOrderChangeApplyEntity renterOrderChangeApplyEntity = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(renterOrderNo);
		if (renterOrderChangeApplyEntity != null) {
			// 修改租客申请状态为已同意
			renterOrderChangeApplyService.updateRenterOrderChangeApplyStatus(renterOrderChangeApplyEntity.getId(), AUDIT_STATUS_AGREE);
		}
		
		// TODO 生成车主子订单
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
			renterApply.setRentAfterTime(after.getRentTime().toString()+"-"+after.getRevertTime().toString());
		}
		if (before.getRentTime() != null && before.getRevertTime() != null) {
			renterApply.setRentBeforeTime(before.getRentTime().toString()+"-"+before.getRevertTime().toString());
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
	public void supplementPayPostProcess(String orderNo, String renterOrderNo) {
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
			updateRenterOrderStatus(orderNo, renterOrderNo, initRenterOrderEntity);
		} else {
			// 保存修改申请记录
			addRenterOrderChangeApply(orderNo, renterOrderNo, before, after);
			// 修改订单子状态:1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束 
			renterOrderService.updateRenterOrderChildStatus(updRenterOrderEntity.getId(), 2);
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
}
