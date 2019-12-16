package com.atzuche.order.accountdebt.mapper;

import com.atzuche.order.accountdebt.entity.AccountDebtEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 个人历史总额表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Mapper
public interface AccountDebtMapper{

    AccountDebtEntity selectByPrimaryKey(Integer id);

    int insert(AccountDebtEntity record);
    
    int insertSelective(AccountDebtEntity record);

    int updateByPrimaryKeySelective(AccountDebtEntity record);

    /**根据会员号查询个人总欠款信息
     * @param memNo
     * @return
     */
    AccountDebtEntity getAccountDebtByMemNo(@Param("memNo") Integer memNo);
}
