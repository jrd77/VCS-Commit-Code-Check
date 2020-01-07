package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.WzTemporaryRefundLogEntity;

import java.util.List;

/**
 * WzTemporaryRefundLogMapper
 *
 * @author shisong
 * @date 2020/01/06
 */
@Mapper
public interface WzTemporaryRefundLogMapper{

	/**
	 * 保存
	 * @param wzTemporaryRefundLog 保存信息
	 * @return 成功条数
	 */
	Integer saveWzTemporaryRefundLog(WzTemporaryRefundLogEntity wzTemporaryRefundLog);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	WzTemporaryRefundLogEntity queryWzTemporaryRefundLogById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @param orderNo 订单号
	 * @return 查询列表
	 */
	List<WzTemporaryRefundLogEntity> queryTemporaryRefundLogsByOrderNo(@Param("orderNo") String orderNo);

	/**
	 * 修改
	 * @param status 状态
	 * @param id 主键
	 * @return 成功条数
	 */
	Integer updateStatusById(@Param("status") Integer status ,@Param("id") Long id);
}
