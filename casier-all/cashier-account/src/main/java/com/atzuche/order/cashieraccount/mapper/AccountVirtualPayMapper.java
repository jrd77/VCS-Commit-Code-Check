package com.atzuche.order.cashieraccount.mapper;


import com.atzuche.order.cashieraccount.entity.AccountVirtualPayEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountVirtualPayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AccountVirtualPayEntity record);

    int insertSelective(AccountVirtualPayEntity record);

    AccountVirtualPayEntity selectByPrimaryKey(Integer id);

    AccountVirtualPayEntity selectByAccountNo(String accountNo);

    int updateByPrimaryKeySelective(AccountVirtualPayEntity record);

    int updateByPrimaryKey(AccountVirtualPayEntity record);

    int deductAmt(@Param("accountNo") String accountNo,@Param("payAmt") Integer payAmt);
}