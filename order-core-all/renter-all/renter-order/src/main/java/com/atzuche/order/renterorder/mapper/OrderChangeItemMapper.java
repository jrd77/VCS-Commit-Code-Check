package com.atzuche.order.renterorder.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;


@Mapper
public interface OrderChangeItemMapper{
	
	List<String> listChangeCodeByRenterOrderNo(@Param("renterOrderNo") String renterOrderNo);
    
    Integer saveOrderChangeItemBatch(@Param("changeItemList") List<OrderChangeItemDTO> changeItemList);

}
