package com.atzuche.order.commons;

import java.time.LocalDateTime;

import org.apache.commons.lang.StringUtils;

import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.vo.res.SectionDeliveryResultVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;

public class SectionDeliveryUtils {

	/**
	 * 同时获取租客处和车主处区间配送信息
	 * @param sectionParam
	 * @param formate
	 * @return SectionDeliveryResultVO
	 */
	public static SectionDeliveryResultVO getSectionDeliveryResultVO(SectionParamDTO sectionParam, String formate) {
		if (sectionParam == null) {
			return null;
		}
		SectionDeliveryResultVO result = new SectionDeliveryResultVO();
		SectionDeliveryVO renterSectionDelivery = getRenterSectionDeliveryVO(sectionParam, formate);
		SectionDeliveryVO ownerSectionDelivery = getOwnerSectionDeliveryVO(sectionParam, formate);
		result.setRenterSectionDelivery(renterSectionDelivery);
		result.setOwnerSectionDelivery(ownerSectionDelivery);
		return result;
	}
	
	/**
	 * 获取租客处区间配送信息
	 * @param sectionParam
	 * @param formate
	 * @return SectionDeliveryVO
	 */
	public static SectionDeliveryVO getRenterSectionDeliveryVO(SectionParamDTO sectionParam, String formate) {
		if (sectionParam == null) {
			return null;
		}
		SectionDeliveryVO renterSectionDeliveryVO = new SectionDeliveryVO();
		formate = StringUtils.isBlank(formate) ? DateUtils.DATE_DEFAUTE1:formate;
		renterSectionDeliveryVO.setRentTimeStart(getStartTime(sectionParam.getRentTime(), sectionParam.getRentBeforeMinutes(), formate));
		renterSectionDeliveryVO.setRentTimeEnd(getEndTime(sectionParam.getRentTime(), sectionParam.getRentAfterMinutes(), formate));
		renterSectionDeliveryVO.setRevertTimeStart(getStartTime(sectionParam.getRevertTime(), sectionParam.getRevertBeforeMinutes(), formate));
		renterSectionDeliveryVO.setRevertTimeEnd(getEndTime(sectionParam.getRevertTime(), sectionParam.getRevertAfterMinutes(), formate));
		renterSectionDeliveryVO.setDefaultRentTime(DateUtils.formate(sectionParam.getRentTime(), formate));
		renterSectionDeliveryVO.setDefaultRevertTime(DateUtils.formate(sectionParam.getRevertTime(), formate));
		return renterSectionDeliveryVO;
	}
	
	/**
	 * 获取车主处区间配送信息
	 * @param sectionParam
	 * @param formate
	 * @return SectionDeliveryVO
	 */
	public static SectionDeliveryVO getOwnerSectionDeliveryVO(SectionParamDTO sectionParam, String formate) {
		if (sectionParam == null) {
			return null;
		}
		SectionDeliveryVO ownerSectionDeliveryVO = new SectionDeliveryVO();
		formate = StringUtils.isBlank(formate) ? DateUtils.DATE_DEFAUTE1:formate;
		LocalDateTime rentTimeStartl = getStartLocalDateTime(sectionParam.getRentTime(), sectionParam.getRentBeforeMinutes());
		LocalDateTime rentTimeEndl = getEndLocalDateTime(sectionParam.getRentTime(), sectionParam.getRentAfterMinutes());
		LocalDateTime revertTimeStartl = getStartLocalDateTime(sectionParam.getRevertTime(), sectionParam.getRevertBeforeMinutes());
		LocalDateTime revertTimeEndl = getEndLocalDateTime(sectionParam.getRevertTime(), sectionParam.getRevertAfterMinutes());
		ownerSectionDeliveryVO.setRentTimeStart(calBeforeTime(rentTimeStartl, sectionParam.getGetCarBeforeTime(), formate));
		ownerSectionDeliveryVO.setRentTimeEnd(calBeforeTime(rentTimeEndl, sectionParam.getGetCarBeforeTime(), formate));
		ownerSectionDeliveryVO.setRevertTimeStart(calAfterTime(revertTimeStartl, sectionParam.getReturnCarAfterTime(), formate));
		ownerSectionDeliveryVO.setRevertTimeEnd(calAfterTime(revertTimeEndl, sectionParam.getReturnCarAfterTime(), formate));
		ownerSectionDeliveryVO.setDefaultRentTime(calBeforeTime(sectionParam.getRentTime(), sectionParam.getGetCarBeforeTime(), formate));
		ownerSectionDeliveryVO.setDefaultRevertTime(calAfterTime(sectionParam.getRevertTime(), sectionParam.getReturnCarAfterTime(), formate));
		return ownerSectionDeliveryVO;
	}
	
