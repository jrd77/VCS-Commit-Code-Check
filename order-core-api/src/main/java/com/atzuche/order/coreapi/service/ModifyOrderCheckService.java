package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderDataNoChangeException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderGoodNotExistException;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParentOrderNotFindException;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModifyOrderCheckService {

	public void modifyMainCheck(ModifyOrderDTO modifyOrderDTO) {
		OrderEntity orderEntity = modifyOrderDTO.getOrderEntity();
		checkParentOrder(orderEntity);
	}
	
	public void checkParentOrder(OrderEntity orderEntity) {
		if (orderEntity == null) {
			Cat.logError("ModifyOrderCheckService.checkParentOrder校验主订单", new ModifyOrderParentOrderNotFindException());
			throw new ModifyOrderParentOrderNotFindException();
		}
	}
	
	public void checkDataChange(List<OrderChangeItemDTO> changeItemList) {
		if (changeItemList == null || changeItemList.isEmpty()) {
			Cat.logError("ModifyOrderCheckService.checkDataChange校验是否修改数据", new ModifyOrderDataNoChangeException());
			throw new ModifyOrderDataNoChangeException();
		}
	}
	
	public void checkGoods(RenterGoodsDetailDTO renterGoodsDetailDTO) {
		if (renterGoodsDetailDTO == null) {
			Cat.logError("ModifyOrderCheckService.checkGoods校验商品信息", new ModifyOrderGoodNotExistException());
			throw new ModifyOrderGoodNotExistException();
		}
		List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
		if (renterGoodsPriceDetailDTOList == null || renterGoodsPriceDetailDTOList.isEmpty()) {
			Cat.logError("ModifyOrderCheckService.checkGoods校验商品价格信息", new ModifyOrderGoodNotExistException());
			throw new ModifyOrderGoodNotExistException();
		}
	}
}
