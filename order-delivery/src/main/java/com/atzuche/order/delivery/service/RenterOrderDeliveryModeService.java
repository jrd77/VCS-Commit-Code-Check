package com.atzuche.order.delivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryModeMapper;

@Service
public class RenterOrderDeliveryModeService {

	@Autowired
	private RenterOrderDeliveryModeMapper renterOrderDeliveryModeMapper;
	
	public RenterOrderDeliveryMode getDeliveryModeByRenterOrderNo(String renterOrderNo) {
		return renterOrderDeliveryModeMapper.getDeliveryModeByRenterOrderNo(renterOrderNo);
	}
	
	public int saveRenterOrderDeliveryMode(RenterOrderDeliveryMode renterOrderDeliveryMode) {
		return renterOrderDeliveryModeMapper.insertSelective(renterOrderDeliveryMode);
	}
}
