package com.atzuche.order.coreapi.utils;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.commons.enums.OrderChangeItemEnum;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyFlagDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.autoyol.platformcost.CommonUtils;

public class ModifyOrderUtils {

	/**
	 * 是否修改
	 * @param initFlag 修改前
	 * @param updFlag 修改后
	 * @return boolean
	 */
	public static boolean getModifyFlag(Integer initFlag, Integer updFlag) {
		if (updFlag != null && !updFlag.equals(initFlag)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否修改取还车时间
	 * @param initTime 修改前
	 * @param updTime 修改后
	 * @return boolean
	 */
	public static boolean getModifyRentTimeFlag(LocalDateTime initTime, LocalDateTime updTime) {
		if (updTime != null && !updTime.isEqual(initTime)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否修改取还车时间
	 * @param initTime 修改前
	 * @param updTime 修改后
	 * @return boolean
	 */
	public static boolean getModifyTimeFlag(LocalDateTime initTime, String updTimeStr) {
		LocalDateTime updTime = null;
		if (StringUtils.isNotBlank(updTimeStr)) {
			updTime = CommonUtils.parseTime(updTimeStr, CommonUtils.FORMAT_STR_LONG);
		}
		if (updTime != null && !updTime.equals(initTime)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否修改取还车地址
	 * @param initLon 修改前
	 * @param initLat 修改前
	 * @param updLon 修改后
	 * @param updLat 修改后
	 * @return boolean
	 */
	public static boolean getModifyGetReturnAddrFlag(String initLon, String initLat, String updLon, String updLat) {
		if ((StringUtils.isBlank(initLon) && StringUtils.isNotBlank(updLon)) || 
				(StringUtils.isBlank(initLat) && StringUtils.isNotBlank(updLat))) {
			return true;
		}
		if (StringUtils.isNotBlank(initLon) && StringUtils.isNotBlank(updLon)) {
			BigDecimal bigInitLon = new BigDecimal(initLon);
			BigDecimal bigLon = new BigDecimal(updLon);
			if (bigInitLon.compareTo(bigLon) != 0) {
				//修改了地址
				return true;
			}
		}
		if (StringUtils.isNotBlank(initLat) && StringUtils.isNotBlank(updLat)) {
			BigDecimal bigInitLat = new BigDecimal(initLat);
			BigDecimal bigLat = new BigDecimal(updLat);
			if (bigInitLat.compareTo(bigLat) != 0) {
				//修改了地址
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否修改附加驾驶人
	 * @param driverIds 新增的附加驾驶人列表
	 * @return boolean
	 */
	public static boolean getModifyDriverFlag(List<String> driverIds) {
		if (driverIds != null && !driverIds.isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否修改
	 * @param initCouponId 修改前
	 * @param updCouponId 修改后
	 * @return boolean
	 */
	public static boolean getModifyStrFlag(String initCouponId, String updCouponId) {
		if (StringUtils.isNotBlank(updCouponId) && !updCouponId.equals(initCouponId)) {
			return true;
		}
		return false;
	}
	
	
	public static List<OrderChangeItemDTO> listOrderChangeItemDTO(String renterOrderNo, RenterOrderEntity initRenterOrder, ModifyOrderReq updModifyOrder, List<OrderCouponEntity> orderCouponList, Map<Integer, RenterOrderDeliveryEntity> deliveryMap) {
		if (updModifyOrder == null || initRenterOrder == null) {
			return null;
		}
		List<OrderChangeItemDTO> changeItemList = new ArrayList<OrderChangeItemDTO>();
		if (getModifyTimeFlag(initRenterOrder.getExpRentTime(), updModifyOrder.getRentTime())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_RENTTIME.getCode(), OrderChangeItemEnum.MODIFY_RENTTIME.getName()));
		}
		if (getModifyTimeFlag(initRenterOrder.getExpRevertTime(), updModifyOrder.getRevertTime())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_REVERTTIME.getCode(), OrderChangeItemEnum.MODIFY_REVERTTIME.getName()));
		}
		if (getModifyFlag(initRenterOrder.getIsAbatement(), updModifyOrder.getAbatementFlag())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_ABATEMENT.getCode(), OrderChangeItemEnum.MODIFY_ABATEMENT.getName()));
		}
		if (getModifyDriverFlag(updModifyOrder.getDriverIds())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_DRIVER.getCode(), OrderChangeItemEnum.MODIFY_DRIVER.getName()));
		}
		String initGetLon = null,initGetLat = null,initReturnLon = null,initReturnLat = null;
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				initGetLon = srvGetDelivery.getRenterGetReturnAddrLon();
				initGetLat = srvGetDelivery.getRenterGetReturnAddrLat();
			}
			if (srvReturnDelivery != null) {
				initReturnLon = srvReturnDelivery.getRenterGetReturnAddrLon();
				initReturnLat = srvReturnDelivery.getRenterGetReturnAddrLat();
			}
		}
		if (getModifyGetReturnAddrFlag(initGetLon, initGetLat, updModifyOrder.getGetCarLon(), updModifyOrder.getGetCarLat())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_GETADDR.getCode(), OrderChangeItemEnum.MODIFY_GETADDR.getName()));
		}
		if (getModifyGetReturnAddrFlag(initReturnLon, initReturnLat, updModifyOrder.getRevertCarLon(), updModifyOrder.getRevertCarLat())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_RETURNADDR.getCode(), OrderChangeItemEnum.MODIFY_RETURNADDR.getName()));
		}
		String initCarOwnerCouponId = null,initGetReturnCouponId = null,initPlatformCouponId = null;
		if (orderCouponList != null && !orderCouponList.isEmpty()) {
			Map<Integer, String> orderCouponMap = orderCouponList.stream().collect(Collectors.toMap(OrderCouponEntity::getCouponType, OrderCouponEntity::getCouponId));
			initCarOwnerCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode()); 
			initGetReturnCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
			initPlatformCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
		}
		if (getModifyStrFlag(initCarOwnerCouponId, updModifyOrder.getCarOwnerCouponId())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_CAROWNERCOUPON.getCode(), OrderChangeItemEnum.MODIFY_CAROWNERCOUPON.getName()));
		}
		if (getModifyStrFlag(initPlatformCouponId, updModifyOrder.getPlatformCouponId())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_PLATFORMCOUPON.getCode(), OrderChangeItemEnum.MODIFY_PLATFORMCOUPON.getName()));
		}
		if (getModifyStrFlag(initGetReturnCouponId, updModifyOrder.getSrvGetReturnCouponId())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_GETRETURNCOUPON.getCode(), OrderChangeItemEnum.MODIFY_GETRETURNCOUPON.getName()));
		}
		if (getModifyFlag(initRenterOrder.getIsUseCoin(), updModifyOrder.getUserCoinFlag())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_USERCOINFLAG.getCode(), OrderChangeItemEnum.MODIFY_USERCOINFLAG.getName()));
		}
		if (getModifyFlag(initRenterOrder.getIsGetCar(), updModifyOrder.getSrvGetFlag())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_SRVGETFLAG.getCode(), OrderChangeItemEnum.MODIFY_SRVGETFLAG.getName()));
		}
		if (getModifyFlag(initRenterOrder.getIsReturnCar(), updModifyOrder.getSrvReturnFlag())) {
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_SRVRETURNFLAG.getCode(), OrderChangeItemEnum.MODIFY_SRVRETURNFLAG.getName()));
		}
		if (updModifyOrder.getTransferFlag() != null && updModifyOrder.getTransferFlag() && StringUtils.isNotBlank(updModifyOrder.getCarNo())) {
			// 换车操作
			changeItemList.add(new OrderChangeItemDTO(initRenterOrder.getOrderNo(), renterOrderNo, OrderChangeItemEnum.MODIFY_TRANSFER.getCode(), OrderChangeItemEnum.MODIFY_TRANSFER.getName()));
		}
		return changeItemList;
	}
	
}
