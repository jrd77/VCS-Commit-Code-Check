package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountDebtDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 个人历史欠款明细
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Mapper
public interface AccountDebtDetailMapper{

    AccountDebtDetailEntity selectByPrimaryKey(Integer id);

    List<AccountDebtDetailEntity> selectALL();

    int insert(AccountDebtDetailEntity record);
    
    int insertSelective(AccountDebtDetailEntity record);

    int updateByPrimaryKey(AccountDebtDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountDebtDetailEntity record);

}
