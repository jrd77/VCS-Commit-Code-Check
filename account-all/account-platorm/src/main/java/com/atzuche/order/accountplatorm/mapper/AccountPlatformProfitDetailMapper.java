package com.atzuche.order.accountplatorm.mapper;

import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台结算收益明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:45:24
 */
@Mapper
public interface AccountPlatformProfitDetailMapper{

    AccountPlatformProfitDetailEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountPlatformProfitDetailEntity record);

    int updateByPrimaryKey(AccountPlatformProfitDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountPlatformProfitDetailEntity record);

    List<AccountPlatformProfitDetailEntity> getPlatformProfitDetails(@Param("orderNo") String orderNo);
}
