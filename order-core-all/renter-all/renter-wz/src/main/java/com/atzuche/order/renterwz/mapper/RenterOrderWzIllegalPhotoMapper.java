package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzIllegalPhotoEntity;

import java.util.List;

/**
 * RenterOrderWzIllegalPhotoMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenterOrderWzIllegalPhotoMapper{

	/**
	 * 保存
	 * @param renterOrderWzIllegalPhoto 保存信息
	 * @return 成功条数
	 */
	Integer saveRenterOrderWzIllegalPhoto(RenterOrderWzIllegalPhotoEntity renterOrderWzIllegalPhoto);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenterOrderWzIllegalPhotoEntity queryRenterOrderWzIllegalPhotoById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenterOrderWzIllegalPhotoEntity> queryList();

	/**
	 * 修改
	 * @param renterOrderWzIllegalPhoto 修改实体
	 * @return 成功条数
	 */
	Integer updateRenterOrderWzIllegalPhoto(RenterOrderWzIllegalPhotoEntity renterOrderWzIllegalPhoto);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenterOrderWzIllegalPhotoById(@Param("id") Long id);

	/**
	 * 获取最大的序列号
	 * @param orderNo 订单号
	 * @param userType 操作类型
	 * @param carPlateNum 车牌号
	 * @return 序列号
	 */
	Integer getMaxSerialNum(@Param("orderNo") String orderNo,@Param("userType") Integer userType,@Param("carPlateNum") String carPlateNum);

	/**
	 * 查询数量
	 * @param orderNo 订单号
	 * @param img 图品地址
	 * @param carPlateNum 车牌号
	 * @return 数量
	 */
	Integer countPhoto(@Param("orderNo")String orderNo,@Param("path") String img,@Param("carPlateNum") String carPlateNum);
}
