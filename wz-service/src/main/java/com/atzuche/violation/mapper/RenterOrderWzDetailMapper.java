package com.atzuche.violation.mapper;

import com.atzuche.violation.entity.RenterOrderWzDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
	 * 变更违章为已处理状态
	 * @param orderNo 订单号
	 */
	void updateIllegalStatus(String orderNo);

}
