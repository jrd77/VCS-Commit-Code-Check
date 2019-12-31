package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.WzQueryDayConfEntity;

import java.util.List;

/**
 * WzQueryDayConfMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface WzQueryDayConfMapper{

	/**
	 * 保存
	 * @param wzQueryDayConf 保存信息
	 * @return 成功条数
	 */
	Integer saveWzQueryDayConf(WzQueryDayConfEntity wzQueryDayConf);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	WzQueryDayConfEntity queryWzQueryDayConfById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<WzQueryDayConfEntity> queryList();

	/**
	 * 修改
	 * @param wzQueryDayConf 修改实体
	 * @return 成功条数
	 */
	Integer updateWzQueryDayConf(WzQueryDayConfEntity wzQueryDayConf);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteWzQueryDayConfById(@Param("id") Long id);
}
