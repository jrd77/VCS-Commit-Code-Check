package com.atzuche.order.delivery.service;

import java.util.List;

import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class RenterOrderDeliveryService {

	@Resource
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
