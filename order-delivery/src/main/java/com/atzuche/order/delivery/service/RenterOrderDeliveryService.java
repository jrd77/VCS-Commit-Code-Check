package com.atzuche.order.delivery.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryMapper.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
		if (deliveryList == null || deliveryList.isEmpty()) {
			return null;
		}
		// 根据type去重
		deliveryList = deliveryList.stream().collect(
	            Collectors.collectingAndThen(
	                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RenterOrderDeliveryEntity::getType))), ArrayList::new));
		return deliveryList;
	}
}
