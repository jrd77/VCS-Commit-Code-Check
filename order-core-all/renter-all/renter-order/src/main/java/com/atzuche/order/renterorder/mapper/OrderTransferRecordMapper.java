package com.atzuche.order.renterorder.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterorder.entity.OrderTransferRecordEntity;

@Mapper
public interface OrderTransferRecordMapper {

	Integer saveOrderTransferRecord(OrderTransferRecordEntity orderTransferRecordEntity);
	
	List<OrderTransferRecordEntity> listOrderTransferRecordByOrderNo(@Param("orderNo") String orderNo);
}
