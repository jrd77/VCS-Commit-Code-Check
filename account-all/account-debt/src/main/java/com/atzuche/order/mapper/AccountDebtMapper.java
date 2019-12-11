package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountDebtEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 个人历史总额表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Mapper
public interface AccountDebtMapper{

    AccountDebtEntity selectByPrimaryKey(Integer id);

    List<AccountDebtEntity> selectALL();

    int insert(AccountDebtEntity record);
    
    int insertSelective(AccountDebtEntity record);

    int updateByPrimaryKey(AccountDebtEntity record);
    
    int updateByPrimaryKeySelective(AccountDebtEntity record);

}
