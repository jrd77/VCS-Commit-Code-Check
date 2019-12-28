package com.atzuche.delivery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.delivery.mapper.RenterOrderDeliveryMapper;

@Service
public class RenterOrderDeliveryService {

	@Autowired
	private RenterOrderDeliveryMapper renterOrderDeliveryMapper;
	
	/**
	 * 根据租客子单号获取配送信息列表
	 * @param renterOrderNo 租客子单号
	 * @return List<RenterOrderDeliveryEntity>
	 */
	public List<RenterOrderDeliveryEntity> listRenterOrderDeliveryByRenterOrderNo(String renterOrderNo) {
		return renterOrderDeliveryMapper.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
	}
}
