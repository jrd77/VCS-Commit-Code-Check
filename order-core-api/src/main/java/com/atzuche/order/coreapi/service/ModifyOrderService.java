package com.atzuche.order.coreapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnAddressInfoDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.RenterOrderStatusEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.CarService.CarDetailReqVO;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.CommodityService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderCalCostService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderService {
	@Autowired
	private UniqueOrderNoService uniqueOrderNoService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private CarService carService;
	@Autowired
	private CommodityService commodityService;
	@Autowired
	private OrderCouponService orderCouponService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterOrderCalCostService renterOrderCalCostService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;

	/**
	 * 修改订单主逻辑
	 * @param modifyOrderReq
	 * @return ResponseData
	 */
	public ResponseData<?> modifyOrder(ModifyOrderReq modifyOrderReq) {
		log.info("modifyOrder修改订单主逻辑入参modifyOrderReq=[{}]", modifyOrderReq);
		// TODO 前置校验
		
		// 主单号
		String orderNo = modifyOrderReq.getOrderNo();
		// 获取租客新订单号
		String renterOrderNo = uniqueOrderNoService.getRenterOrderNo(orderNo);
		// 获取修改前有效租客子订单信息
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// DTO包装
		ModifyOrderDTO modifyOrderDTO = getModifyOrderDTO(modifyOrderReq, renterOrderNo, initRenterOrder, deliveryList);
		// TODO 获取修改前租客租客端配送列表
		
		// TODO 获取修改前租客使用的优惠券列表
		
		// 获取租客会员信息
		RenterMemberDTO renterMemberDTO = getRenterMemberDTO(initRenterOrder.getRenterOrderNo());
		// 获取租客商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = getRenterGoodsDetailDTO(modifyOrderDTO, initRenterOrder);
		// 获取组装后的新租客子单信息
		RenterOrderEntity renterOrderNew = convertToRenterOrderEntity(modifyOrderDTO, initRenterOrder);
		
		// TODO 风控校验
		
		// TODO 库存校验
		
		// 基础费用计算包含租金，手续费，基础保障费用，全面保障费用，附加驾驶人保障费用，取还车费用计算和超运能费用计算
		RenterOrderCostRespDTO renterOrderCostRespDTO = getRenterOrderCostRespDTO(modifyOrderDTO, renterMemberDTO, renterGoodsDetailDTO);
		
		// 获取修改订单违约金
		List<RenterOrderFineDeatailEntity> renterFineList = getRenterFineList(modifyOrderDTO, initRenterOrder, deliveryList, renterOrderCostRespDTO);
		
		// TODO 车主券
		
		// TODO 限时立减
		
		// TODO 取还车券
		
		// TODO 平台券
		
		// TODO 凹凸币
		
		// 修改前费用
		
		// 修改后费用
		
		// 入库
		
		
		
		return null;
	}
	
	
	/**
	 * 获取组装后的租客商品详情
	 * @param modifyOrderDTO
	 * @param renterOrderEntity
	 * @return RenterGoodsDetailDTO
	 */
	public RenterGoodsDetailDTO getRenterGoodsDetailDTO(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderEntity) {
		// 调远程获取最新租客商品详情
		RenterGoodsDetailDTO renterGoodsDetailDTO = carService.getRenterGoodsDetail(convertToCarDetailReqVO(modifyOrderDTO, renterOrderEntity));
		if (renterGoodsDetailDTO == null) {
			log.error("getRenterGoodsDetailDTO renterGoodsDetailDTO为空");
			Cat.logError("getRenterGoodsDetailDTO renterGoodsDetailDTO为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		renterGoodsDetailDTO.setOrderNo(modifyOrderDTO.getOrderNo());
		renterGoodsDetailDTO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterGoodsDetailDTO.setRentTime(modifyOrderDTO.getRentTime());
		renterGoodsDetailDTO.setRevertTime(modifyOrderDTO.getRevertTime());
		// 获取汇总后的租客商品详情
		renterGoodsDetailDTO = commodityService.setPriceAndGroup(renterGoodsDetailDTO);
		// 每天价
		List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
		if (renterGoodsPriceDetailDTOList == null || renterGoodsPriceDetailDTOList.isEmpty()) {
			log.error("getRenterGoodsDetailDTO renterGoodsPriceDetailDTOList为空");
			Cat.logError("getRenterGoodsDetailDTO renterGoodsPriceDetailDTOList为空",new ModifyOrderParameterException());
			return renterGoodsDetailDTO;
		}
		for (RenterGoodsPriceDetailDTO rp:renterGoodsPriceDetailDTOList) {
			rp.setOrderNo(modifyOrderDTO.getOrderNo());
			rp.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		}
		return renterGoodsDetailDTO;
	}
	
	/**
	 * 转DTO
	 * @param modifyOrderReq
	 * @return ModifyOrderDTO
	 */
	public ModifyOrderDTO getModifyOrderDTO(ModifyOrderReq modifyOrderReq, String renterOrderNo, RenterOrderEntity initRenterOrder, List<RenterOrderDeliveryEntity> deliveryList) {
		ModifyOrderDTO modifyOrderDTO = new ModifyOrderDTO();
		BeanUtils.copyProperties(modifyOrderReq, modifyOrderDTO);
		// 设置租客子单号
		modifyOrderDTO.setRenterOrderNo(renterOrderNo);
		// 是否使用全面保障
		Integer abatementFlag = modifyOrderReq.getAbatementFlag();
		if (abatementFlag == null) {
			modifyOrderDTO.setAbatementFlag(initRenterOrder.getIsAbatement());
		} 
		if (modifyOrderReq.getRentTime() == null) {
			modifyOrderDTO.setRentTime(initRenterOrder.getExpRentTime());
		}
		if (modifyOrderReq.getRevertTime() == null) {
			modifyOrderDTO.setRevertTime(initRenterOrder.getExpRevertTime());
		}
		if (modifyOrderReq.getDriverIds() == null || modifyOrderReq.getDriverIds().isEmpty()) {
			
		}
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (StringUtils.isBlank(modifyOrderReq.getGetCarAddress()) && srvGetDelivery != null) {
				modifyOrderDTO.setGetCarAddress(srvGetDelivery.getRenterGetReturnAddr());
				modifyOrderDTO.setGetCarLat(srvGetDelivery.getRenterGetReturnAddrLat());
				modifyOrderDTO.setGetCarLon(srvGetDelivery.getRenterGetReturnAddrLon());
			}
			if (StringUtils.isBlank(modifyOrderReq.getRevertCarAddress()) && srvReturnDelivery != null) {
				modifyOrderDTO.setRevertCarAddress(srvGetDelivery.getRenterGetReturnAddr());
				modifyOrderDTO.setRevertCarLat(srvGetDelivery.getRenterGetReturnAddrLat());
				modifyOrderDTO.setRevertCarLon(srvGetDelivery.getRenterGetReturnAddrLon());
			}
		}
		if (modifyOrderReq.getSrvGetFlag() == null) {
			modifyOrderDTO.setSrvGetFlag(initRenterOrder.getIsGetCar());
		}
		if (modifyOrderReq.getSrvReturnFlag() == null) {
			modifyOrderDTO.setSrvGetFlag(initRenterOrder.getIsReturnCar());
		}
		if (modifyOrderReq.getUserCoinFlag() == null) {
			modifyOrderDTO.setUserCoinFlag(initRenterOrder.getIsUseCoin());
		}
		// 获取修改前租客使用的优惠券列表
		List<OrderCouponEntity> orderCouponList = orderCouponService.listOrderCouponByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		String initCarOwnerCouponId = null;
		String initGetReturnCouponId = null;
		String initPlatformCouponId = null;
		if (orderCouponList != null && !orderCouponList.isEmpty()) {
			Map<Integer, String> orderCouponMap = orderCouponList.stream().collect(Collectors.toMap(OrderCouponEntity::getCouponType, OrderCouponEntity::getCouponId));
			initCarOwnerCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode()); 
			initGetReturnCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
			initPlatformCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
		}
		if (StringUtils.isBlank(modifyOrderReq.getCarOwnerCouponId())) {
			modifyOrderDTO.setCarOwnerCouponId(initCarOwnerCouponId);
		}
		if (StringUtils.isBlank(modifyOrderReq.getSrvGetReturnCouponId())) {
			modifyOrderDTO.setSrvGetReturnCouponId(initGetReturnCouponId);
		}
		if (StringUtils.isBlank(modifyOrderReq.getPlatformCouponId())) {
			modifyOrderDTO.setPlatformCouponId(initPlatformCouponId);
		}
		modifyOrderDTO.setModifyFlagDTO(ModifyOrderUtils.getModifyFlagDTO(initRenterOrder, modifyOrderReq, orderCouponList));
		return modifyOrderDTO;
	}
	
	/**
	 * 数据转化为CarDetailReqVO
	 * @param renterOrderEntity
	 * @param modifyOrderReq
	 * @return CarDetailReqVO
	 */
	public CarDetailReqVO convertToCarDetailReqVO(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderEntity) {
		// 租客子订单号
		String renterOrderNo = renterOrderEntity.getRenterOrderNo();
		// 获取租客商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = commodityService.getRenterGoodsDetail(renterOrderNo, false);
		CarDetailReqVO carDetailReqVO = new CarDetailReqVO(); 
		carDetailReqVO.setAddrIndex(renterGoodsDetailDTO.getCarAddrIndex());
		carDetailReqVO.setCarNo(String.valueOf(renterGoodsDetailDTO.getCarNo()));
		carDetailReqVO.setRentTime(modifyOrderDTO.getRentTime());
		carDetailReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		Integer useSpecialPriceFlag = renterOrderEntity.getIsUseSpecialPrice();
		Boolean useSpecialPrice = (useSpecialPriceFlag != null && useSpecialPriceFlag == 1)?true:false;
		carDetailReqVO.setUseSpecialPrice(useSpecialPrice);
		return carDetailReqVO;
	}
	
	/**
	 * 组装租客会员信息
	 * @param renterOrderNo
	 * @return RenterMemberDTO
	 */
	public RenterMemberDTO getRenterMemberDTO(String renterOrderNo) {
		// 获取租客会员信息
		RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByMemNo(renterOrderNo, true);
		renterMemberDTO.setRenterOrderNo(renterOrderNo);
		// 会员权益
		List<RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
		if (renterMemberRightDTOList == null || renterMemberRightDTOList.isEmpty()) {
			return renterMemberDTO;
		}
		for (RenterMemberRightDTO rr:renterMemberRightDTOList) {
			rr.setRenterOrderNo(renterOrderNo);
		}
		return renterMemberDTO;
	}
	
	
	/**
	 * 组装租客子单
	 * @param modifyOrderDTO
	 * @param renterOrderEntity
	 * @return RenterOrderEntity
	 */
	public RenterOrderEntity convertToRenterOrderEntity(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderEntity) {
		RenterOrderEntity renterOrderNew = new RenterOrderEntity();
		BeanUtils.copyProperties(renterOrderEntity, renterOrderNew);
		renterOrderNew.setExpRentTime(modifyOrderDTO.getRentTime());
		renterOrderNew.setExpRevertTime(modifyOrderDTO.getRevertTime());
		renterOrderNew.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterOrderNew.setId(null);
		renterOrderNew.setChildStatus(RenterOrderStatusEnum.WAIT_PAY.getCode());
		return renterOrderNew;
	}
	
	
	/**
	 * 基础费用计算
	 * @param modifyOrderDTO
	 * @param renterMemberDTO
	 * @param renterGoodsDetailDTO
	 * @return RenterOrderCostRespDTO
	 */
	public RenterOrderCostRespDTO getRenterOrderCostRespDTO(ModifyOrderDTO modifyOrderDTO, RenterMemberDTO renterMemberDTO, RenterGoodsDetailDTO renterGoodsDetailDTO) {
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderEntity(modifyOrderDTO.getOrderNo());
		RenterOrderReqVO renterOrderReqVO = convertToRenterOrderReqVO(modifyOrderDTO, renterMemberDTO, renterGoodsDetailDTO, orderEntity);
		RenterOrderCostReqDTO renterOrderCostReqDTO = renterOrderService.buildRenterOrderCostReqDTO(renterOrderReqVO);
		// 获取计算好的费用信息
		return renterOrderCalCostService.getOrderCostAndDeailList(renterOrderCostReqDTO);
	}
	
	
	/**
	 * 获取修改订单违约金
	 * @param modifyOrderDTO
	 * @param initRenterOrder
	 * @param deliveryList
	 * @param renterOrderCostRespDTO
	 * @return List<RenterOrderFineDeatailEntity>
	 */
	public List<RenterOrderFineDeatailEntity> getRenterFineList(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity initRenterOrder, List<RenterOrderDeliveryEntity> deliveryList, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		// 获取取还车违约金
		List<RenterOrderFineDeatailEntity> renterFineList = getRenterGetReturnFineList(modifyOrderDTO, initRenterOrder, deliveryList);
		// 获取提前还车违约金
		RenterOrderFineDeatailEntity renterFine = getRenterFineEntity(modifyOrderDTO, renterOrderCostRespDTO, initRenterOrder);
		if (renterFineList == null) {
			renterFineList = new ArrayList<RenterOrderFineDeatailEntity>();
		}
		if (renterFine != null) {
			renterFineList.add(renterFine);
		}
		return renterFineList;
	}
	
	
	/**
	 * 获取取还车违约金
	 * @param modifyOrderDTO
	 * @param initRenterOrder
	 * @param deliveryList
	 * @return List<RenterOrderFineDeatailEntity>
	 */
	public List<RenterOrderFineDeatailEntity> getRenterGetReturnFineList(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity initRenterOrder, List<RenterOrderDeliveryEntity> deliveryList) {
		// 封装修改前对象
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		String getLon = null,getLat = null,returnLon = null,returnLat = null;
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				getLon = srvGetDelivery.getRenterGetReturnAddrLon();
				getLat = srvGetDelivery.getRenterGetReturnAddrLat();
			}
			if (srvReturnDelivery != null) {
				returnLon = srvGetDelivery.getRenterGetReturnAddrLon();
				returnLat = srvGetDelivery.getRenterGetReturnAddrLat();
			}
		}
		GetReturnAddressInfoDTO initInfo = new GetReturnAddressInfoDTO(initRenterOrder.getIsGetCar(), initRenterOrder.getIsReturnCar(), getLon, getLat, returnLon, returnLat);
		CostBaseDTO initBase = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), initRenterOrder.getExpRentTime(), initRenterOrder.getExpRevertTime());
		initInfo.setCostBaseDTO(initBase);
		// 封装修改后对象
		GetReturnAddressInfoDTO updateInfo = new GetReturnAddressInfoDTO(modifyOrderDTO.getGetCarLon(), modifyOrderDTO.getGetCarLat(), modifyOrderDTO.getRevertCarLon(), modifyOrderDTO.getRevertCarLat());
		CostBaseDTO updBase = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		updateInfo.setCostBaseDTO(updBase);
		// 取还车违约金
		List<RenterOrderFineDeatailEntity> renterFineList = renterOrderFineDeatailService.calculateGetOrReturnFineAmt(initInfo, updateInfo);
		return renterFineList;
	}
	
	/**
	 * 计算提前还车违约金
	 * @param modifyOrderDTO
	 * @param renterOrderCostRespDTO
	 * @param initRenterOrder
	 * @return RenterOrderFineDeatailEntity
	 */
	public RenterOrderFineDeatailEntity getRenterFineEntity(ModifyOrderDTO modifyOrderDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderEntity initRenterOrder) {
		// 获取修改前租客费用明细
		List<RenterOrderCostDetailEntity> initCostList = renterOrderCostDetailService.listRenterOrderCostDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 修改前租金
		Integer initAmt = initCostList.stream().filter(cost -> {return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
		// 修改后租金
		Integer updAmt = renterOrderCostRespDTO.getRentAmount();
		// 计算提前还车违约金
		CostBaseDTO updBase = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		RenterOrderFineDeatailEntity renterFine = renterOrderFineDeatailService.calcFineAmt(updBase, initAmt, updAmt, initRenterOrder.getExpRevertTime());
		return renterFine;
	}
	
	
	/**
	 * 对象转化RenterOrderReqVO
	 * @param modifyOrderDTO
	 * @param renterMemberDTO
	 * @param renterGoodsDetailDTO
	 * @param orderEntity
	 * @return RenterOrderReqVO
	 */
	public RenterOrderReqVO convertToRenterOrderReqVO(ModifyOrderDTO modifyOrderDTO, RenterMemberDTO renterMemberDTO, RenterGoodsDetailDTO renterGoodsDetailDTO, OrderEntity orderEntity) {
		RenterOrderReqVO renterOrderReqVO = new RenterOrderReqVO();
		renterOrderReqVO.setAbatement(modifyOrderDTO.getAbatementFlag()==null?"0":String.valueOf(modifyOrderDTO.getAbatementFlag()));
		renterOrderReqVO.setCarLat(renterGoodsDetailDTO.getCarShowLat());
		renterOrderReqVO.setCarLon(renterGoodsDetailDTO.getCarShowLon());
		renterOrderReqVO.setCarOwnerCouponNo(modifyOrderDTO.getCarOwnerCouponId());
		renterOrderReqVO.setCertificationTime(renterMemberDTO.getCertificationTime());
		renterOrderReqVO.setCityCode(orderEntity.getCityCode());
		renterOrderReqVO.setDisCouponIds(modifyOrderDTO.getPlatformCouponId());
		renterOrderReqVO.setDriverIds(modifyOrderDTO.getDriverIds());
		renterOrderReqVO.setEntryCode(orderEntity.getEntryCode());
		renterOrderReqVO.setGetCarBeforeTime(modifyOrderDTO.getGetCarBeforeTime());
		renterOrderReqVO.setGetCarFreeCouponId(modifyOrderDTO.getSrvGetReturnCouponId());
		renterOrderReqVO.setGuidPrice(renterGoodsDetailDTO.getCarGuidePrice());
		renterOrderReqVO.setInmsrp(renterGoodsDetailDTO.getCarInmsrp());
		renterOrderReqVO.setLabelIds(renterGoodsDetailDTO.getLabelIds());
		renterOrderReqVO.setMemNo(modifyOrderDTO.getMemNo());
		renterOrderReqVO.setOrderNo(modifyOrderDTO.getOrderNo());
		renterOrderReqVO.setRenterGoodsPriceDetailDTOList(renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList());
		renterOrderReqVO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterOrderReqVO.setRentTime(modifyOrderDTO.getRentTime());
		renterOrderReqVO.setReturnCarAfterTime(modifyOrderDTO.getReturnCarAfterTime());
		renterOrderReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		renterOrderReqVO.setSource(orderEntity.getSource());
		renterOrderReqVO.setSrvGetFlag(modifyOrderDTO.getSrvGetFlag());
		renterOrderReqVO.setSrvGetLat(modifyOrderDTO.getGetCarLat());
		renterOrderReqVO.setSrvGetLon(modifyOrderDTO.getGetCarLon());
		renterOrderReqVO.setSrvReturnFlag(modifyOrderDTO.getSrvReturnFlag());
		renterOrderReqVO.setSrvReturnLat(modifyOrderDTO.getRevertCarLat());
		renterOrderReqVO.setSrvReturnLon(modifyOrderDTO.getRevertCarLon());
		renterOrderReqVO.setUseAutoCoin(modifyOrderDTO.getUserCoinFlag());
		return renterOrderReqVO;
	}
	
}
