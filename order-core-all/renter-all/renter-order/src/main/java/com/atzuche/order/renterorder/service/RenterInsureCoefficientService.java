package com.atzuche.order.renterorder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.InsurAmtDTO;
import com.atzuche.order.commons.vo.RenterInsureCoefficientReasonVO;
import com.atzuche.order.commons.vo.RenterInsureCoefficientVO;
import com.atzuche.order.renterorder.entity.RenterInsureCoefficient;
import com.atzuche.order.renterorder.entity.RenterInsureCoefficientReason;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.mapper.RenterInsureCoefficientMapper;
import com.atzuche.order.renterorder.mapper.RenterInsureCoefficientReasonMapper;
import com.autoyol.platformcost.CommonUtils;

@Service
public class RenterInsureCoefficientService {

	@Autowired
	private RenterInsureCoefficientMapper renterInsureCoefficientMapper;
	@Autowired
	private RenterInsureCoefficientReasonMapper renterInsureCoefficientReasonMapper;
	
	
	/**
	 * 保存系数
	 * @param coefficientList
	 */
	public void saveRenterInsureCoefficient(List<RenterInsureCoefficientVO> coefficientList) {
		if (coefficientList == null || coefficientList.isEmpty()) {
			return;
		}
		for (RenterInsureCoefficientVO coefficient:coefficientList) {
			RenterInsureCoefficient ric = new RenterInsureCoefficient();
			BeanUtils.copyProperties(coefficient, ric);
			renterInsureCoefficientMapper.insertSelective(ric);
			List<RenterInsureCoefficientReasonVO> reasonList = coefficient.getReasonList();
			saveRenterInsureCoefficientReason(reasonList, ric.getId());
		}
		
	}
	
	/**
	 * 保存系数因数
	 * @param reasonList
	 * @param coefficientId
	 */
	public void saveRenterInsureCoefficientReason(List<RenterInsureCoefficientReasonVO> reasonList, Integer coefficientId) {
		if (reasonList == null || reasonList.isEmpty()) {
			return;
		}
		for (RenterInsureCoefficientReasonVO reason:reasonList) {
			RenterInsureCoefficientReason ricr = new RenterInsureCoefficientReason();
			BeanUtils.copyProperties(reason, ricr);
			ricr.setInsureCoefficientId(coefficientId);
			renterInsureCoefficientReasonMapper.insertSelective(ricr);
		}
	}
	
