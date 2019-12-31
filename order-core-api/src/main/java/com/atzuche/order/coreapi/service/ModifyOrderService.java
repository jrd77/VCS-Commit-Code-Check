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
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.entity.vo.CostDeductVO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.CarService.CarDetailReqVO;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.CommodityService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.AutoCoinCostCalService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.AutoCoinService;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterAdditionalDriverService;
import com.atzuche.order.renterorder.service.RenterOrderCalCostService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.autoyol.auto.coin.service.vo.res.AutoCoinResponseVO;
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
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private RenterAdditionalDriverService renterAdditionalDriverService;
	@Autowired
	private AutoCoinService autoCoinService;
	@Autowired
	private AutoCoinCostCalService autoCoinCostCalService;
	@Autowired
	private RenterGoodsService renterGoodsService;

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
		
		
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderEntity(modifyOrderDTO.getOrderNo());
		// 获取修改前租客费用明细
		List<RenterOrderCostDetailEntity> initCostList = renterOrderCostDetailService.listRenterOrderCostDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 获取修改前补贴信息
		List<RenterOrderSubsidyDetailEntity> initSubsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 封装计算用对象
		RenterOrderReqVO renterOrderReqVO = convertToRenterOrderReqVO(modifyOrderDTO, renterMemberDTO, renterGoodsDetailDTO, orderEntity);
		// 基础费用计算包含租金，手续费，基础保障费用，全面保障费用，附加驾驶人保障费用，取还车费用计算和超运能费用计算
		RenterOrderCostRespDTO renterOrderCostRespDTO = getRenterOrderCostRespDTO(modifyOrderDTO, renterOrderReqVO, initCostList, initSubsidyList);
		// 获取修改订单违约金
		List<RenterOrderFineDeatailEntity> renterFineList = getRenterFineList(modifyOrderDTO, initRenterOrder, deliveryList, renterOrderCostRespDTO);
		// 封装基础信息对象
		CostBaseDTO costBaseDTO = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		// 获取抵扣补贴(含车主券，限时立减，取还车券，平台券和凹凸币)
		CostDeductVO costDeductVO = getCostDeductVO(modifyOrderDTO, costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, orderEntity, initSubsidyList);
		// 聚合租客补贴
		renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(getPolymerizationSubsidy(renterOrderCostRespDTO, costDeductVO));
		// 
		// 修改前费用
		
		// 修改后费用
		
		// 入库
		// 保存租客商品信息
		renterGoodsService.save(renterGoodsDetailDTO);
		// 保存租客会员信息
		renterMemberService.save(renterMemberDTO);
		// 保存租客子单信息
		renterOrderService.saveRenterOrder(renterOrderNew);
		// 保存基本费用和补贴
		renterOrderCalCostService.saveOrderCostAndDeailList(renterOrderCostRespDTO);
		// 保存罚金
		renterOrderFineDeatailService.saveRenterOrderFineDeatailBatch(renterFineList);
		// 
		
		// TODO 发送MQ
		
		return null;
	}
	
	/**
	 * 聚合租客补贴
	 * @param renterOrderCostRespDTO
	 * @param costDeductVO
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getPolymerizationSubsidy(RenterOrderCostRespDTO renterOrderCostRespDTO, CostDeductVO costDeductVO) {
		// 补贴
		List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = costDeductVO.getRenterSubsidyList();
		List<RenterOrderSubsidyDetailDTO> rosdList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		if (renterOrderSubsidyDetailDTOList != null && !renterOrderSubsidyDetailDTOList.isEmpty()) {
			rosdList.addAll(renterOrderSubsidyDetailDTOList);
		}
		if (renterSubsidyList != null && !renterSubsidyList.isEmpty()) {
			rosdList.addAll(renterSubsidyList);
		}
		return rosdList;
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
		// 获取修改前的附加驾驶人列表
		List<String> drivers = renterAdditionalDriverService.listDriverIdByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// 本次修改新增的附加驾驶人列表
		List<String> addDrivers = modifyOrderReq.getDriverIds();
		// 汇总后的附加驾驶人
		List<String> afterDrivers = new ArrayList<String>();
		if (drivers != null && !drivers.isEmpty()) {
			afterDrivers.addAll(drivers); 
		}
		if (addDrivers != null && !addDrivers.isEmpty()) {
			afterDrivers.addAll(addDrivers);
		}
		// 去重处理
		afterDrivers = afterDrivers.stream().distinct().collect(Collectors.toList());
		modifyOrderDTO.setDriverIds(afterDrivers);
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
		renterOrderNew.setIsUseCoin(modifyOrderDTO.getUserCoinFlag());
		if (StringUtils.isNotBlank(modifyOrderDTO.getCarOwnerCouponId()) || 
				StringUtils.isNotBlank(modifyOrderDTO.getPlatformCouponId()) || 
				StringUtils.isNotBlank(modifyOrderDTO.getSrvGetReturnCouponId())) {
			renterOrderNew.setIsUseCoupon(1);
		} else {
			renterOrderNew.setIsUseCoupon(0);
		}
		renterOrderNew.setIsGetCar(modifyOrderDTO.getSrvGetFlag());
		renterOrderNew.setIsReturnCar(modifyOrderDTO.getSrvReturnFlag());
		int addDriver = modifyOrderDTO.getDriverIds() == null ? 0:modifyOrderDTO.getDriverIds().size();
		renterOrderNew.setAddDriver(addDriver);
		renterOrderNew.setIsAbatement(modifyOrderDTO.getAbatementFlag());
		renterOrderNew.setIsEffective(0);
		renterOrderNew.setAgreeFlag(0);
		renterOrderNew.setCreateOp(null);
		renterOrderNew.setCreateTime(null);
		renterOrderNew.setUpdateOp(null);
		renterOrderNew.setUpdateTime(null);
		return renterOrderNew;
	}
	
	
	/**
	 * 基础费用计算
	 * @param modifyOrderDTO
	 * @param renterMemberDTO
	 * @param renterGoodsDetailDTO
	 * @return RenterOrderCostRespDTO
	 */
	public RenterOrderCostRespDTO getRenterOrderCostRespDTO(ModifyOrderDTO modifyOrderDTO, RenterOrderReqVO renterOrderReqVO, List<RenterOrderCostDetailEntity> initCostList, List<RenterOrderSubsidyDetailEntity> initSubsidyList) {
		// 构建参数
		RenterOrderCostReqDTO renterOrderCostReqDTO = renterOrderService.buildRenterOrderCostReqDTO(renterOrderReqVO);
		// 获取费用补贴列表
		List<RenterOrderSubsidyDetailDTO> subsidyList = getRenterSubsidyList(renterOrderCostReqDTO, modifyOrderDTO.getRenterSubsidyList(), initCostList, initSubsidyList);
		renterOrderCostReqDTO.setSubsidyOutList(subsidyList);
		// 获取计算好的费用信息
		RenterOrderCostRespDTO renterOrderCostRespDTO = renterOrderCalCostService.getOrderCostAndDeailList(renterOrderCostReqDTO);
		renterOrderCostRespDTO.setOrderNo(modifyOrderDTO.getOrderNo());
		renterOrderCostRespDTO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterOrderCostRespDTO.setMemNo(modifyOrderDTO.getMemNo());
		return renterOrderCostRespDTO;
	}
	
	/**
	 * 获取费用补贴列表
	 * @param renterOrderCostReqDTO
	 * @param renterSubsidyList
	 * @param initCostList
	 * @param initSubsidyList
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getRenterSubsidyList(RenterOrderCostReqDTO renterOrderCostReqDTO, List<RenterOrderSubsidyDetailDTO> renterSubsidyList, List<RenterOrderCostDetailEntity> initCostList, List<RenterOrderSubsidyDetailEntity> initSubsidyList) {
		if (renterSubsidyList == null) {
			renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		}
		Map<String,RenterOrderSubsidyDetailDTO> subMap = renterSubsidyList.stream().collect(Collectors.toMap(RenterOrderSubsidyDetailDTO::getSubsidyCostCode, sub -> {return sub;}));
		if (subMap == null || subMap.get(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo()) == null) {
			//获取平台保障费
	        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
	        Integer insurAmt = insurAmtEntity.getTotalAmount();
	        // 修改前平台保障费
	        Integer initInsurAmt = 0;
	        if (initCostList != null && !initCostList.isEmpty()) {
	        	initInsurAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
	        }
	        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
	        	initInsurAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
	        }
	        if (insurAmt != null && initInsurAmt != null && Math.abs(initInsurAmt) > Math.abs(insurAmt)) {
	        	Integer subsidyAmount = Math.abs(insurAmt) - Math.abs(initInsurAmt);
	        	// 产生补贴
	        	RenterOrderSubsidyDetailDTO subsidyDetail = convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.INSURE_AMT, 
	        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.INSURE_TOTAL_PRICES, "修改订单平台保障费不退还");
	        	renterSubsidyList.add(subsidyDetail);
	        }
		}
		if (subMap == null || subMap.get(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo()) == null) {
			//获取全面保障费
	        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
	        Integer comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
	        // 修改前全面保障费
	        Integer initAbatementAmt = 0;
	        if (initCostList != null && !initCostList.isEmpty()) {
	        	initAbatementAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
	        }
	        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
	        	initAbatementAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
	        }
	        if (comprehensiveEnsureAmount != null && initAbatementAmt != null && Math.abs(initAbatementAmt) > Math.abs(comprehensiveEnsureAmount)) {
	        	Integer subsidyAmount = Math.abs(comprehensiveEnsureAmount) - Math.abs(initAbatementAmt);
	        	// 产生补贴
	        	RenterOrderSubsidyDetailDTO subsidyDetail = convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.INSURE_AMT, 
	        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.ABATEMENT_INSURE, "修改订单全面保障费不退还");
	        	renterSubsidyList.add(subsidyDetail);
	        }
		}
		return renterSubsidyList;
	}
	
	/**
	 * 封装费用补贴对象
	 * @param costBaseDTO
	 * @param subsidyAmount
	 * @param type
	 * @param source
	 * @param targe
	 * @param cash
	 * @param subsidyDesc
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO convertToRenterOrderSubsidyDetailDTO(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidyTypeCodeEnum type, SubsidySourceCodeEnum source, SubsidySourceCodeEnum targe, RenterCashCodeEnum cash, String subsidyDesc) {
		RenterOrderSubsidyDetailDTO subd = new RenterOrderSubsidyDetailDTO();
		subd.setMemNo(costBaseDTO.getMemNo());
		subd.setOrderNo(costBaseDTO.getOrderNo());
		subd.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		subd.setSubsidyAmount(subsidyAmount);
		subd.setSubsidyCostCode(cash.getCashNo());
		subd.setSubsidyCostName(cash.getTxt());
		subd.setSubsidyDesc(subsidyDesc);
		subd.setSubsidySourceCode(source.getCode());
		subd.setSubsidySourceName(source.getDesc());
		subd.setSubsidyTargetCode(targe.getCode());
		subd.setSubsidyTargetName(targe.getDesc());
		subd.setSubsidyTypeCode(type.getCode());
		subd.setSubsidyTypeName(type.getDesc());
		return subd;
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
		RenterOrderFineDeatailEntity renterFine = renterOrderFineDeatailService.calcFineAmt(updBase, Math.abs(initAmt), Math.abs(updAmt), initRenterOrder.getExpRevertTime());
		return renterFine;
	}
	
	
	/**
	 * 抵扣补贴
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @param orderEntity
	 * @return CostDeductVO
	 */
	public CostDeductVO getCostDeductVO(ModifyOrderDTO modifyOrderDTO, CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO, OrderEntity orderEntity, List<RenterOrderSubsidyDetailEntity> initSubsidyList) {
		// 抵扣列表
		List<OrderCouponDTO> orderCouponList = new ArrayList<OrderCouponDTO>();
		// 补贴列表
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		// 剩余抵扣的租金
		Integer surplusRentAmt = Math.abs(renterOrderCostRespDTO.getRentAmount());
		// 获取车主券抵扣
		OrderCouponDTO ownerCoupon = getOwnerCoupon(costBaseDTO, renterOrderReqVO, surplusRentAmt);
		if (ownerCoupon != null) {
			orderCouponList.add(ownerCoupon);
			int ownerCouponAmt = ownerCoupon.getAmount() == null ? 0:ownerCoupon.getAmount();
			surplusRentAmt = surplusRentAmt - ownerCouponAmt;
		}
		// 获取车主券补贴
		RenterOrderSubsidyDetailDTO ownerCouponSubsidy = getOwnerCouponSubsidy(costBaseDTO, ownerCoupon);
		if (ownerCouponSubsidy != null) {
			renterSubsidyList.add(ownerCouponSubsidy);
		}
		// 获取限时立减补贴
		RenterOrderSubsidyDetailDTO limitRedSubsidy = getLimitRedSubsidy(costBaseDTO, orderEntity, surplusRentAmt);
		if (limitRedSubsidy != null) {
			renterSubsidyList.add(limitRedSubsidy);
			int limitRedAmt = limitRedSubsidy.getSubsidyAmount() == null ? 0:limitRedSubsidy.getSubsidyAmount();
			surplusRentAmt = surplusRentAmt - limitRedAmt;
		}
		// 获取取还车券抵扣
		OrderCouponDTO getCarFeeCoupon = getGetCarFeeCoupon(costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO);
		if (getCarFeeCoupon != null) {
			orderCouponList.add(getCarFeeCoupon);
		}
		// 获取取还车补贴
		RenterOrderSubsidyDetailDTO getCarFeeCouponSubsidy = getGetCarFeeCouponSubsidy(costBaseDTO, getCarFeeCoupon);
		if (getCarFeeCouponSubsidy != null) {
			renterSubsidyList.add(getCarFeeCouponSubsidy);
		}
		// 获取平台券抵扣
		OrderCouponDTO platformCoupon = getPlatformCoupon(costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, getCarFeeCoupon, surplusRentAmt);
		if (platformCoupon != null) {
			orderCouponList.add(platformCoupon);
		}
		// 获取平台券补贴
		RenterOrderSubsidyDetailDTO platformCouponSubsidy = getPlatformCouponSubsidy(costBaseDTO, platformCoupon);
		if (platformCouponSubsidy != null) {
			renterSubsidyList.add(platformCouponSubsidy);
			int platformCouponAmt = platformCouponSubsidy.getSubsidyAmount() == null ? 0:platformCouponSubsidy.getSubsidyAmount();
			surplusRentAmt = surplusRentAmt - platformCouponAmt;
		}
		// 凹凸币补贴
		RenterOrderSubsidyDetailDTO autoCoinSubsidy = getAutoCoinSubsidy(costBaseDTO, modifyOrderDTO, initSubsidyList, renterOrderCostRespDTO.getRentAmount(), surplusRentAmt);
		if (autoCoinSubsidy != null) {
			renterSubsidyList.add(autoCoinSubsidy);
		}
		CostDeductVO costDeductVO = new CostDeductVO();
		costDeductVO.setOrderCouponList(orderCouponList);
		costDeductVO.setRenterSubsidyList(renterSubsidyList);
		return costDeductVO;
	}
	
	
	/**
	 * 获取车主券抵扣
	 * @param costBaseDTO
	 * @param renterOrderReqVO
	 * @param surplusRentAmt
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getOwnerCoupon(CostBaseDTO costBaseDTO, RenterOrderReqVO renterOrderReqVO, Integer surplusRentAmt) {
		OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = renterOrderService.buildOwnerCouponGetAndValidReqVO(renterOrderReqVO,
				surplusRentAmt);
		// 修改标识
		ownerCouponGetAndValidReqVO.setMark(2);
        OrderCouponDTO ownerCoupon = renterOrderCalCostService.calOwnerCouponDeductInfo(ownerCouponGetAndValidReqVO);
        if (ownerCoupon != null) {
        	ownerCoupon.setOrderNo(costBaseDTO.getOrderNo());
            ownerCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return ownerCoupon;
	}
	
	/**
	 * 获取车主券补贴
	 * @param costBaseDTO
	 * @param renterOrderReqVO
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getOwnerCouponSubsidy(CostBaseDTO costBaseDTO, OrderCouponDTO ownerCoupon) {
        if(ownerCoupon == null) {
        	return null;
        }
        //补贴明细
        RenterOrderSubsidyDetailDTO ownerCouponSubsidyInfo =
                renterOrderSubsidyDetailService.calOwnerCouponSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo()),
                ownerCoupon);
        if (ownerCouponSubsidyInfo != null) {
        	ownerCouponSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
        	ownerCouponSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return ownerCouponSubsidyInfo;
	}
	
	/**
	 * 限时立减补贴
	 * @param costBaseDTO
	 * @param orderEntity
	 * @param surplusRentAmt
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getLimitRedSubsidy(CostBaseDTO costBaseDTO, OrderEntity orderEntity, Integer surplusRentAmt) {
		if (orderEntity == null) {
			return null;
		}
		// 限时红包limit_amt
        Integer reductiAmt = orderEntity.getLimitAmt() == null ? 0:orderEntity.getLimitAmt();
        if (reductiAmt == null || reductiAmt == 0) {
        	return null;
        }
        RenterOrderSubsidyDetailDTO limitRedSubsidyInfo =
                renterOrderSubsidyDetailService.calLimitRedSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo()),
                        surplusRentAmt,reductiAmt);
        if (limitRedSubsidyInfo != null) {
        	limitRedSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
            limitRedSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return limitRedSubsidyInfo;
	}
	
	
	/**
	 * 获取车主券抵扣
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getGetCarFeeCoupon(CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO) {
		MemAvailCouponRequestVO getCarFeeCouponReqVO = renterOrderService.buildMemAvailCouponRequestVO(renterOrderCostRespDTO, renterOrderReqVO);
		getCarFeeCouponReqVO.setDisCouponId(renterOrderReqVO.getGetCarFreeCouponId());
		OrderCouponDTO getCarFeeCoupon = renterOrderCalCostService.calGetAndReturnSrvCouponDeductInfo(getCarFeeCouponReqVO);
		if (getCarFeeCoupon != null) {
			getCarFeeCoupon.setOrderNo(costBaseDTO.getOrderNo());
			getCarFeeCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		}
		return getCarFeeCoupon;
	}
	
	
	/**
	 * 获取取还车优惠券补贴
	 * @param costBaseDTO
	 * @param getCarFeeCoupon
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getGetCarFeeCouponSubsidy(CostBaseDTO costBaseDTO, OrderCouponDTO getCarFeeCoupon) {
		if (getCarFeeCoupon == null) {
			return null;
		}
        //记录送取服务券
        getCarFeeCoupon.setOrderNo(costBaseDTO.getOrderNo());
        getCarFeeCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        //补贴明细
        RenterOrderSubsidyDetailDTO getCarFeeCouponSubsidyInfo =
                renterOrderSubsidyDetailService.calGetCarFeeCouponSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo())
                        , getCarFeeCoupon);
        if (getCarFeeCouponSubsidyInfo != null) {
        	getCarFeeCouponSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
            getCarFeeCouponSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return getCarFeeCouponSubsidyInfo;
	}
	
	
	/**
	 * 获取平台券抵扣
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @param getCarFeeCoupon
	 * @param surplusRentAmt
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getPlatformCoupon(CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO, OrderCouponDTO getCarFeeCoupon, Integer surplusRentAmt) {
		MemAvailCouponRequestVO platformCouponReqVO = renterOrderService.buildMemAvailCouponRequestVO(renterOrderCostRespDTO,
                renterOrderReqVO);
        platformCouponReqVO.setDisCouponId(renterOrderReqVO.getDisCouponIds());
        if (getCarFeeCoupon != null) {
        	platformCouponReqVO.setSrvGetCost(0);
        	platformCouponReqVO.setSrvReturnCost(0);
        }
        platformCouponReqVO.setRentAmt(surplusRentAmt);
        OrderCouponDTO platfromCoupon = renterOrderCalCostService.calPlatformCouponDeductInfo(platformCouponReqVO);
        if (platfromCoupon != null) {
        	platfromCoupon.setOrderNo(costBaseDTO.getOrderNo());
        	platfromCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return platfromCoupon;
	}
	
	/**
	 * 获取平台券补贴
	 * @param costBaseDTO
	 * @param platfromCoupon
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getPlatformCouponSubsidy(CostBaseDTO costBaseDTO, OrderCouponDTO platformCoupon) {
		//补贴明细
        RenterOrderSubsidyDetailDTO platformCouponSubsidyInfo =
                renterOrderSubsidyDetailService.calPlatformCouponSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo()), platformCoupon);
        if (platformCouponSubsidyInfo == null) {
        	return null;
        }
        platformCouponSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
        platformCouponSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        return platformCouponSubsidyInfo;
	}
	
	/**
	 * 获取凹凸币补贴
	 * @param costBaseDTO
	 * @param modifyOrderDTO
	 * @param initSubsidyList
	 * @param rentAmt
	 * @param surplusRentAmt
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getAutoCoinSubsidy(CostBaseDTO costBaseDTO, ModifyOrderDTO modifyOrderDTO, List<RenterOrderSubsidyDetailEntity> initSubsidyList, Integer rentAmt, Integer surplusRentAmt) {
		// 是否使用凹凸币
		Integer userAutoCoinFlag = modifyOrderDTO.getUserCoinFlag();
		if (userAutoCoinFlag == null || !userAutoCoinFlag.equals(1)) {
			return null;
		}
		// 修改前凹凸币抵扣金额
		Integer initAutoCoinAmt = 0;
		if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
			initAutoCoinAmt = initSubsidyList.stream().filter(subsid -> {
				return RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(subsid.getSubsidyCostCode());
				}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
		}
		AutoCoinResponseVO crmCustPoint = autoCoinService.getCrmCustPoint(Integer.valueOf(costBaseDTO.getMemNo()));
		RenterOrderSubsidyDetailDTO autoCoinSubsidy = autoCoinCostCalService.calAutoCoinDeductInfo(rentAmt, surplusRentAmt, crmCustPoint, initAutoCoinAmt);
		return autoCoinSubsidy;
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
		renterOrderReqVO.setMemNo(Integer.valueOf(modifyOrderDTO.getMemNo()));
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
		renterOrderReqVO.setIsNew((renterMemberDTO.getIsNew() != null && renterMemberDTO.getIsNew() == 1) ? true:false);
		return renterOrderReqVO;
	}
	
}
