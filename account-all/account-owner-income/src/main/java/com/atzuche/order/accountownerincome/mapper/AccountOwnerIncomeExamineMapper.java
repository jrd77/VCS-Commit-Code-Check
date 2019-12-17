package com.atzuche.order.accountownerincome.mapper;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车主收益待审核表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Mapper
public interface AccountOwnerIncomeExamineMapper{

    AccountOwnerIncomeExamineEntity selectByPrimaryKey(Integer id);
    int insert(AccountOwnerIncomeExamineEntity record);
    int updateByPrimaryKeySelective(AccountOwnerIncomeExamineEntity record);

}
