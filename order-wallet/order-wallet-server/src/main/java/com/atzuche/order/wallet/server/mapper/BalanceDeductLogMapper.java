package com.atzuche.order.wallet.server.mapper;


import com.atzuche.order.wallet.server.entity.BalanceDeductLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BalanceDeductLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BalanceDeductLogEntity record);

    int insertSelective(BalanceDeductLogEntity record);

    BalanceDeductLogEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BalanceDeductLogEntity record);

    int updateByPrimaryKey(BalanceDeductLogEntity record);
}