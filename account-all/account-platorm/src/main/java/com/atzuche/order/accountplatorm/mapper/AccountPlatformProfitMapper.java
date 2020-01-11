package com.atzuche.order.accountplatorm.mapper;

import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *  平台订单收益结算表
 * 
 * @author ZhangBin
 * @date 2019-12-21 14:38:34
 */
@Mapper
public interface AccountPlatformProfitMapper{

    AccountPlatformProfitEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountPlatformProfitEntity record);

    int updateByPrimaryKeySelective(AccountPlatformProfitEntity record);

    AccountPlatformProfitEntity getAccountPlatformProfit(@Param("orderNo") String orderNo);
}
