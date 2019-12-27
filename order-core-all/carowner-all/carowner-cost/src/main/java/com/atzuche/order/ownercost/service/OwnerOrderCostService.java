package com.atzuche.order.ownercost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.mapper.OwnerOrderCostMapper;

@Service
public class OwnerOrderCostService {

	@Autowired
	private OwnerOrderCostMapper ownerOrderCostMapper;
	
	/**
	 * 保存车主费用总表信息
	 * @param ownerOrderCostEntity 车主费用总表信息
	 * @return Integer
	 */
	public Integer saveOwnerOrderCost(OwnerOrderCostEntity ownerOrderCostEntity) {
		return ownerOrderCostMapper.saveOwnerOrderCost(ownerOrderCostEntity);
	}
}
