package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.RenterOrderStatusEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderOwnerDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.CarService.CarDetailReqVO;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.rentercommodity.service.CommodityService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
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
		
		// 获取修改前
		// TODO DTO包装
		ModifyOrderDTO modifyOrderDTO = getModifyOrderDTO(modifyOrderReq, renterOrderNo, initRenterOrder);
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
		
		// 基础费用计算
		
		// TODO 取还车费用计算
		
		// TODO 超运能费用计算
		
		// 违约金计算
		
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
	public ModifyOrderDTO getModifyOrderDTO(ModifyOrderReq modifyOrderReq, String renterOrderNo, RenterOrderEntity initRenterOrder) {
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
		if (StringUtils.isBlank(modifyOrderReq.getGetCarAddress())) {
			
		}
		if (StringUtils.isBlank(modifyOrderReq.getGetCarLat())) {
			
		}
		if (StringUtils.isBlank(modifyOrderReq.getGetCarLon())) {
			
		}
		if (StringUtils.isBlank(modifyOrderReq.getRevertCarAddress())) {
			
		}
		if (StringUtils.isBlank(modifyOrderReq.getRevertCarLat())) {
			
		}
		if (StringUtils.isBlank(modifyOrderReq.getRevertCarLon())) {
			
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
	
	
	
	
}
