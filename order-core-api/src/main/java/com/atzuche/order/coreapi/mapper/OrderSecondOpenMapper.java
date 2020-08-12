/**
 * 
 */
package com.atzuche.order.coreapi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.coreapi.entity.OrderSecondOpenEntity;

/**
 * @author jing.huang
 *
 */
@Mapper
public interface OrderSecondOpenMapper {
	//新增
	int insert(OrderSecondOpenEntity record);
	//查询
	List<OrderSecondOpenEntity> query();
	//修改flag
	int updateFlag(List<Integer> ids);
	
}
