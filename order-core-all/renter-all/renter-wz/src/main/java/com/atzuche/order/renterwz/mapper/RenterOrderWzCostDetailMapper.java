package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;

import java.util.List;

/**
 * RenterOrderWzCostDetailMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzCostDetailMapper{

	/**
	 * 保存
	 * @param renterOrderWzCostDetail 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzCostDetail(RenterOrderWzCostDetailEntity renterOrderWzCostDetail);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzCostDetailEntity queryRenterOrderWzCostDetailById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @param orderNo 订单号
	 * @return 查询列表
	 */
	List<RenterOrderWzCostDetailEntity> queryInfosByOrderNo(@Param("orderNo") String orderNo);
	/**
	 * 明细
	 * @param orderNo
	 * @return
	 */
	List<RenterOrderWzCostDetailEntity> queryInfosUnitByOrderNo(@Param("orderNo") String orderNo);

	/**
	 * 修改
	 * @param renterOrderWzCostDetail 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzCostDetail(RenterOrderWzCostDetailEntity renterOrderWzCostDetail);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzCostDetailById(@Param("id") Long id);

	/**
	 * 将上一笔设为无效
	 *  @param orderNo 订单号
	 * @param carNum 车牌号
	 * @param memNo 会员号
	 * @param costStatus 0-正常，1-失效,管理后台修改费用时会造成同类费用上一笔失效
	 * @param code 费用编码
	 */
	void updateCostStatusByOrderNoAndCarNumAndMemNoAndCostCode(@Param("orderNo") String orderNo,@Param("carNum") String carNum,@Param("memNo")  Integer memNo,@Param("costStatus")  Integer costStatus,@Param("code")  String code);

	/**
	 *  根据消费code和订单号 查询 详情
	 * @param orderNo 订单号
	 * @param costCode 消费code
	 * @return 详情
	 */
	RenterOrderWzCostDetailEntity queryInfoByOrderAndCode(@Param("orderNo") String orderNo, @Param("costCode")String costCode);

	/**
	 * 查询费用信息
	 * @param orderNo 订单号
	 * @param costCode 费用编码
	 * @return 费用信息
	 */
	RenterOrderWzCostDetailEntity queryInfoWithSumAmountByOrderAndCode(@Param("orderNo") String orderNo,@Param("costCode") String costCode);
}