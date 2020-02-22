package com.atzuche.order.accountownerincome.mapper;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车主收益资金进出明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Mapper
public interface AccountOwnerIncomeDetailMapper{

    AccountOwnerIncomeDetailEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountOwnerIncomeDetailEntity record);
    
    List<AccountOwnerIncomeDetailEntity> selectByOrderNo(@Param("orderNo")String orderNo,@Param("memNo")String memNo);


}
