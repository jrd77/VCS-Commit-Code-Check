package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.MqSendFeelbackLogEntity;

import java.util.List;

/**
 * MqSendFeelbackLogMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface MqSendFeelbackLogMapper{

	/**
	 * 保存
	 * @param mqSendFeelbackLog 保存信息
	 * @return 成功条数
	 */
	Integer saveMqSendFeelbackLog(MqSendFeelbackLogEntity mqSendFeelbackLog);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	MqSendFeelbackLogEntity queryMqSendFeelbackLogById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<MqSendFeelbackLogEntity> queryList();

	/**
	 * 修改
	 * @param mqSendFeelbackLog 修改实体
	 * @return 成功条数
	 */
	Integer updateMqSendFeelbackLog(MqSendFeelbackLogEntity mqSendFeelbackLog);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteMqSendFeelbackLogById(@Param("id") Long id);
}
