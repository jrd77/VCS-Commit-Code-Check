package com.atzuche.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.entity.OwnerOrderEntity;
import com.atzuche.order.mapper.OwnerOrderMapper;

@Service
public class OwnerOrderService {

	@Autowired
	private OwnerOrderMapper ownerOrderMapper;
	
	/**
	 * 获取有效的车主子订单信息
	 * @param orderNo 主订单号
	 * @return OwnerOrderEntity
	 */
	public OwnerOrderEntity getOwnerOrderByOrderNoAndIsEffective(String orderNo) {
		return ownerOrderMapper.getOwnerOrderByOrderNoAndIsEffective(orderNo);
	}
}
