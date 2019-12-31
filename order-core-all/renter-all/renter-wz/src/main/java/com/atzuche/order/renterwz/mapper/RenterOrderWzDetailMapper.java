package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;

import java.util.List;

/**
 * RenterOrderWzDetailMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzDetailMapper{

	/**
	 * 保存
	 * @param renterOrderWzDetail 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzDetail(RenterOrderWzDetailEntity renterOrderWzDetail);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzDetailEntity queryRenterOrderWzDetailById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzDetailEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzDetail 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzDetail(RenterOrderWzDetailEntity renterOrderWzDetail);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzDetailById(@Param("id") Long id);

	/**
	 * 将之前的数据设为无效
	 * @param orderNo 订单号
	 * @param carNum
	 * @return 修改数量
	 */
	Integer updateIsValid(@Param("orderNo") String orderNo, @Param("carNum") String carNum);

	/**
	 * 保存从任云那边传递的数据
	 * @param illegal 违章实体
	 */
    void addIllegalDetailFromRenyun(RenterOrderWzDetailEntity illegal);

	/**
	 * 修改违章报价
	 * @param illegal 违章实体
	 * @return 修改成功的数量
	 */
	int updateFeeByWzCode(RenterOrderWzDetailEntity illegal);
}
