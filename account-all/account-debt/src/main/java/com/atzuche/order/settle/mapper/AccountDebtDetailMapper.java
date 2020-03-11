package com.atzuche.order.settle.mapper;

import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    int insert(AccountDebtDetailEntity record);
    
    int updateByPrimaryKeySelective(AccountDebtDetailEntity record);

    List<AccountDebtDetailEntity> getDebtListByMemNo(@Param("memNo") String memNo);
    
    List<AccountDebtDetailEntity> listAccountDebtDetailEntity(@Param("orderNo") String orderNo,@Param("memNo") String memNo);
    
    List<AccountDebtDetailEntity> getDebtListByOrderNo(@Param("orderNo") String orderNo,@Param("memNo") String memNo);
    
}
