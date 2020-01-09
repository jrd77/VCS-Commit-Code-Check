package com.atzuche.order.settle.mapper;

import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 个人历史欠款收款记录
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Mapper
public interface AccountDebtReceivableaDetailMapper{

    AccountDebtReceivableaDetailEntity selectByPrimaryKey(Integer id);

    int insertSelective(AccountDebtReceivableaDetailEntity record);
    
    AccountDebtReceivableaDetailEntity selectByUniqueAndSourceCode(@Param("sourceCode") Integer sourceCode, @Param("uniqueNo") String uniqueNo);
}
