package com.atzuche.order.coreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.OrderChangeItemEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.OrderDeliveryDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.atzuche.order.renterorder.service.OrderChangeItemService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderConfirmService {

	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private ModifyOrderForOwnerService modifyOrderForOwnerService;
	@Autowired
	private ModifyOrderForRenterService modifyOrderForRenterService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private OrderChangeItemService orderChangeItemService;
	@Autowired
	private DeliveryCarService deliveryCarService;
	
	/**
	 * 自动同意
	 * @param modifyOrderDTO
	 * @param renterOrder
	 * @param initRenterOrder
	 * @param renterSubsidyList
	 */
	public void agreeModifyOrder(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrder, RenterOrderEntity initRenterOrder, List<RenterOrderSubsidyDetailDTO> renterSubsidyList) {
		log.info("modifyOrderConfirmService agreeModifyOrder modifyOrderDTO=[{}], renterOrder=[{}],initRenterOrder=[{}],renterSubsidyList=[{}]",modifyOrderDTO, renterOrder,initRenterOrder,renterSubsidyList);
		if (modifyOrderDTO == null) {
			log.error("自动同意 agreeModifyOrder modifyOrderDTO为空");
			Cat.logError("自动同意 agreeModifyOrder modifyOrderDTO为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		if (renterOrder == null) {
			log.error("自动同意agreeModifyOrder 修改后租客子单 renterOrder为空");
			Cat.logError("自动同意 agreeModifyOrder 修改后租客子单 renterOrder为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		if (initRenterOrder == null) {
			log.error("自动同意agreeModifyOrder 修改前租客子单 renterOrder为空");
			Cat.logError("自动同意 agreeModifyOrder 修改前租客子单 renterOrder为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		// 封装车主同意需要的对象
		ModifyOrderOwnerDTO modifyOrderOwnerDTO = new ModifyOrderOwnerDTO();
		BeanUtils.copyProperties(modifyOrderDTO, modifyOrderOwnerDTO);
		// 获取租客车主券补贴
		RenterOrderSubsidyDetailEntity renterSubsidy = null;
		if (renterSubsidyList != null && !renterSubsidyList.isEmpty()) {
			for (RenterOrderSubsidyDetailDTO subsidy:renterSubsidyList) {
				if (RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(subsidy.getSubsidyCostCode())) {
					renterSubsidy = new RenterOrderSubsidyDetailEntity();
					BeanUtils.copyProperties(subsidy, renterSubsidy);
					break;
				}
			}
		}
		// 重新生成车主订单
		modifyOrderForOwnerService.modifyOrderForOwner(modifyOrderOwnerDTO, renterSubsidy);
		// 处理租客订单信息
		modifyOrderForRenterService.updateRenterOrderStatus(renterOrder.getOrderNo(), renterOrder.getRenterOrderNo(), initRenterOrder);
		// 生成配送订单通知仁云
	}
	
	
	/**
	 * 车主同意
	 * @param orderNo
	 * @param renterOrderNo
	 * @return ResponseData<?>
	 */
	public ResponseData<?> agreeModifyOrder(String orderNo, String renterOrderNo) {
		log.info("modifyOrderConfirmService agreeModifyOrder orderNo=[{}],renterOrderNo=[{}]",orderNo,renterOrderNo);
		// 获取租客修改申请表中已同意的租客子订单
		RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
		// 获取修改项
		List<String> changeItemList = orderChangeItemService.listChangeCodeByRenterOrderNo(renterOrderNo);
		// 封装车主同意需要的对象
		ModifyOrderOwnerDTO modifyOrderOwnerDTO = modifyOrderForOwnerService.getModifyOrderOwnerDTO(renterOrder, deliveryList);
		// 获取租客车主券补贴
		RenterOrderSubsidyDetailEntity renterSubsidy = null;
		List<RenterOrderSubsidyDetailEntity> renterSubsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
		if (renterSubsidyList != null && !renterSubsidyList.isEmpty()) {
			for (RenterOrderSubsidyDetailEntity subsidy:renterSubsidyList) {
				if (RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST.getCashNo().equals(subsidy.getSubsidyCostCode())) {
					renterSubsidy = subsidy;
					break;
				}
			}
		}
		// 获取同意前有效的租客子订单
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 重新生成车主订单
		modifyOrderForOwnerService.modifyOrderForOwner(modifyOrderOwnerDTO, renterSubsidy);
		// 处理租客订单信息
		modifyOrderForRenterService.updateRenterOrderStatus(orderNo, renterOrderNo, initRenterOrder);
		// 通知仁云
		noticeRenYun(modifyOrderOwnerDTO, changeItemList);
		return ResponseData.success();
	}
	
	
	/**
	 * 通知仁云
	 * @param modify
	 * @param changeItemList
	 */
	public void noticeRenYun(ModifyOrderOwnerDTO modify, List<String> changeItemList) {
		log.info("modifyorder sent to renyun modify=[{}], changeItemList=[{}]", modify, changeItemList);
		try {
			if (modify == null) {
				return;
			}
			if (changeItemList == null || changeItemList.isEmpty()) {
				return;
			}
			// 取车服务标志
			Integer srvGetFlag = modify.getSrvGetFlag();
			// 还车服务标志
			Integer srvReturnFlag = modify.getSrvReturnFlag();
			if (srvGetFlag != null && srvGetFlag == 1) {
				if (changeItemList.contains(OrderChangeItemEnum.MODIFY_RENTTIME.getCode())
						|| changeItemList.contains(OrderChangeItemEnum.MODIFY_REVERTTIME.getCode())) {
					// 修改时间
					UpdateFlowOrderDTO getUpdFlow = getUpdateFlowOrderDTO(modify, modify.getOwnerOrderEffective(), "take", "time");
					deliveryCarService.updateRenYunFlowOrderInfo(getUpdFlow);
				}
				if (changeItemList.contains(OrderChangeItemEnum.MODIFY_GETADDR.getCode())
						|| changeItemList.contains(OrderChangeItemEnum.MODIFY_RETURNADDR.getCode())) {
					// 修改地址
					UpdateFlowOrderDTO getUpdFlow = getUpdateFlowOrderDTO(modify, modify.getOwnerOrderEffective(), "take", "addr");
					deliveryCarService.updateRenYunFlowOrderInfo(getUpdFlow);
				}
			}
			
			if (srvReturnFlag != null && srvReturnFlag == 1) {
				if (changeItemList.contains(OrderChangeItemEnum.MODIFY_RENTTIME.getCode())
						|| changeItemList.contains(OrderChangeItemEnum.MODIFY_REVERTTIME.getCode())) {
					// 修改时间
					UpdateFlowOrderDTO getUpdFlow = getUpdateFlowOrderDTO(modify, modify.getOwnerOrderEffective(), "back", "time");
					deliveryCarService.updateRenYunFlowOrderInfo(getUpdFlow);
				}
				if (changeItemList.contains(OrderChangeItemEnum.MODIFY_GETADDR.getCode())
						|| changeItemList.contains(OrderChangeItemEnum.MODIFY_RETURNADDR.getCode())) {
					// 修改地址
					UpdateFlowOrderDTO getUpdFlow = getUpdateFlowOrderDTO(modify, modify.getOwnerOrderEffective(), "back", "addr");
					deliveryCarService.updateRenYunFlowOrderInfo(getUpdFlow);
				}
			}
		} catch (Exception e) {
			log.error("modifyorder sent to renyun exception:", e);
		}
	}
	
	
	public Map<Integer,OrderDeliveryDTO> getOrderDeliveryDTO(ModifyOrderDTO modifyOrderDTO) {
		Map<Integer,OrderDeliveryDTO> delivMap = new HashMap<Integer, OrderDeliveryDTO>();
		OrderDeliveryDTO getDelivery = new OrderDeliveryDTO();
		getDelivery.setRentTime(modifyOrderDTO.getRentTime());
		getDelivery.setRevertTime(modifyOrderDTO.getRevertTime());
		getDelivery.setRenterGetReturnAddr(modifyOrderDTO.getGetCarAddress());
		getDelivery.setRenterGetReturnAddrLat(modifyOrderDTO.getGetCarLat());
		getDelivery.setRenterGetReturnAddrLon(modifyOrderDTO.getGetCarLon());
		getDelivery.setOrderNo(modifyOrderDTO.getOrderNo());
		getDelivery.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		getDelivery.setType(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
		delivMap.put(SrvGetReturnEnum.SRV_GET_TYPE.getCode(), getDelivery);
		OrderDeliveryDTO returnDelivery = new OrderDeliveryDTO();
		returnDelivery.setRentTime(modifyOrderDTO.getRentTime());
		returnDelivery.setRevertTime(modifyOrderDTO.getRevertTime());
		returnDelivery.setRenterGetReturnAddr(modifyOrderDTO.getGetCarAddress());
		returnDelivery.setRenterGetReturnAddrLat(modifyOrderDTO.getGetCarLat());
		returnDelivery.setRenterGetReturnAddrLon(modifyOrderDTO.getGetCarLon());
		returnDelivery.setOrderNo(modifyOrderDTO.getOrderNo());
		returnDelivery.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		returnDelivery.setType(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
		delivMap.put(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode(), getDelivery);
		return delivMap;
	}
	
	
	public UpdateFlowOrderDTO getUpdateFlowOrderDTO(ModifyOrderOwnerDTO modify, OwnerOrderEntity ownerOrderEffective, String serviceType, String changetype) {
		// 获取车主商品信息 yyyy-MM-dd HH:mm
		OwnerGoodsDetailDTO ownerGoods = modify.getOwnerGoodsDetailDTO();
		UpdateFlowOrderDTO updFlow = new UpdateFlowOrderDTO();
		updFlow.setOrdernumber(modify.getOrderNo());
		updFlow.setServicetype(serviceType);
		updFlow.setChangetype(changetype);
		updFlow.setNewpickupcaraddr(modify.getGetCarAddress());
		updFlow.setNewalsocaraddr(modify.getRevertCarAddress());
		updFlow.setNewtermtime(modify.getRentTime().toString());
		updFlow.setNewreturntime(modify.getRevertTime().toString());
		updFlow.setDefaultpickupcaraddr(ownerGoods.getCarRealAddr());
		updFlow.setNewbeforeTime(ownerOrderEffective.getShowRentTime().toString());
		updFlow.setNewafterTime(ownerOrderEffective.getShowRevertTime().toString());
		updFlow.setRealGetCarLon(modify.getGetCarLon());
		updFlow.setRealGetCarLat(modify.getGetCarLat());
		updFlow.setRealReturnCarLon(modify.getRevertCarLon());
		updFlow.setRealReturnCarLat(modify.getRevertCarLat());
		updFlow.setCarLon(ownerGoods.getCarRealLon());
		updFlow.setCarLat(ownerGoods.getCarRealLat());
		return updFlow;
	}
}
