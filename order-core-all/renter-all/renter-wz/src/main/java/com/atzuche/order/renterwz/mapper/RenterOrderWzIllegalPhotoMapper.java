package com.atzuche.order.renterwz.mapper;

import com.atzuche.order.renterwz.vo.PhotoPath;
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

	/**
	 * 根据条件 查询数量
	 * @param orderNo 订单号
	 * @param userType 操作类型
	 * @param carPlateNum 车牌号
	 * @return 总数
	 */
    Integer countIllegalPhoto(@Param("orderNo") String orderNo,@Param("userType") Integer userType,@Param("carPlateNum") String carPlateNum);

	/**
	 * 查询dto
	 * @param orderNo 订单号
	 * @param userType 操作类型
	 * @param serialNumber 序列号
	 * @param carNum 车牌号
	 * @return 返回实体
	 */
	RenterOrderWzIllegalPhotoEntity getIllegalPhotoBy(@Param("orderNo") String orderNo,@Param("userType") int userType,@Param("serialNumber") int serialNumber,@Param("carNum") String carNum);

	/**
	 * 按条件 修改信息
	 * @param photo 实体
	 * @return 修改成功的数量
	 */
	int update(RenterOrderWzIllegalPhotoEntity photo);

	/**
	 * 根据条件查询
	 * @param orderNo 订单号
	 * @param carPlateNum 车牌号
	 * @return 列表
	 */
    List<PhotoPath> queryIllegalPhotoByOrderNo(@Param("orderNo") String orderNo,@Param("carPlateNum")  String carPlateNum);
}
