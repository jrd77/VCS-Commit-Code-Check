package com.atzuche.order.accountownerincome.mapper;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 车主收益总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Mapper
public interface AccountOwnerIncomeMapper{

    AccountOwnerIncomeEntity selectByPrimaryKey(Integer id);
    int insertSelective(AccountOwnerIncomeEntity record);
    int updateByPrimaryKey(AccountOwnerIncomeEntity record);
    
    /**
     * 根据车主会员号查询车主收益信息
     * @param memNo
     * @return
     */
    AccountOwnerIncomeEntity selectByMemNo(@Param("memNo") String memNo);
}