	/**
	 * 组合保存
	 * @param renterOrderCostReqDTO
	 */
	public void saveCombineCoefficient(RenterOrderCostReqDTO renterOrderCostReqDTO) {
		if (renterOrderCostReqDTO == null) {
			return;
		}
		InsurAmtDTO insurAmtDTO = renterOrderCostReqDTO.getInsurAmtDTO();
		if (insurAmtDTO == null) {
			return;
		}
		CostBaseDTO costBaseDTO = insurAmtDTO.getCostBaseDTO();
		// 驾龄系数
		Double coefficient = CommonUtils.getDriveAgeCoefficientByDri(insurAmtDTO.getCertificationTime());
		RenterInsureCoefficientVO memCoefficient = new RenterInsureCoefficientVO(costBaseDTO.getOrderNo(), costBaseDTO.getRenterOrderNo(), coefficient, 1);
		String certificationTime = insurAmtDTO.getCertificationTime() == null ? null:CommonUtils.localDateToString(insurAmtDTO.getCertificationTime(), CommonUtils.FORMAT_STR_DATE);
		RenterInsureCoefficientReasonVO memReason = new RenterInsureCoefficientReasonVO("driving_age", "驾龄", certificationTime);
		List<RenterInsureCoefficientReasonVO> memReasonList = new ArrayList<RenterInsureCoefficientReasonVO>();
		memReasonList.add(memReason);
		memCoefficient.setReasonList(memReasonList);
		// 易出险车系数
		Double easyCoefficient = CommonUtils.getEasyCoefficient(insurAmtDTO.getCarLabelIds(), insurAmtDTO.getCarLevel());
		RenterInsureCoefficientVO easy = new RenterInsureCoefficientVO(costBaseDTO.getOrderNo(), costBaseDTO.getRenterOrderNo(), easyCoefficient, 2);
		String carTags = insurAmtDTO.getCarLabelIds() == null || insurAmtDTO.getCarLabelIds().isEmpty() ? null:String.join(",",insurAmtDTO.getCarLabelIds());
		RenterInsureCoefficientReasonVO easyReason1 = new RenterInsureCoefficientReasonVO("car_tags", "车辆标签", carTags);
		String carLevel = insurAmtDTO.getCarLevel() == null ? null:String.valueOf(insurAmtDTO.getCarLevel());
		RenterInsureCoefficientReasonVO easyReason2 = new RenterInsureCoefficientReasonVO("car_level", "车辆等级", carLevel);
		List<RenterInsureCoefficientReasonVO> easyReasonList = new ArrayList<RenterInsureCoefficientReasonVO>();
		easyReasonList.add(easyReason1);
		easyReasonList.add(easyReason2);
		easy.setReasonList(easyReasonList);
		// 驾驶行为系数
		Double driverCoefficient = CommonUtils.getDriverCoefficient(insurAmtDTO.getDriverScore());
		RenterInsureCoefficientVO driver = new RenterInsureCoefficientVO(costBaseDTO.getOrderNo(), costBaseDTO.getRenterOrderNo(), driverCoefficient, 3);
		RenterInsureCoefficientReasonVO driverReason = new RenterInsureCoefficientReasonVO("driver_score", "驾驶行为评分", insurAmtDTO.getDriverScore());
		List<RenterInsureCoefficientReasonVO> driverReasonList = new ArrayList<RenterInsureCoefficientReasonVO>();
		driverReasonList.add(driverReason);
		driver.setReasonList(driverReasonList);
		// 折扣，基本保费和不计免赔使用
		Integer inmsrpGuidePrice = null;
		if (insurAmtDTO != null) {
			inmsrpGuidePrice = insurAmtDTO.getInmsrp() == null ? insurAmtDTO.getGuidPrice():insurAmtDTO.getInmsrp();
		}
		// 获取保险和不计免赔的折扣
		double insureDiscount = CommonUtils.getInsureDiscount(costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), inmsrpGuidePrice);
		RenterInsureCoefficientVO discount = new RenterInsureCoefficientVO(costBaseDTO.getOrderNo(), costBaseDTO.getRenterOrderNo(), insureDiscount, 4);
		List<RenterInsureCoefficientVO> list = new ArrayList<RenterInsureCoefficientVO>();
		list.add(memCoefficient);
		list.add(easy);
		list.add(driver);
		list.add(discount);
		saveRenterInsureCoefficient(list);
	}
	
	
	public List<RenterInsureCoefficient> listInsurecoeByRenterOrderNo(String renterOrderNo) {
		return renterInsureCoefficientMapper.listInsurecoeByRenterOrderNo(renterOrderNo);
	}
	
	public List<RenterInsureCoefficientReason> listInsurecoeReasonByInsucoeId(Integer insureCoefficientId) {
		return renterInsureCoefficientReasonMapper.listInsurecoeReasonByInsucoeId(insureCoefficientId);
	}
	
	public List<RenterInsureCoefficientReasonVO> listRenterInsureCoefficientReasonVO(Integer insureCoefficientId) {
		List<RenterInsureCoefficientReason> reasonList = listInsurecoeReasonByInsucoeId(insureCoefficientId);
		if (reasonList == null || reasonList.isEmpty()) {
			return null;
		}
		List<RenterInsureCoefficientReasonVO> reasonvoList = new ArrayList<RenterInsureCoefficientReasonVO>();
		for (RenterInsureCoefficientReason reason:reasonList) {
			RenterInsureCoefficientReasonVO reasonvo = new RenterInsureCoefficientReasonVO();
			BeanUtils.copyProperties(reason, reasonvo);
			reasonvoList.add(reasonvo);
		}
		return reasonvoList;
	}
	
	public List<RenterInsureCoefficientVO> listRenterInsureCoefficientVO(String renterOrderNo) {
		List<RenterInsureCoefficient> inscoeList = listInsurecoeByRenterOrderNo(renterOrderNo);
		if (inscoeList == null || inscoeList.isEmpty()) {
			return null;
		}
		List<RenterInsureCoefficientVO> inscoeVOList = new ArrayList<RenterInsureCoefficientVO>();
		for (RenterInsureCoefficient inscoe:inscoeList) {
			Integer insureCoefficientId = inscoe.getId();
			RenterInsureCoefficientVO insvo = new RenterInsureCoefficientVO();
			BeanUtils.copyProperties(inscoe, insvo);
			insvo.setReasonList(listRenterInsureCoefficientReasonVO(insureCoefficientId));
			inscoeVOList.add(insvo);
		}
		return inscoeVOList;
	}
 }
