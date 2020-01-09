package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.DerenCarApproachCitysEntity;

import java.util.List;

/**
 * DerenCarApproachCitysMapper
 *
 * @author shisong
 * @date 2020/01/08
 */
@Mapper
public interface DerenCarApproachCitysMapper{

	/**
	 * 保存
	 * @param derenCarApproachCitys 保存信息
	 * @return 成功条数
	 */
	Integer saveDerenCarApproachCitys(DerenCarApproachCitysEntity derenCarApproachCitys);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	DerenCarApproachCitysEntity queryDerenCarApproachCitysById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<DerenCarApproachCitysEntity> queryList();

	/**
	 * 修改
	 * @param derenCarApproachCitys 修改实体
	 * @return 成功条数
	 */
	Integer updateDerenCarApproachCitys(DerenCarApproachCitysEntity derenCarApproachCitys);

	/**
	 * 根据车牌号和订单号 查询途经城市
	 * @param orderNo 订单号
	 * @param plateNum 车牌号
	 * @return 途经城市
	 */
	String queryCitiesByOrderNoAndCarNum(@Param("orderNo") String orderNo,@Param("plateNum") String plateNum);
}
