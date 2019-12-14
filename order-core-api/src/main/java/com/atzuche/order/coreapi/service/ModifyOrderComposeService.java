package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.coreapi.entity.bo.RenterGoodsPriceBO;
import com.atzuche.rentercommodity.entity.RenterGoodsPriiceDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.rentercommodity.service.RenterGoodsPriiceDetailService;

@Service
public class ModifyOrderComposeService {

	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterGoodsPriiceDetailService renterGoodsPriiceDetailService;
	
	/**
	 * 获取已同意的租客子订单列表含商品价格列表
	 * @param orderNo 车主订单号
	 * @return List<RenterGoodsPriceBO>
	 */
	public List<RenterGoodsPriceBO> listRenterOrderPrice(Long orderNo) {
		// 获取已同意的租客子单
		List<RenterOrderEntity> renterOrderList = renterOrderService.listAgreeRenterOrderByOrderNo(orderNo);
		if (renterOrderList == null || renterOrderList.isEmpty()) {
			return null;
		}
		// 最后一条索引
		Integer lastIndex = renterOrderList.size() - 1;
		// 最后一条起租时间
		LocalDateTime lastRentTime = renterOrderList.get(lastIndex).getExpRentStartTime();
		List<RenterOrderEntity> renterOrderAfterList = new ArrayList<RenterOrderEntity>();
		for (int i=lastIndex; i>=0; i--) {
			LocalDateTime expRentStartTime = renterOrderList.get(i).getExpRentStartTime();
			if (lastRentTime != null && expRentStartTime != null && !lastRentTime.isEqual(expRentStartTime)) {
				break;
			}
			renterOrderAfterList.add(renterOrderList.get(i));
		}
		RenterOrderEntity lastRenterOrderEntity = renterOrderAfterList.get(0);
		RenterOrderEntity curRenterOrderEntity = null;
		List<RenterOrderEntity> renterOrderThenList = new ArrayList<RenterOrderEntity>();
		for (int i=0; i<renterOrderAfterList.size(); i++) {
			curRenterOrderEntity = renterOrderAfterList.get(i);
			LocalDateTime lastStartTime = lastRenterOrderEntity.getExpRentStartTime();
			LocalDateTime lastEndTime = lastRenterOrderEntity.getExpRentEndTime();
			LocalDateTime curStartTime = curRenterOrderEntity.getExpRentStartTime();
			LocalDateTime curEndTime = curRenterOrderEntity.getExpRentEndTime();
			// 顺便做了一个去重
            if ((i > 0 && curStartTime.isEqual(lastStartTime) && curEndTime.isEqual(lastEndTime))
                    || curEndTime.isAfter(lastEndTime)) {
                continue;
            } else {
            	renterOrderThenList.add(curRenterOrderEntity);
            	lastRenterOrderEntity = curRenterOrderEntity;
            }
		}
		// 获取租客价格列表
		List<RenterGoodsPriiceDetailEntity> renterGoodsPriceAllList = renterGoodsPriiceDetailService.listRenterGoodsPriceByOrderNo(orderNo);
		List<RenterGoodsPriceBO> renterGoodsPriceBOList = renterOrderList.stream().map(renterOrder -> getRenterOrderPrice(renterOrder,renterGoodsPriceAllList)).collect(Collectors.toList());
		return renterGoodsPriceBOList;
	}
	
	public RenterGoodsPriceBO getRenterOrderPrice(RenterOrderEntity renterOrder, List<RenterGoodsPriiceDetailEntity> renterGoodsPriceAllList) {
		RenterGoodsPriceBO renterGoodsPriceBO = new RenterGoodsPriceBO();
		renterGoodsPriceBO.setExpRentStartTime(renterOrder.getExpRentStartTime());
		renterGoodsPriceBO.setExpRentEndTime(renterOrder.getExpRentEndTime());
		renterGoodsPriceBO.setOrderNo(renterOrder.getOrderNo());
		renterGoodsPriceBO.setRenterOrderNo(renterOrder.getRenterOrderNo());
		return renterGoodsPriceBO;
	}
}
