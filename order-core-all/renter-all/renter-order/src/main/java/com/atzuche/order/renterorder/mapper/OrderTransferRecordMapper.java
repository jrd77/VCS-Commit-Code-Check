package com.atzuche.order.renterorder.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.renterorder.entity.OrderTransferRecordEntity;

@Mapper
public interface OrderTransferRecordMapper {

	Integer saveOrderTransferRecord(OrderTransferRecordEntity orderTransferRecordEntity);
}
