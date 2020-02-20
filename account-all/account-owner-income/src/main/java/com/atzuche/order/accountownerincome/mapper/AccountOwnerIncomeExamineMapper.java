package com.atzuche.order.accountownerincome.mapper;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 车主收益待审核表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Mapper
public interface AccountOwnerIncomeExamineMapper{

    AccountOwnerIncomeExamineEntity selectByPrimaryKey(Integer id);
    int insertSelective(AccountOwnerIncomeExamineEntity record);
    int updateByPrimaryKeySelective(AccountOwnerIncomeExamineEntity record);
    /*根据订单号查询*/
    List<AccountOwnerIncomeExamineEntity> selectByOrderNo(String orderNo);
	int getTotalAccountOwnerIncomeExamineByOrderNo(String orderNo);

}
