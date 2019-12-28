package com.atzuche.order.coreapi.utils;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.atzuche.order.commons.enums.CouponTypeEnum;
import com.atzuche.order.coreapi.entity.dto.ModifyFlagDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;

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
	 * 是否修改取还车地址
	 * @param initLon 修改前
	 * @param initLat 修改前
	 * @param updLon 修改后
	 * @param updLat 修改后
	 * @return boolean
	 */
	public static boolean getModifyGetReturnAddrFlag(String initLon, String initLat, String updLon, String updLat) {
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
	
	
	public static ModifyFlagDTO getModifyFlagDTO(RenterOrderEntity initRenterOrder, ModifyOrderReq updModifyOrder, List<OrderCouponEntity> orderCouponList) {
		if (updModifyOrder == null || initRenterOrder == null) {
			return null;
		}
		ModifyFlagDTO modifyFlagDTO = new ModifyFlagDTO();
		modifyFlagDTO.setModifyAbatementFlag(getModifyFlag(initRenterOrder.getIsAbatement(), updModifyOrder.getAbatementFlag()));
		modifyFlagDTO.setModifyDriverFlag(getModifyDriverFlag(updModifyOrder.getDriverIds()));
		//modifyFlagDTO.setModifyGetAddrFlag(getModifyGetReturnAddrFlag(initRenterOrder, initLat, updLon, updLat));
		String initCarOwnerCouponId = null;
		String initGetReturnCouponId = null;
		String initPlatformCouponId = null;
		if (orderCouponList != null && !orderCouponList.isEmpty()) {
			Map<Integer, String> orderCouponMap = orderCouponList.stream().collect(Collectors.toMap(OrderCouponEntity::getCouponType, OrderCouponEntity::getCouponId));
			initCarOwnerCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode()); 
			initGetReturnCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
			initPlatformCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
		}
		modifyFlagDTO.setModifyCarOwnerCouponFlag(getModifyStrFlag(initCarOwnerCouponId, updModifyOrder.getCarOwnerCouponId()));
		modifyFlagDTO.setModifyPlatformCouponFlag(getModifyStrFlag(initPlatformCouponId, updModifyOrder.getPlatformCouponId()));
		modifyFlagDTO.setModifyGetReturnCouponFlag(getModifyStrFlag(initGetReturnCouponId, updModifyOrder.getSrvGetReturnCouponId()));
		modifyFlagDTO.setModifyUserCoinFlag(getModifyFlag(initRenterOrder.getIsUseCoin(), updModifyOrder.getUserCoinFlag()));
		modifyFlagDTO.setModifySrvGetFlag(getModifyFlag(initRenterOrder.getIsGetCar(), updModifyOrder.getSrvGetFlag()));
		modifyFlagDTO.setModifySrvReturnFlag(getModifyFlag(initRenterOrder.getIsReturnCar(), updModifyOrder.getSrvReturnFlag()));
		return modifyFlagDTO;
	}
	
}
