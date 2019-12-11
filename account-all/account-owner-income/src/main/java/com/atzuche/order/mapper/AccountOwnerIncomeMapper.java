package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountOwnerIncomeEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主收益总表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Mapper
public interface AccountOwnerIncomeMapper{

    AccountOwnerIncomeEntity selectByPrimaryKey(Integer id);

    List<AccountOwnerIncomeEntity> selectALL();

    int insert(AccountOwnerIncomeEntity record);
    
    int insertSelective(AccountOwnerIncomeEntity record);

    int updateByPrimaryKey(AccountOwnerIncomeEntity record);
    
    int updateByPrimaryKeySelective(AccountOwnerIncomeEntity record);

}
