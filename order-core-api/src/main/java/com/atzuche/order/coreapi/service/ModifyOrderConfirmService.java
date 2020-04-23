package com.atzuche.order.coreapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;
import com.atzuche.order.coreapi.service.remote.StockProxyService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OrderTransferRecordDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.DispatcherStatusEnum;
import com.atzuche.order.commons.enums.OrderChangeItemEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.OrderTransferSourceEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.entity.dto.ModifyConfirmDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderGoodNotExistException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.order.delivery.vo.delivery.CancelOrderDeliveryVO;
import com.atzuche.order.delivery.vo.delivery.UpdateFlowOrderDTO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.OrderTransferRecordEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.atzuche.order.renterorder.service.OrderChangeItemService;
import com.atzuche.order.renterorder.service.OrderTransferRecordService;
import com.atzuche.order.renterorder.service.RenterOrderChangeApplyService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.enums.OrderOperationTypeEnum;
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
	private StockProxyService stockService;
	@Autowired
	private RenterOrderChangeApplyService renterOrderChangeApplyService;
	@Autowired
	private OrderTransferRecordService orderTransferRecordService;
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private ModifyOrderRabbitMQService modifyOrderRabbitMQService;
	@Autowired
	private OrderFlowService orderFlowService;
	@Autowired
	private OrderSourceStatService orderSourceStatService;
	
	private static final Integer ALREADY_PAY_SUCCESS = 1;
	
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
		List<RenterOrderSubsidyDetailEntity> subsidyDetailEntityList = new ArrayList<RenterOrderSubsidyDetailEntity>();
		if (renterSubsidyList != null && !renterSubsidyList.isEmpty()) {
			for (RenterOrderSubsidyDetailDTO subsidy:renterSubsidyList) {
				RenterOrderSubsidyDetailEntity renterSubsidy = new RenterOrderSubsidyDetailEntity();;
				BeanUtils.copyProperties(subsidy, renterSubsidy);
				subsidyDetailEntityList.add(renterSubsidy);
			}
		}
		// 重新生成车主订单
		modifyOrderForOwnerService.modifyOrderForOwner(modifyOrderOwnerDTO, subsidyDetailEntityList, modifyOrderDTO.getRenterOrderNo());
		// 处理租客订单信息
		modifyOrderForRenterService.updateRenterOrderStatus(renterOrder.getOrderNo(), renterOrder.getRenterOrderNo(), initRenterOrder);
		// 如果是换车增加一条换车记录
		saveOrderTransferRecord(modifyOrderOwnerDTO, modifyOrderDTO);
		// 更新历史未处理的申请记录为拒绝(管理后台修改订单逻辑)
		renterOrderChangeApplyService.updateRenterOrderChangeApplyStatusByOrderNo(modifyOrderOwnerDTO.getOrderNo());
		// 订单状态
		OrderStatusEntity orderStatus = modifyOrderDTO.getOrderStatusEntity();
		// 租车费用支付状态:0,待支付 1,已支付
		Integer rentCarPayStatus = orderStatus == null ? null:orderStatus.getRentCarPayStatus();
		modifyOrderOwnerDTO.setRentCarPayStatus(rentCarPayStatus);
		if (modifyOrderDTO.getScanCodeFlag() == null || !modifyOrderDTO.getScanCodeFlag()) {
			// 扫码还车不管
			// 封装OrderReqContext对象
			OrderReqContext reqContext = getOrderReqContext(modifyOrderDTO, modifyOrderOwnerDTO);
			// 通知仁云
			noticeRenYun(modifyOrderDTO.getRenterOrderNo(), modifyOrderOwnerDTO, listChangeCode(modifyOrderDTO.getChangeItemList()), reqContext);
			// 扣库存
			cutCarStock(modifyOrderOwnerDTO, listChangeCode(modifyOrderDTO.getChangeItemList()));
			// 发送mq
			modifyOrderRabbitMQService.sendOrderDelayMq(modifyOrderDTO);
		}
	}
	
	
	/**
	 * 保存换车记录
	 * @param modifyOrderOwnerDTO
	 * @param modifyOrderDTO
	 */
	public void saveOrderTransferRecord(ModifyOrderOwnerDTO modifyOrderOwnerDTO, ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderOwnerDTO == null || modifyOrderDTO == null) {
			return;
		}
		if (modifyOrderDTO.getTransferFlag() == null || !modifyOrderDTO.getTransferFlag()) {
			return;
		}
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = modifyOrderOwnerDTO.getOwnerGoodsDetailDTO();
		if (ownerGoodsDetailDTO == null) {
			return;
		}
		OrderTransferRecordEntity orderTransferRecordEntity = new OrderTransferRecordEntity();
		orderTransferRecordEntity.setCarNo(ownerGoodsDetailDTO.getCarNo() == null ? null:String.valueOf(ownerGoodsDetailDTO.getCarNo()));
		orderTransferRecordEntity.setCarPlateNum(ownerGoodsDetailDTO.getCarPlateNum());
		orderTransferRecordEntity.setOperator(modifyOrderDTO.getOperator());
		orderTransferRecordEntity.setMemNo(modifyOrderDTO.getMemNo());
		orderTransferRecordEntity.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
		orderTransferRecordEntity.setSource(0);
		// 保存换车记录
		orderTransferRecordService.saveOrderTransferRecord(orderTransferRecordEntity);
		// 更新调度状态
		orderStatusService.updateDispatchStatus(modifyOrderOwnerDTO.getOrderNo(), DispatcherStatusEnum.DISPATCH_SUCCESS.getCode());
		// 查询订单状态
		OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(modifyOrderOwnerDTO.getOrderNo());
		// 订单状态
		Integer status = orderStatus.getStatus();
		if (status != null && status.intValue() == OrderStatusEnum.TO_DISPATCH.getStatus()) {
			// 租车费用支付状态:0,待支付 1,已支付
			Integer rentCarPayStatus = orderStatus.getRentCarPayStatus();
			// 车辆押金支付状态:0,待支付 1,已支付
			Integer depositPayStatus = orderStatus.getDepositPayStatus();
			// 违章押金支付状态:0,待支付 1,已支付
			Integer wzPayStatus = orderStatus.getWzPayStatus();
			Integer updOrderStatus = OrderStatusEnum.TO_GET_CAR.getStatus();
			if (!(ALREADY_PAY_SUCCESS.equals(rentCarPayStatus) && 
					ALREADY_PAY_SUCCESS.equals(depositPayStatus) && 
					ALREADY_PAY_SUCCESS.equals(wzPayStatus))) {
				updOrderStatus = OrderStatusEnum.TO_PAY.getStatus();
			}
			// 修改订单状态
			orderStatusService.updateOrderStatus(modifyOrderOwnerDTO.getOrderNo(), updOrderStatus);
			// 增加订单状态流转
			orderFlowService.inserOrderStatusChangeProcessInfo(modifyOrderOwnerDTO.getOrderNo(), OrderStatusEnum.from(updOrderStatus));
		}
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
		// 获取订单来源信息
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(orderNo);
        if (osse != null) {
        	modifyOrderOwnerDTO.setLongCouponCode(osse.getLongRentCouponCode());
        }
		// 获取租客补贴有和车主关联的
		List<RenterOrderSubsidyDetailEntity> renterSubsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
		// 获取同意前有效的租客子订单
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 重新生成车主订单
		modifyOrderForOwnerService.modifyOrderForOwner(modifyOrderOwnerDTO, renterSubsidyList, renterOrderNo);
		// 处理租客订单信息
		modifyOrderForRenterService.updateRenterOrderStatus(orderNo, renterOrderNo, initRenterOrder);
		// 通知仁云
		noticeRenYun(renterOrderNo, modifyOrderOwnerDTO, changeItemList, null);
		// 扣库存
		cutCarStock(modifyOrderOwnerDTO, changeItemList);
		// 发mq
		modifyOrderRabbitMQService.sendOrderOwnerAgreeModifyMq(modifyOrderOwnerDTO);
		modifyOrderRabbitMQService.sendOrderDelayOwnerMq(modifyOrderOwnerDTO);
	}
	
	
	
	/**
	 * 车主拒绝
	 * @param modifyConfirmDTO
	 */
	public void refuseModifyOrder(ModifyConfirmDTO modifyConfirmDTO) {
		modifyOrderForRenterService.updateRefuseOrderStatus(modifyConfirmDTO.getRenterOrderNo());
		// 发mq
		modifyOrderRabbitMQService.sendOrderOwnerRefundModifyMq(modifyConfirmDTO);
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
			if (modify.getTransferFlag() != null && modify.getTransferFlag()) {
				// 换车操作先取消上笔仁云再新增当前订单
				OwnerOrderEntity ownerOrderEffective = modify.getOwnerOrderEffective();
				Integer getMinutes = ownerOrderEffective == null ? 0:ownerOrderEffective.getBeforeMinutes();
				Integer returnMinutes = ownerOrderEffective == null ? 0:ownerOrderEffective.getAfterMinutes();
				if (srvGetFlag != null && srvGetFlag == 1) {
					deliveryCarService.updateRenYunFlowOrderCarInfo(getMinutes, returnMinutes, reqContext, SrvGetReturnEnum.SRV_GET_TYPE.getCode());
				}
				if (srvReturnFlag != null && srvReturnFlag == 1) {
					deliveryCarService.updateRenYunFlowOrderCarInfo(getMinutes, returnMinutes, reqContext, SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
				}
				
			}
			// 是否发送仁云
			boolean sendRenyunFlag = false;
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
					// 新增取车服务 （修改订单前面已经做了，此处不需要再新增配送订单）
					//deliveryCarService.addRenYunFlowOrderInfo(getMinutes, returnMinutes, reqContext, SrvGetReturnEnum.SRV_GET_TYPE.getCode());
					sendRenyunFlag = true;
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
					// 新增取车服务 （修改订单前面已经做了，此处不需要再新增配送订单）
					//deliveryCarService.addRenYunFlowOrderInfo(getMinutes, returnMinutes, reqContext, SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
					sendRenyunFlag = true;
				}
				if (srvReturnFlag != null && srvReturnFlag == 0) {
					// 取消还车服务
					deliveryCarService.cancelRenYunFlowOrderInfo(getCancelOrderDeliveryVO(modify.getOrderNo(), renterOrderNo, "back"));
				}
			}
			if (changeItemList.contains(OrderChangeItemEnum.MODIFY_SRVRETURNFLAG.getCode()) || 
					changeItemList.contains(OrderChangeItemEnum.MODIFY_SRVGETFLAG.getCode())) {
				// 租车费用支付状态:0,待支付 1,已支付
				Integer rentCarPayStatus = modify.getRentCarPayStatus();
				if (sendRenyunFlag && (rentCarPayStatus != null && rentCarPayStatus == 1)) {
					// 发送给仁云
					deliveryCarService.sendDataMessageToRenYun(renterOrderNo);
				}
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
	
	
	/**
	 * 扣库存
	 * @param modifyOrderOwnerDTO
	 * @param changeItemList
	 */
	public void cutCarStock(ModifyOrderOwnerDTO modifyOrderOwnerDTO, List<String> changeItemList) {
		if (modifyOrderOwnerDTO == null) {
			log.error("ModifyOrderConfirmService.cutCarStock扣库存  modifyOrderOwnerDTO为空");
			Cat.logError("ModifyOrderConfirmService.cutCarStock扣库存modifyOrderOwnerDTO为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		// 修改项目
		/*
		 * if (changeItemList == null || changeItemList.isEmpty()) { return; } if
		 * (!changeItemList.contains(OrderChangeItemEnum.MODIFY_RENTTIME.getCode()) &&
		 * !changeItemList.contains(OrderChangeItemEnum.MODIFY_REVERTTIME.getCode())) {
		 * // 未修改租期,不需要校验库存 return; }
		 */
		OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
		orderInfoDTO.setOrderNo(modifyOrderOwnerDTO.getOrderNo());
		orderInfoDTO.setCityCode(modifyOrderOwnerDTO.getCityCode() != null ? Integer.valueOf(modifyOrderOwnerDTO.getCityCode()):null);
		// 商品信息
		OwnerGoodsDetailDTO ownerGoodsDetailDTO = modifyOrderOwnerDTO.getOwnerGoodsDetailDTO();
		if (ownerGoodsDetailDTO == null) {
			Cat.logError("ModifyOrderConfirmService.cutCarStock扣库存", new ModifyOrderGoodNotExistException());
			throw new ModifyOrderGoodNotExistException();
		}
		orderInfoDTO.setCarNo(ownerGoodsDetailDTO.getCarNo());
		orderInfoDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(modifyOrderOwnerDTO.getRentTime()));
		orderInfoDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(modifyOrderOwnerDTO.getRevertTime()));
		LocationDTO getCarAddress = new LocationDTO();
		getCarAddress.setCarAddress(modifyOrderOwnerDTO.getGetCarAddress());
		getCarAddress.setFlag(modifyOrderOwnerDTO.getSrvGetFlag());
		getCarAddress.setLat(modifyOrderOwnerDTO.getGetCarLat() != null ? Double.valueOf(modifyOrderOwnerDTO.getGetCarLat()):null);
		getCarAddress.setLon(modifyOrderOwnerDTO.getGetCarLon() != null ? Double.valueOf(modifyOrderOwnerDTO.getGetCarLon()):null);
		orderInfoDTO.setGetCarAddress(getCarAddress);
		LocationDTO returnCarAddress = new LocationDTO();
		returnCarAddress.setCarAddress(modifyOrderOwnerDTO.getRevertCarAddress());
		returnCarAddress.setFlag(modifyOrderOwnerDTO.getSrvReturnFlag());
		returnCarAddress.setLat(modifyOrderOwnerDTO.getRevertCarLat() != null ? Double.valueOf(modifyOrderOwnerDTO.getRevertCarLat()):null);
		returnCarAddress.setLon(modifyOrderOwnerDTO.getRevertCarLon() != null ? Double.valueOf(modifyOrderOwnerDTO.getRevertCarLon()):null);
		orderInfoDTO.setReturnCarAddress(returnCarAddress);
		orderInfoDTO.setOperationType(OrderOperationTypeEnum.DDXGZQ.getType());
		if (modifyOrderOwnerDTO.getTransferFlag() != null && modifyOrderOwnerDTO.getTransferFlag() && 
				modifyOrderOwnerDTO.getOldCarNo() != null) {
			// 换车要释放上一辆车的库存
			orderInfoDTO.setOldCarNo(modifyOrderOwnerDTO.getOldCarNo());
			orderInfoDTO.setOperationType(OrderOperationTypeEnum.DDHC.getType());
		}
		orderInfoDTO.setLongRent(0);
		if (StringUtils.isNotBlank(modifyOrderOwnerDTO.getLongCouponCode())) {
			// 长租订单
			orderInfoDTO.setLongRent(1);
		}
		stockService.cutCarStock(orderInfoDTO);
	}
	
	/**
	 * 获取换车记录
	 * @param orderNo
	 * @return List<OrderTransferRecordDTO>
	 */
	public List<OrderTransferRecordDTO> listOrderTransferRecordByOrderNo(String orderNo) {
		List<OrderTransferRecordEntity> list = orderTransferRecordService.listOrderTransferRecordByOrderNo(orderNo);
		if (list != null && !list.isEmpty()) {
			List<OrderTransferRecordDTO> listDTO = list.stream().map(transfer -> {
				OrderTransferRecordDTO transDTO = new OrderTransferRecordDTO();
				BeanUtils.copyProperties(transfer, transDTO);
				if (OrderTransferSourceEnum.DISPATCH_TRANSFER.getCode().equals(transfer.getSource())) {
					transDTO.setSourceDesc(OrderTransferSourceEnum.DISPATCH_TRANSFER.getDesc());
				} else if (OrderTransferSourceEnum.NORMAL_TRANSFER.getCode().equals(transfer.getSource())) {
					transDTO.setSourceDesc(OrderTransferSourceEnum.NORMAL_TRANSFER.getDesc());
				} else if (OrderTransferSourceEnum.CPIC_TRANSFER.getCode().equals(transfer.getSource())) {
					transDTO.setSourceDesc(OrderTransferSourceEnum.CPIC_TRANSFER.getDesc());
				} else if (OrderTransferSourceEnum.CREATE_ORDER.getCode().equals(transfer.getSource())) {
					transDTO.setSourceDesc(OrderTransferSourceEnum.CREATE_ORDER.getDesc());
				}
				transDTO.setCreateTime(CommonUtils.formatTime(transfer.getCreateTime(), CommonUtils.FORMAT_STR_DEFAULT));
				return transDTO;
			}).collect(Collectors.toList());
			return listDTO;
		}
		return null;
	}
}
