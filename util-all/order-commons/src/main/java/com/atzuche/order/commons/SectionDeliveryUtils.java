package com.atzuche.order.commons;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.entity.dto.TransProgressDTO;
import com.atzuche.order.commons.vo.res.QuHuanQujianVO;
import com.atzuche.order.commons.vo.res.QuZhiHuanQujianVO;
import com.atzuche.order.commons.vo.res.QuZhiHuanZhunshiVO;
import com.atzuche.order.commons.vo.res.QuhuanZhunshiVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryResultVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;
import com.atzuche.order.commons.vo.res.ZhiquZhihuanVO;

public class SectionDeliveryUtils {
	
	private static final Integer RENTER = 1;
	private static final Integer OWNER = 2;

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
	
	
	/**
	 * 取还车区间配送
	 * @param result
	 * @param sectionParam
	 * @param pro
	 * @return List<QuHuanQujianVO>
	 */
	public static List<QuHuanQujianVO> listQuHuanQujianVO(SectionDeliveryResultVO result, SectionParamDTO sectionParam, TransProgressDTO pro) {
		SectionDeliveryVO renterS = result.getRenterSectionDelivery();
		List<QuHuanQujianVO> list = new ArrayList<QuHuanQujianVO>();
		if (renterS != null) {
			QuHuanQujianVO renter = new QuHuanQujianVO();
			BeanUtils.copyProperties(renterS, renter);
			renter.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			renter.setRealRentTime(pro.getRenterRentTime());
			renter.setRealRevertTime(pro.getRenterRevertTime());
			renter.setReturnCarAfterTime(sectionParam.getReturnCarAfterTime());
			renter.setMemType(RENTER);
			list.add(renter);
		}
		SectionDeliveryVO ownerS = result.getOwnerSectionDelivery();
		if (ownerS != null) {
			QuHuanQujianVO owner = new QuHuanQujianVO();
			BeanUtils.copyProperties(ownerS, owner);
			owner.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			owner.setRealRentTime(pro.getOwnerRentTime());
			owner.setRealRevertTime(pro.getOwnerRevertTime());
			owner.setReturnCarAfterTime(sectionParam.getReturnCarAfterTime());
			owner.setMemType(OWNER);
			list.add(owner);
		}
		return list;
	}
	
	/**
	 * 取还车准时达
	 * @param result
	 * @param sectionParam
	 * @param pro
	 * @param mode
	 * @return List<QuhuanZhunshiVO>
	 */
	public static List<QuhuanZhunshiVO> listQuhuanZhunshiVO(SectionDeliveryResultVO result, SectionParamDTO sectionParam, TransProgressDTO pro) {
		SectionDeliveryVO renterS = result.getRenterSectionDelivery();
		List<QuhuanZhunshiVO> list = new ArrayList<QuhuanZhunshiVO>();
		if (renterS != null) {
			QuhuanZhunshiVO renter = new QuhuanZhunshiVO();
			renter.setAccurateGetSrvUnit(renterS.getAccurateGetSrvUnit());
			renter.setAccurateReturnSrvUnit(renterS.getAccurateReturnSrvUnit());
			renter.setExpectRentTime(renterS.getDefaultRentTime());
			renter.setExpectRevertTime(renterS.getDefaultRevertTime());
			renter.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			renter.setReturnCarAfterTime(sectionParam.getReturnCarAfterTime());
			renter.setRealRentTime(pro.getRenterRentTime());
			renter.setRealRevertTime(pro.getRenterRevertTime());
			renter.setMemType(RENTER);
			list.add(renter);
		}
		SectionDeliveryVO ownerS = result.getOwnerSectionDelivery();
		if (ownerS != null) {
			QuhuanZhunshiVO owner = new QuhuanZhunshiVO();
			owner.setAccurateGetSrvUnit(ownerS.getAccurateGetSrvUnit());
			owner.setAccurateReturnSrvUnit(ownerS.getAccurateReturnSrvUnit());
			owner.setExpectRentTime(ownerS.getDefaultRentTime());
			owner.setExpectRevertTime(ownerS.getDefaultRevertTime());
			owner.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			owner.setReturnCarAfterTime(sectionParam.getReturnCarAfterTime());
			owner.setRealRentTime(pro.getOwnerRentTime());
			owner.setRealRevertTime(pro.getOwnerRevertTime());
			owner.setMemType(OWNER);
			list.add(owner);
		}
		return list;
	}
	
