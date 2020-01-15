package com.atzuche.order.renterwz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atzuche.order.renterwz.entity.IllegalAppealEntity;

import java.util.List;

/**
 * IllegalAppealMapper
 *
 * @author shisong
 * @date 2020/01/15
 */
@Mapper
public interface IllegalAppealMapper{

	/**
	 * 保存
	 * @param illegalAppeal 保存信息
	 * @return 成功条数
	 */
	Integer saveIllegalAppeal(IllegalAppealEntity illegalAppeal);

	/**
	 * 获取当天已存在的申诉记录数
	 * @param orderNo
	 * @param illegalNum
	 * @return
	 */
    Integer getIllegalAppealCount(String orderNo, String illegalNum);
}
