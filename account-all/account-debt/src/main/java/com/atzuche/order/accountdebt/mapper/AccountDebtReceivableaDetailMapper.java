package com.atzuche.order.accountdebt.mapper;

import com.atzuche.order.accountdebt.entity.AccountDebtReceivableaDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 个人历史欠款收款记录
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Mapper
public interface AccountDebtReceivableaDetailMapper{

    AccountDebtReceivableaDetailEntity selectByPrimaryKey(Integer id);

    int insert(AccountDebtReceivableaDetailEntity record);
    
    AccountDebtReceivableaDetailEntity selectByUniqueAndSourceCode(@Param("sourceCode") Integer sourceCode, @Param("uniqueNo") String uniqueNo);
}
