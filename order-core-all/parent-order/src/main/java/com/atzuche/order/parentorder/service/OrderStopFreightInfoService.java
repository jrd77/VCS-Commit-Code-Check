package com.atzuche.order.parentorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.parentorder.entity.OrderStopFreightInfo;
import com.atzuche.order.parentorder.mapper.OrderStopFreightInfoMapper;

@Service
public class OrderStopFreightInfoService {

	@Autowired
	private OrderStopFreightInfoMapper orderStopFreightInfoMapper;
	
	public Integer getCountByOrderNo(String orderNo) {
		return orderStopFreightInfoMapper.getCountByOrderNo(orderNo);
	}
	
	public int insertSelective(OrderStopFreightInfo record) {
		return orderStopFreightInfoMapper.insertSelective(record);
	}
	
	public int updateByPrimaryKeySelective(OrderStopFreightInfo record) {
		return orderStopFreightInfoMapper.updateByPrimaryKeySelective(record);
	}
}
