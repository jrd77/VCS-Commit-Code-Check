package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountOwnerIncomeDetailEntity;
import org.apache.ibatis.annotations.Mapper;
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

    List<AccountOwnerIncomeDetailEntity> selectALL();

    int insert(AccountOwnerIncomeDetailEntity record);
    
    int insertSelective(AccountOwnerIncomeDetailEntity record);

    int updateByPrimaryKey(AccountOwnerIncomeDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountOwnerIncomeDetailEntity record);

}
