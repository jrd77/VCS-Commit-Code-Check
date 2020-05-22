package com.atzuche.order.coreapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderExtendService {
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private OrderCouponService orderCouponService;
	@Autowired
	private OwnerGoodsService ownerGoodsService;
	

	/**
	 * 获取修改前数据
	 * @param modifyOrderReq
	 * @return ModifyOrderDTO
	 */
	public ModifyOrderDTO getInitModifyOrderDTO(ModifyOrderReq modifyOrderReq) {
		// 主单号
		String orderNo = modifyOrderReq.getOrderNo();
		// 获取修改前有效租客子订单信息
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		ModifyOrderDTO modifyOrderDTO = new ModifyOrderDTO();
		// 是否使用全面保障
		modifyOrderDTO.setAbatementFlag(initRenterOrder.getIsAbatement());
		modifyOrderDTO.setRentTime(initRenterOrder.getExpRentTime());
		modifyOrderDTO.setRevertTime(initRenterOrder.getExpRevertTime());
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				modifyOrderDTO.setGetCarAddress(srvGetDelivery.getRenterGetReturnAddr());
				modifyOrderDTO.setGetCarLat(srvGetDelivery.getRenterGetReturnAddrLat());
				modifyOrderDTO.setGetCarLon(srvGetDelivery.getRenterGetReturnAddrLon());
			}
			if (srvReturnDelivery != null) {
				modifyOrderDTO.setRevertCarAddress(srvReturnDelivery.getRenterGetReturnAddr());
				modifyOrderDTO.setRevertCarLat(srvReturnDelivery.getRenterGetReturnAddrLat());
				modifyOrderDTO.setRevertCarLon(srvReturnDelivery.getRenterGetReturnAddrLon());
			}
		}
		modifyOrderDTO.setSrvGetFlag(initRenterOrder.getIsGetCar());
		modifyOrderDTO.setSrvReturnFlag(initRenterOrder.getIsReturnCar());
		modifyOrderDTO.setUserCoinFlag(initRenterOrder.getIsUseCoin());
		// 获取修改前租客使用的优惠券列表
		List<OrderCouponEntity> orderCouponList = orderCouponService.listOrderCouponByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		String initCarOwnerCouponId = null;
		String initGetReturnCouponId = null;
		String initPlatformCouponId = null;
		if (orderCouponList != null && !orderCouponList.isEmpty()) {
			// 设置已使用的优惠券列表
			modifyOrderDTO.setOrderCouponList(orderCouponList);
			Map<Integer, String> orderCouponMap = orderCouponList.stream().collect(Collectors.toMap(OrderCouponEntity::getCouponType, OrderCouponEntity::getCouponId));
			initCarOwnerCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode()); 
			initGetReturnCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
			initPlatformCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
		}
		modifyOrderDTO.setCarOwnerCouponId(initCarOwnerCouponId);
		modifyOrderDTO.setSrvGetReturnCouponId(initGetReturnCouponId);
		modifyOrderDTO.setPlatformCouponId(initPlatformCouponId);
		modifyOrderDTO.setChangeItemList(ModifyOrderUtils.listOrderChangeItemDTO(null, initRenterOrder, modifyOrderReq, orderCouponList, deliveryMap));
		return modifyOrderDTO;
	}
	
	
	/**
	 * 获取车牌号
	 * @param orderNo
	 * @return String
	 */
	public String getCarPlateNum(String orderNo) {
		// 获取最新的车主商品信息
		OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getLastOwnerGoodsByOrderNo(orderNo);
		return ownerGoodsEntity.getCarPlateNum();
	}
}
