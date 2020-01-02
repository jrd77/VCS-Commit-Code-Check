package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzStatusEntity;

/**
 * RenterOrderWzStatusMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzStatusMapper{

	/**
	 * 保存
	 * @param renterOrderWzStatus 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzStatus(RenterOrderWzStatusEntity renterOrderWzStatus);

	/**
	 * 修改违章查询状态
	 * @param illegalQuery 查询状态
	 * @param orderNo 订单号
	 * @param carNum 车牌号
	 */
    void updateTransIllegalQuery(@Param("illegalQuery") Integer illegalQuery,@Param("orderNo") String orderNo,@Param("carNum") String carNum);

	/**
	 * 修改 违章的状态
	 * @param status 状态
	 * @param orderNo 订单号
	 * @param carNum 车牌
	 * @param statusDesc
	 */
	void updateTransIllegalStatus(@Param("status") Integer status,@Param("orderNo") String orderNo,@Param("carNum") String carNum,@Param("statusDesc") String statusDesc);

	/**
	 * 修改操作平台
	 * @param managementMode 操作平台
	 * @param orderNo 订单号
	 * @param carNum 车牌号
	 */
    void updateIllegalHandle(@Param("managementMode") Integer managementMode,@Param("orderNo") String orderNo,@Param("carNum") String carNum);

	/**
	 * 查询详情
	 * @param orderNo 订单号
	 * @param carNum 车牌号
	 * @return 详情
	 */
	RenterOrderWzStatusEntity selectByOrderNo(@Param("orderNo") String orderNo,@Param("carNum") String carNum);

	/**
	 * 修改处理状态
	 * @param orderNo 订单号
	 * @param status 处理状态
	 * @param carNum 车牌号
	 * @param statusDesc 描述信息
	 */
	void updateStatusByOrderNoAndCarNum(@Param("orderNo") String orderNo,@Param("status") Integer status,@Param("carNum") String carNum,@Param("statusDesc") String statusDesc);

	/**
	 * 查询 违章状态
	 * @param orderNo 订单号
	 * @param plateNum 车牌号
	 * @return 违章状态
	 */
	Integer getTransWzDisposeStatusByOrderNo(@Param("orderNo") String orderNo,@Param("plateNum") String plateNum);

	/**
	 * 根据订单号和车牌号 修改违章状态
	 * @param orderNo 订单号
	 * @param carNumber 车牌号
	 * @param wzDisposeStatus 状态
	 */
	void updateTransWzDisposeStatus(@Param("orderNo") String orderNo,@Param("carNumber") String carNumber,@Param("status") int wzDisposeStatus);
}
