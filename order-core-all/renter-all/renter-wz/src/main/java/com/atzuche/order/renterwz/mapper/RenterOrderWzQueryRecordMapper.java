package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzQueryRecordEntity;

import java.util.List;

/**
 * RenterOrderWzQueryRecordMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzQueryRecordMapper{

	/**
	 * 保存
	 * @param renterOrderWzQueryRecord 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzQueryRecord(RenterOrderWzQueryRecordEntity renterOrderWzQueryRecord);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzQueryRecordEntity queryRenterOrderWzQueryRecordById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzQueryRecordEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzQueryRecord 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzQueryRecord(RenterOrderWzQueryRecordEntity renterOrderWzQueryRecord);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzQueryRecordById(@Param("id") Long id);
}
