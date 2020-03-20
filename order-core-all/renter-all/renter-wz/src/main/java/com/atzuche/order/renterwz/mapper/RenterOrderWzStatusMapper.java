package com.atzuche.order.renterwz.mapper;

import com.atzuche.order.commons.vo.req.ViolationReqVO;
import com.atzuche.order.commons.vo.res.ViolationResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenterOrderWzStatusEntity;

import java.util.List;

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
    void updateTransIllegalQuery(@Param("status") Integer illegalQuery,@Param("orderNo") String orderNo,@Param("carNum") String carNum);

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

	/**
	 * 根据订单号查询 违章处理状态信息
	 * @param orderNo 订单号
	 * @return 违章处理状态信息列表
	 */
    List<RenterOrderWzStatusEntity> queryInfosByOrderNo(@Param("orderNo") String orderNo);

	/**
	 * 删除之前的数据
	 * @param orderNo 订单号
	 * @param operator 操作人
	 */
	void deleteInfoByOrderNo(@Param("orderNo") String orderNo,@Param("operator") String operator);

	/**
	 * 查询违章的订单
	 * @param memNo 会员号
	 * @return 违章订单
	 */
    List<RenterOrderWzStatusEntity> queryIllegalOrderListByMemNo(@Param("memNo") String memNo);

	/**
	 * 查询当前订单的违章详情
	 * @param orderNo
	 * @return
	 */
	RenterOrderWzStatusEntity getOrderInfoByOrderNo(@Param("orderNo") String orderNo);

	/**
	 * 修改违章信息
	 * @param renterOrderWzStatusEntity
	 */
	void updateOrderWzStatus(RenterOrderWzStatusEntity renterOrderWzStatusEntity);
	/**
	 * 查询违章列表数据
	 * @return 违章订单
	 */
	List<ViolationResVO> queryIllegalOrderList(ViolationReqVO violationReqVO);
}
