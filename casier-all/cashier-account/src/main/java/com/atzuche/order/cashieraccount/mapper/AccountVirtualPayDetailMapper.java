package com.atzuche.order.cashieraccount.mapper;


import com.atzuche.order.cashieraccount.entity.AccountVirtualPayDetailEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountVirtualPayDetailMapper {
    int deleteByPrimaryKey(Integer id);


    int insertSelective(AccountVirtualPayDetailEntity record);

    AccountVirtualPayDetailEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountVirtualPayDetailEntity record);


}