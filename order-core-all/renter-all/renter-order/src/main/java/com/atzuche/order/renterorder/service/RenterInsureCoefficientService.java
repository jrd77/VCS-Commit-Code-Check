package com.atzuche.order.renterorder.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.vo.RenterInsureCoefficientReasonVO;
import com.atzuche.order.commons.vo.RenterInsureCoefficientVO;
import com.atzuche.order.renterorder.entity.RenterInsureCoefficient;
import com.atzuche.order.renterorder.entity.RenterInsureCoefficientReason;
import com.atzuche.order.renterorder.mapper.RenterInsureCoefficientMapper;
import com.atzuche.order.renterorder.mapper.RenterInsureCoefficientReasonMapper;

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
		for (RenterInsureCoefficientReasonVO reason:reasonList) {
			RenterInsureCoefficientReason ricr = new RenterInsureCoefficientReason();
			BeanUtils.copyProperties(reason, ricr);
			ricr.setInsureCoefficientId(coefficientId);
			renterInsureCoefficientReasonMapper.insertSelective(ricr);
		}
	}
}
