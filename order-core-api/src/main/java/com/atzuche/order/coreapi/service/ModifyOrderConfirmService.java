package com.atzuche.order.coreapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.OrderChangeItemEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.entity.dto.ModifyConfirmDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.entity.request.ModifyApplyHandleReq;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderChangeApplyNotFindException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.atzuche.order.renterorder.service.OrderChangeItemService;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.platformcost.CommonUtils;
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
	@Autowired
	private RenterOrderChangeApplyService renterOrderChangeApplyService;
	
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
		// 封装OrderReqContext对象
		OrderReqContext reqContext = getOrderReqContext(modifyOrderDTO, modifyOrderOwnerDTO);
		// 通知仁云
		noticeRenYun(modifyOrderDTO.getRenterOrderNo(), modifyOrderOwnerDTO, listChangeCode(modifyOrderDTO.getChangeItemList()), reqContext);
	}
	
	/**
	 * 封装新增仁云订单需要的参数
	 * @param modifyOrderDTO
	 * @param modifyOrderOwnerDTO
	 * @return OrderReqContext
	 */
	public OrderReqContext getOrderReqContext(ModifyOrderDTO modifyOrderDTO, ModifyOrderOwnerDTO modifyOrderOwnerDTO) {
		OrderReqContext reqContext = new OrderReqContext();
		reqContext.setOwnerGoodsDetailDto(modifyOrderOwnerDTO.getOwnerGoodsDetailDTO());
		reqContext.setOwnerMemberDto(modifyOrderOwnerDTO.getOwnerMemberDTO());
		reqContext.setRenterGoodsDetailDto(modifyOrderDTO.getRenterGoodsDetailDTO());
		reqContext.setRenterMemberDto(modifyOrderDTO.getRenterMemberDTO());
		OrderEntity orderEntity = modifyOrderDTO.getOrderEntity();
		OrderReqVO req = new OrderReqVO();
		req.setSrvGetFlag(modifyOrderDTO.getSrvGetFlag());
		req.setSrvReturnFlag(modifyOrderDTO.getSrvReturnFlag());
		req.setSrvGetAddr(modifyOrderDTO.getGetCarAddress());
		req.setSrvGetLat(modifyOrderDTO.getGetCarLat());
		req.setSrvGetLon(modifyOrderDTO.getGetCarLon());
		req.setSrvReturnAddr(modifyOrderDTO.getRevertCarAddress());
		req.setSrvReturnLat(modifyOrderDTO.getRevertCarLat());
		req.setSrvReturnLon(modifyOrderDTO.getRevertCarLon());
		if (orderEntity != null) {
			req.setCityCode(orderEntity.getCityCode());
			req.setCityName(orderEntity.getCityName());
			req.setOrderCategory(orderEntity.getCategory()+"");
			req.setSceneCode(orderEntity.getEntryCode());
			req.setSource(orderEntity.getSource()+"");
		}
		reqContext.setOrderReqVO(req);
		return reqContext;
	}
	
	
	/**
	 * 车主操作修改申请
	 * @param modifyApplyHandleReq
	 * @return ResponseData
	 */
	public ResponseData<?> modifyConfirm(ModifyApplyHandleReq modifyApplyHandleReq) {
		log.info("ModifyOrderConfirmService.modifyConfirm modifyApplyHandleReq=[{}]", modifyApplyHandleReq);
		if (modifyApplyHandleReq == null) {
			log.error("ModifyOrderConfirmService.modifyConfirm车主处理修改申请报错参数为空");
			Cat.logError("ModifyOrderConfirmService.modifyConfirm车主处理修改申请报错", new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		ModifyConfirmDTO modifyConfirmDTO = convertToModifyConfirmDTO(modifyApplyHandleReq);
		RenterOrderChangeApplyEntity changeApply = renterOrderChangeApplyService.getRenterOrderChangeApplyByRenterOrderNo(modifyConfirmDTO.getRenterOrderNo());
		if (changeApply == null) {
			log.error("ModifyOrderConfirmService.modifyConfirm未找到有效的修改申请记录modifyApplyHandleReq=[{}]", modifyApplyHandleReq);
			Cat.logError("ModifyOrderConfirmService.modifyConfirm车主处理修改申请报错", new ModifyOrderChangeApplyNotFindException());
			throw new ModifyOrderChangeApplyNotFindException();
		}
		modifyConfirmDTO.setOrderNo(changeApply.getOrderNo());
		modifyConfirmDTO.setOwnerOrderNo(changeApply.getOwnerOrderNo());
		if (String.valueOf(ModifyOrderForRenterService.AUDIT_STATUS_AGREE).equals(modifyConfirmDTO.getFlag())) {
			// 同意
			agreeModifyOrder(changeApply.getOrderNo(), modifyConfirmDTO.getRenterOrderNo());
		} else if (String.valueOf(ModifyOrderForRenterService.AUDIT_STATUS_REFUSE).equals(modifyConfirmDTO.getFlag())) {
			// 拒绝
			refuseModifyOrder(modifyConfirmDTO);
		}
		return ResponseData.success();
	}
	
	
	/**
	 * 车主同意
	 * @param orderNo
	 * @param renterOrderNo
	 * @return ResponseData<?>
	 */
	public void agreeModifyOrder(String orderNo, String renterOrderNo) {
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
		noticeRenYun(renterOrderNo, modifyOrderOwnerDTO, changeItemList, null);
	}
	
	
	/**
	 * 车主拒绝
	 * @param modifyConfirmDTO
	 */
	public void refuseModifyOrder(ModifyConfirmDTO modifyConfirmDTO) {
		modifyOrderForRenterService.updateRefuseOrderStatus(modifyConfirmDTO.getRenterOrderNo());
	}
	
	/**
	 * 转换List<String>
	 * @param changeItemList
	 * @return List<String>
	 */
	public List<String> listChangeCode(List<OrderChangeItemDTO> changeItemList) {
		if (changeItemList == null || changeItemList.isEmpty()) {
			return null;
		}
		return changeItemList.stream().map(chit -> {return chit.getChangeCode();}).collect(Collectors.toList());
	}
	
	
	/**
	 * 通知仁云
	 * @param modify
	 * @param changeItemList
	 */
	public void noticeRenYun(String renterOrderNo, ModifyOrderOwnerDTO modify, List<String> changeItemList, OrderReqContext reqContext) {
		log.info("modifyorder sent to renyun renterOrderNo=[{}], modify=[{}], changeItemList=[{}]",renterOrderNo, modify, changeItemList);
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
			if (changeItemList.contains(OrderChangeItemEnum.MODIFY_SRVGETFLAG.getCode())) {
				// 修改过取车服务标记
				if (srvGetFlag != null && srvGetFlag == 1) {
					OwnerOrderEntity ownerOrderEffective = modify.getOwnerOrderEffective();
					Integer getMinutes = ownerOrderEffective == null ? 0:ownerOrderEffective.getBeforeMinutes();
					Integer returnMinutes = ownerOrderEffective == null ? 0:ownerOrderEffective.getAfterMinutes();
					if (reqContext != null && reqContext.getOrderReqVO() != null) {
						reqContext.getOrderReqVO().setSrvGetFlag(1);
						reqContext.getOrderReqVO().setSrvReturnFlag(0);
					}
					// 新增取车服务
					deliveryCarService.addRenYunFlowOrderInfo(getMinutes, returnMinutes, reqContext, SrvGetReturnEnum.SRV_GET_TYPE.getCode());
				}
				if (srvGetFlag != null && srvGetFlag == 0) {
					// 取消取车服务
					deliveryCarService.cancelRenYunFlowOrderInfo(getCancelOrderDeliveryVO(modify.getOrderNo(), renterOrderNo, "take"));
				}
			}
			if (changeItemList.contains(OrderChangeItemEnum.MODIFY_SRVRETURNFLAG.getCode())) {
				// 修改过还车服务标记
				if (srvReturnFlag != null && srvReturnFlag == 1) {
					// 新增还车服务
					OwnerOrderEntity ownerOrderEffective = modify.getOwnerOrderEffective();
					Integer getMinutes = ownerOrderEffective == null ? 0:ownerOrderEffective.getBeforeMinutes();
					Integer returnMinutes = ownerOrderEffective == null ? 0:ownerOrderEffective.getAfterMinutes();
					if (reqContext != null && reqContext.getOrderReqVO() != null) {
						reqContext.getOrderReqVO().setSrvGetFlag(0);
						reqContext.getOrderReqVO().setSrvReturnFlag(1);
					}
					// 新增取车服务
					deliveryCarService.addRenYunFlowOrderInfo(getMinutes, returnMinutes, reqContext, SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
				}
				if (srvReturnFlag != null && srvReturnFlag == 0) {
					// 取消还车服务
					deliveryCarService.cancelRenYunFlowOrderInfo(getCancelOrderDeliveryVO(modify.getOrderNo(), renterOrderNo, "back"));
				}
			}
			if (changeItemList.contains(OrderChangeItemEnum.MODIFY_SRVRETURNFLAG.getCode()) || 
					changeItemList.contains(OrderChangeItemEnum.MODIFY_SRVGETFLAG.getCode())) {
				return;
			}
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
	
	/**
	 * 对象转换成仁云需要的参数（修改）
	 * @param modify
	 * @param ownerOrderEffective
	 * @param serviceType
	 * @param changetype
	 * @return UpdateFlowOrderDTO
	 */
	public UpdateFlowOrderDTO getUpdateFlowOrderDTO(ModifyOrderOwnerDTO modify, OwnerOrderEntity ownerOrderEffective, String serviceType, String changetype) {
		// 获取车主商品信息 
		OwnerGoodsDetailDTO ownerGoods = modify.getOwnerGoodsDetailDTO();
		UpdateFlowOrderDTO updFlow = new UpdateFlowOrderDTO();
		updFlow.setOrdernumber(modify.getOrderNo());
		updFlow.setServicetype(serviceType);
		updFlow.setChangetype(changetype);
		updFlow.setNewpickupcaraddr(modify.getGetCarAddress());
		updFlow.setNewalsocaraddr(modify.getRevertCarAddress());
		updFlow.setNewtermtime(CommonUtils.formatTime(modify.getRentTime(), CommonUtils.FORMAT_STR_RENYUN));
		updFlow.setNewreturntime(CommonUtils.formatTime(modify.getRevertTime(), CommonUtils.FORMAT_STR_RENYUN));
		updFlow.setDefaultpickupcaraddr(ownerGoods.getCarRealAddr());
		updFlow.setNewbeforeTime(CommonUtils.formatTime(ownerOrderEffective.getShowRentTime(), CommonUtils.FORMAT_STR_RENYUN));
		updFlow.setNewafterTime(CommonUtils.formatTime(ownerOrderEffective.getShowRevertTime(), CommonUtils.FORMAT_STR_RENYUN));
		updFlow.setRealGetCarLon(modify.getGetCarLon());
		updFlow.setRealGetCarLat(modify.getGetCarLat());
		updFlow.setRealReturnCarLon(modify.getRevertCarLon());
		updFlow.setRealReturnCarLat(modify.getRevertCarLat());
		updFlow.setCarLon(ownerGoods.getCarRealLon());
		updFlow.setCarLat(ownerGoods.getCarRealLat());
		return updFlow;
	}
	
	/**
	 * 封装取消仁云服务对象（取消）
	 * @param orderNo
	 * @param renterOrderNo
	 * @param servicetype
	 * @return CancelOrderDeliveryVO
	 */
	public CancelOrderDeliveryVO getCancelOrderDeliveryVO(String orderNo,String renterOrderNo, String servicetype) {
		CancelOrderDeliveryVO cancelOrderDeliveryVO = new CancelOrderDeliveryVO();
		cancelOrderDeliveryVO.setRenterOrderNo(renterOrderNo);
		cancelOrderDeliveryVO.setCancelFlowOrderDTO(getCancelFlowOrderDTO(orderNo, servicetype));
		return cancelOrderDeliveryVO;
	}
	
	/**
	 * 封装取消仁云服务对象（取消）
	 * @param orderNo
	 * @param servicetype
	 * @return CancelFlowOrderDTO
	 */
	public CancelFlowOrderDTO getCancelFlowOrderDTO(String orderNo,String servicetype) {
		CancelFlowOrderDTO cancelFlowOrderDTO = new CancelFlowOrderDTO();
		cancelFlowOrderDTO.setOrdernumber(orderNo);
		cancelFlowOrderDTO.setServicetype(servicetype);
		return cancelFlowOrderDTO;
	}
	
	
	/**
	 * Req转DTO
	 * @param modifyApplyHandleReq
	 * @return ModifyConfirmDTO
	 */
	public ModifyConfirmDTO convertToModifyConfirmDTO(ModifyApplyHandleReq modifyApplyHandleReq) {
		ModifyConfirmDTO modifyConfirmDTO = new ModifyConfirmDTO();
		modifyConfirmDTO.setFlag(modifyApplyHandleReq.getFlag());
		modifyConfirmDTO.setOwnerMemNo(modifyApplyHandleReq.getMemNo());
		modifyConfirmDTO.setRenterOrderNo(modifyApplyHandleReq.getModifyApplicationId());
		return modifyConfirmDTO;
	}
}
