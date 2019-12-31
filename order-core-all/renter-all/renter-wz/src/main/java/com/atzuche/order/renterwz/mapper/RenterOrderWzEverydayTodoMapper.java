package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzEverydayTodoEntity;

import java.util.List;

/**
 * RenterOrderWzEverydayTodoMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzEverydayTodoMapper{

	/**
	 * 保存
	 * @param renterOrderWzEverydayTodo 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzEverydayTodo(RenterOrderWzEverydayTodoEntity renterOrderWzEverydayTodo);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzEverydayTodoEntity queryRenterOrderWzEverydayTodoById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzEverydayTodoEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzEverydayTodo 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzEverydayTodo(RenterOrderWzEverydayTodoEntity renterOrderWzEverydayTodo);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzEverydayTodoById(@Param("id") Long id);
}