	/**
	 * 取车服务-自还-区间配送
	 * @param result
	 * @param sectionParam
	 * @param pro
	 * @return List<QuZhiHuanQujianVO>
	 */
	public static List<QuZhiHuanQujianVO> listQuZhiHuanQujianVO(SectionDeliveryResultVO result, SectionParamDTO sectionParam, TransProgressDTO pro) {
		SectionDeliveryVO renterS = result.getRenterSectionDelivery();
		List<QuZhiHuanQujianVO> list = new ArrayList<QuZhiHuanQujianVO>();
		if (renterS != null) {
			QuZhiHuanQujianVO renter = new QuZhiHuanQujianVO();
			BeanUtils.copyProperties(renterS, renter);
			renter.setExpectRevertTime(renterS.getDefaultRevertTime());
			renter.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			renter.setRealRentTime(pro.getRenterRentTime());
			renter.setRealRevertTime(pro.getRenterRevertTime());
			renter.setMemType(RENTER);
			list.add(renter);
		}
		SectionDeliveryVO ownerS = result.getOwnerSectionDelivery();
		if (ownerS != null) {
			QuZhiHuanQujianVO owner = new QuZhiHuanQujianVO();
			BeanUtils.copyProperties(ownerS, owner);
			owner.setExpectRevertTime(ownerS.getDefaultRevertTime());
			owner.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			owner.setRealRentTime(pro.getOwnerRentTime());
			owner.setRealRevertTime(pro.getOwnerRevertTime());
			owner.setMemType(OWNER);
			list.add(owner);
		}
		return list;
	}
	
	
	/**
	 * 取车服务-自还-准时达
	 * @param result
	 * @param sectionParam
	 * @param pro
	 * @return List<QuZhiHuanQujianVO>
	 */
	public static List<QuZhiHuanZhunshiVO> listQuZhiHuanZhunshiVO(SectionDeliveryResultVO result, SectionParamDTO sectionParam, TransProgressDTO pro) {
		SectionDeliveryVO renterS = result.getRenterSectionDelivery();
		List<QuZhiHuanZhunshiVO> list = new ArrayList<QuZhiHuanZhunshiVO>();
		if (renterS != null) {
			QuZhiHuanZhunshiVO renter = new QuZhiHuanZhunshiVO();
			renter.setAccurateGetSrvUnit(renterS.getAccurateGetSrvUnit());
			renter.setExpectRentTime(renterS.getDefaultRentTime());
			renter.setExpectRevertTime(renterS.getDefaultRevertTime());
			renter.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			renter.setRealRentTime(pro.getRenterRentTime());
			renter.setRealRevertTime(pro.getRenterRevertTime());
			renter.setMemType(RENTER);
			list.add(renter);
		}
		SectionDeliveryVO ownerS = result.getOwnerSectionDelivery();
		if (ownerS != null) {
			QuZhiHuanZhunshiVO owner = new QuZhiHuanZhunshiVO();
			owner.setAccurateGetSrvUnit(ownerS.getAccurateGetSrvUnit());
			owner.setExpectRentTime(ownerS.getDefaultRentTime());
			owner.setExpectRevertTime(ownerS.getDefaultRevertTime());
			owner.setGetCarBeforeTime(sectionParam.getGetCarBeforeTime());
			owner.setRealRentTime(pro.getOwnerRentTime());
			owner.setRealRevertTime(pro.getOwnerRevertTime());
			owner.setMemType(OWNER);
			list.add(owner);
		}
		return list;
	}
	
	
	/**
	 * 自取自还
	 * @param result
	 * @param pro
	 * @return List<ZhiquZhihuanVO>
	 */
	public static List<ZhiquZhihuanVO> listZhiquZhihuanVO(SectionDeliveryResultVO result, TransProgressDTO pro) {
		List<ZhiquZhihuanVO> list = new ArrayList<ZhiquZhihuanVO>();
		SectionDeliveryVO renterS = result.getRenterSectionDelivery();
		if (renterS != null) {
			ZhiquZhihuanVO renter = new ZhiquZhihuanVO();
			renter.setExpectRentTime(renterS.getDefaultRentTime());
			renter.setExpectRevertTime(renterS.getDefaultRevertTime());
			renter.setRealRentTime(pro.getRenterRentTime());
			renter.setRealRevertTime(pro.getRenterRevertTime());
			renter.setMemType(RENTER);
			list.add(renter);
		}
		SectionDeliveryVO ownerS = result.getOwnerSectionDelivery();
		if (ownerS != null) {
			ZhiquZhihuanVO owner = new ZhiquZhihuanVO();
			owner.setExpectRentTime(ownerS.getDefaultRentTime());
			owner.setExpectRevertTime(ownerS.getDefaultRevertTime());
			owner.setRealRentTime(pro.getOwnerRentTime());
			owner.setRealRevertTime(pro.getOwnerRevertTime());
			owner.setMemType(OWNER);
			list.add(owner);
		}
		return list;
	}
}
