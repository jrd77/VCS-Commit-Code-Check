/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.coreapi.entity.OrderSecondOpenEntity;
import com.atzuche.order.coreapi.mapper.OrderSecondOpenMapper;

/**
 * @author jing.huang
 *
 */
@Service
public class OrderSecondOpenService {
	@Autowired
	OrderSecondOpenMapper orderSecondOpenMapper;
	
	public int insert(OrderSecondOpenEntity record) {
		return orderSecondOpenMapper.insert(record);
	}
	//查询
	public List<OrderSecondOpenEntity> query(){
		return orderSecondOpenMapper.query();
	}
	//修改flag
	public int updateFlag(List<Integer> ids) {
		return orderSecondOpenMapper.updateFlag(ids);
	}
	
}
