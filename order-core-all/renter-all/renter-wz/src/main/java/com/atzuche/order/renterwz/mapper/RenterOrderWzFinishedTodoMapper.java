package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzFinishedTodoEntity;

import java.util.List;

/**
 * RenterOrderWzFinishedTodoMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzFinishedTodoMapper{

	/**
	 * 保存
	 * @param renterOrderWzFinishedTodo 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzFinishedTodo(RenterOrderWzFinishedTodoEntity renterOrderWzFinishedTodo);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzFinishedTodoEntity queryRenterOrderWzFinishedTodoById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzFinishedTodoEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzFinishedTodo 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzFinishedTodo(RenterOrderWzFinishedTodoEntity renterOrderWzFinishedTodo);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzFinishedTodoById(@Param("id") Long id);

	/**
	 * 根据主键修改状态
	 * @param id 主键
	 */
	void updateStatus(Integer id);
}
