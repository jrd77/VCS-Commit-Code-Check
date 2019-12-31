package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzRecordEntity;

import java.util.List;

/**
 * RenterOrderWzRecordMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzRecordMapper{

	/**
	 * 保存
	 * @param renterOrderWzRecord 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzRecord(RenterOrderWzRecordEntity renterOrderWzRecord);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzRecordEntity queryRenterOrderWzRecordById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzRecordEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzRecord 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzRecord(RenterOrderWzRecordEntity renterOrderWzRecord);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzRecordById(@Param("id") Long id);
}
