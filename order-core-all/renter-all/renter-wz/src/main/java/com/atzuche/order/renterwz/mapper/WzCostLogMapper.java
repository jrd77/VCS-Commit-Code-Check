package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.WzCostLogEntity;

import java.util.List;

/**
 * WzCostLogMapper
 *
 * @author shisong
 * @date 2020/01/06
 */
@Mapper
public interface WzCostLogMapper{

	/**
	 * 保存
	 * @param wzCostLog 保存信息
	 * @return 成功条数
	 */
	Integer saveWzCostLog(WzCostLogEntity wzCostLog);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	WzCostLogEntity queryWzCostLogById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<WzCostLogEntity> queryList();
}
