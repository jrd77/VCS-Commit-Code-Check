package com.atzuche.order.delivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.SectionDeliveryUtils;
import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryModeMapper;

@Service
public class RenterOrderDeliveryModeService {

	@Autowired
	private RenterOrderDeliveryModeMapper renterOrderDeliveryModeMapper;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	
	public RenterOrderDeliveryMode getDeliveryModeByRenterOrderNo(String renterOrderNo) {
		return renterOrderDeliveryModeMapper.getDeliveryModeByRenterOrderNo(renterOrderNo);
	}
	
	public int saveRenterOrderDeliveryMode(RenterOrderDeliveryMode renterOrderDeliveryMode) {
		return renterOrderDeliveryModeMapper.insertSelective(renterOrderDeliveryMode);
	}
	
	
	/**
	 * 获取区间配送信息
	 * @param renterOrderNo
	 * @param rentTime
	 * @param revertTime
	 * @return sectionDeliveryVO
	 */
	public SectionDeliveryVO getRenterSectionDeliveryVO(String renterOrderNo, LocalDateTime rentTime, LocalDateTime revertTime) {
		RenterOrderDeliveryMode mode = getDeliveryModeByRenterOrderNo(renterOrderNo);
		if (mode == null) {
			return null;
		}
		SectionParamDTO sectionParam = new SectionParamDTO();
		BeanUtils.copyProperties(mode, sectionParam);
		sectionParam.setRentTime(rentTime);
		sectionParam.setRevertTime(revertTime);
		SectionDeliveryVO sectionDeliveryVO = SectionDeliveryUtils.getRenterSectionDeliveryVO(sectionParam, DateUtils.DATE_DEFAUTE1);
		if (sectionDeliveryVO != null) {
			sectionDeliveryVO.setDistributionMode(mode.getDistributionMode());
		}
		return sectionDeliveryVO;
	}
	
	
	/**
	 * 获取区间配送信息
	 * @param renterOrderNo
	 * @param rentTime
	 * @param revertTime
	 * @return sectionDeliveryVO
	 */
	public SectionDeliveryVO getOwnerSectionDeliveryVO(String renterOrderNo, LocalDateTime rentTime, LocalDateTime revertTime) {
		RenterOrderDeliveryMode mode = getDeliveryModeByRenterOrderNo(renterOrderNo);
		if (mode == null) {
			return null;
		}
		Integer getCarBeforeTime = 0;
		Integer returnCarAfterTime = 0;
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				getCarBeforeTime = srvGetDelivery.getAheadOrDelayTime();
			}
			if (srvReturnDelivery != null) {
				returnCarAfterTime = srvReturnDelivery.getAheadOrDelayTime();
			}
		}
		SectionParamDTO sectionParam = new SectionParamDTO();
		BeanUtils.copyProperties(mode, sectionParam);
		sectionParam.setRentTime(rentTime);
		sectionParam.setRevertTime(revertTime);
		sectionParam.setGetCarBeforeTime(getCarBeforeTime);
		sectionParam.setReturnCarAfterTime(returnCarAfterTime);
		SectionDeliveryVO sectionDeliveryVO = SectionDeliveryUtils.getOwnerSectionDeliveryVO(sectionParam, DateUtils.DATE_DEFAUTE1);
		if (sectionDeliveryVO != null) {
			sectionDeliveryVO.setDistributionMode(mode.getDistributionMode());
		}
		return sectionDeliveryVO;
	}
}
