package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.RenyunSendIllegalInfoLogEntity;

import java.util.List;

/**
 * RenyunSendIllegalInfoLogMapper
 *
 * @author shisong
 * @date 2019/12/28
 */
@Mapper
public interface RenyunSendIllegalInfoLogMapper{

	/**
	 * 保存
	 * @param renyunSendIllegalInfoLog 保存信息
	 * @return 成功条数
	 */
	Integer saveRenyunSendIllegalInfoLog(RenyunSendIllegalInfoLogEntity renyunSendIllegalInfoLog);

	/**
	 * 根据主键查询
	 * @param id 根据主键查询
	 * @return 返回查询到的实体
	 */
	RenyunSendIllegalInfoLogEntity queryRenyunSendIllegalInfoLogById(@Param("id") Long id);

	/**
	 * 批量查询
	 * @return 查询列表
	 */
	List<RenyunSendIllegalInfoLogEntity> queryList();

	/**
	 * 修改
	 * @param renyunSendIllegalInfoLog 修改实体
	 * @return 成功条数
	 */
	Integer updateRenyunSendIllegalInfoLog(RenyunSendIllegalInfoLogEntity renyunSendIllegalInfoLog);

	/**
	 * 根据主键删除
	 * @param id 主键
	 * @return 删除的数量
	 */
	Integer deleteRenyunSendIllegalInfoLogById(@Param("id") Long id);

	/**
	 * 按条件 查询数量
	 * @param wzCode 违章code
	 * @param dataType 01-违章信息，02-违章处理报价信息，03-仁云流程系统反馈租客凭证上传信息，04-仁云流程系统反馈订单车辆违章处理方信息
	 * @param carNum 车牌号
	 * @return
	 */
	int count(@Param("wzCode") String wzCode,@Param("dataType") String dataType,@Param("carNum") String carNum);
}
