package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzSettleFlagEntity;

import java.util.List;

/**
 * RenterOrderWzSettleFlagMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzSettleFlagMapper{

	/**
	 * 保存
	 * @param renterOrderWzSettleFlag 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzSettleFlag(RenterOrderWzSettleFlagEntity renterOrderWzSettleFlag);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzSettleFlagEntity queryRenterOrderWzSettleFlagById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzSettleFlagEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzSettleFlag 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzSettleFlag(RenterOrderWzSettleFlagEntity renterOrderWzSettleFlag);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzSettleFlagById(@Param("id") Long id);

	/**
	 * 根据订单号和车牌号修改数据
	 * @param orderNo 订单号
	 * @param carNum 车牌号
	 * @param hasIllegal 有无违章
	 * @param updateOp 修改人
	 */
	void updateIsIllegal(@Param("orderNo") String orderNo,@Param("carNum") String carNum,@Param("hasIllegal") int hasIllegal,@Param("updateOp") String updateOp);

	/**
	 * 查询存在的数量
	 * @param orderNo 订单号
	 * @param carNum 车牌号
	 * @return 存在的数量
	 */
	Integer countByOrderNoAndCarNum(@Param("orderNo") String orderNo,@Param("carNum") String carNum);

	/**
	 * 获取结算状态
	 * @param orderNo 订单号
	 * @param carNum 车牌号
	 * @return 结算状态
	 */
    Integer getIllegalSettleFlag(@Param("orderNo") String orderNo,@Param("carNum") String carNum);

	/**
	 * 根据订单号和车牌号修改数据
	 * @param orderNo 订单号
	 * @param hasIllegalCost 是否有违章扣款金额
	 * @param updateOp 修改人
	 * @param carNum 车牌号
	 */
    void updateIsIllegalCost(@Param("orderNo") String orderNo,@Param("hasIllegalCost") int hasIllegalCost,@Param("updateOp") String updateOp,@Param("carNum") String carNum);
}
