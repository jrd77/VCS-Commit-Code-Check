package com.atzuche.order.renterorder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.atzuche.order.renterorder.mapper.OrderChangeItemMapper;

@Service
public class OrderChangeItemService {

	@Autowired
	private OrderChangeItemMapper orderChangeItemMapper;
	
	/**
	 * 获取修改项
	 * @param renterOrderNo
	 * @return List<OrderChangeItemDTO>
	 */
	public List<String> listChangeCodeByRenterOrderNo(String renterOrderNo) {
		return orderChangeItemMapper.listChangeCodeByRenterOrderNo(renterOrderNo);
	}
	
	/**
	 * 保存修改项目
	 * @param changeItemList
	 * @return Integer
	 */
	public Integer saveOrderChangeItemBatch(List<OrderChangeItemDTO> changeItemList) {
		if (changeItemList == null || changeItemList.isEmpty()) {
			return 1;
		}
		return orderChangeItemMapper.saveOrderChangeItemBatch(changeItemList);
	}
}
