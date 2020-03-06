package com.atzuche.order.cashieraccount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;

public interface AccountOwnerCashExamineMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AccountOwnerCashExamine record);

    int insertSelective(AccountOwnerCashExamine record);

    AccountOwnerCashExamine selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountOwnerCashExamine record);

    int updateByPrimaryKey(AccountOwnerCashExamine record);
    
    List<AccountOwnerCashExamine> listAccountOwnerCashExamineByMemNo(@Param("memNo") Integer memNo);
    
    Integer getCountByMemNoAndDateTime(@Param("memNo")String memNo,@Param("date")String date);
}