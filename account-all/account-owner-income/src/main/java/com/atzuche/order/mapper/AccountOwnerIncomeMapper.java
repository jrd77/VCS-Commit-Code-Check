package com.atzuche.order.mapper;

import com.atzuche.order.entity.AccountOwnerIncomeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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


    int insert(AccountOwnerIncomeEntity record);
    

    int updateByPrimaryKey(AccountOwnerIncomeEntity record);
    
    /**
     * 根据车主会员号查询车主收益信息
     * @param memNo
     * @return
     */
    AccountOwnerIncomeEntity selectByMemNo(@Param("memNo") Integer memNo);
}