	/**
	 * 计算租客最早时间
	 * @param curTime
	 * @param beforeMinutes
	 * @param formate
	 * @return String
	 */
	public static String getStartTime(LocalDateTime curTime, Integer beforeMinutes, String formate) {
		if (curTime == null) {
			return null;
		}
		if (StringUtils.isBlank(formate)) {
			formate = DateUtils.DATE_DEFAUTE1;
		}
		if (beforeMinutes == null) {
			return DateUtils.formate(curTime, formate);
		}
		LocalDateTime beforeTime = curTime.minusMinutes(beforeMinutes);
		return DateUtils.formate(beforeTime, formate);
	}
	
	/**
	 * 计算租客最晚时间
	 * @param curTime
	 * @param afterMinutes
	 * @param formate
	 * @return String
	 */
	public static String getEndTime(LocalDateTime curTime, Integer afterMinutes, String formate) {
		if (curTime == null) {
			return null;
		}
		if (StringUtils.isBlank(formate)) {
			formate = DateUtils.DATE_DEFAUTE1;
		}
		if (afterMinutes == null) {
			return DateUtils.formate(curTime, formate);
		}
		LocalDateTime afterTime = curTime.plusMinutes(afterMinutes);
		return DateUtils.formate(afterTime, formate);
	}
	
	
	/**
	 * 计算租客最早时间
	 * @param curTime
	 * @param beforeMinutes
	 * @return LocalDateTime
	 */
	public static LocalDateTime getStartLocalDateTime(LocalDateTime curTime, Integer beforeMinutes) {
		if (curTime == null) {
			return null;
		}
		if (beforeMinutes == null) {
			return curTime;
		}
		LocalDateTime beforeTime = curTime.minusMinutes(beforeMinutes);
		return beforeTime;
	}
	
	/**
	 * 计算租客最晚时间
	 * @param curTime
	 * @param afterMinutes
	 * @return LocalDateTime
	 */
	public static LocalDateTime getEndLocalDateTime(LocalDateTime curTime, Integer afterMinutes) {
		if (curTime == null) {
			return null;
		}
		if (afterMinutes == null) {
			return curTime;
		}
		LocalDateTime afterTime = curTime.plusMinutes(afterMinutes);
		return afterTime;
	}
	
	
	/**
	 * 计算提前时间
	 * @param rentTime 取车时间
	 * @param getCarBeforeTime 提前时间（单位分钟）
	 * @param formate
	 * @return String
	 */
	public static String calBeforeTime(LocalDateTime rentTime, Integer getCarBeforeTime, String formate){
		if (rentTime == null) {
			return null;
		}
		if (StringUtils.isBlank(formate)) {
			formate = DateUtils.DATE_DEFAUTE1;
		}
		if (getCarBeforeTime == null) {
			return DateUtils.formate(rentTime, formate);
		}
		LocalDateTime beforeTime = rentTime.minusMinutes(getCarBeforeTime);
		return DateUtils.formate(beforeTime, formate);
	}
	
	/**
	 * 计算延后时间
	 * @param revertTime 还车时间
	 * @param returnCarAfterTime 延后时间（单位分钟）
	 * @param formate
	 * @return String
	 */
	public static String calAfterTime(LocalDateTime revertTime, Integer returnCarAfterTime, String formate){
		if (revertTime == null) {
			return null;
		}
		if (StringUtils.isBlank(formate)) {
			formate = DateUtils.DATE_DEFAUTE1;
		}
		if (returnCarAfterTime == null) {
			return DateUtils.formate(revertTime, formate);
		}
		LocalDateTime afterTime = revertTime.plusMinutes(returnCarAfterTime);
		return DateUtils.formate(afterTime, formate);
	}
}
