package com.atzuche.order.wallet.server.mapper;


import com.atzuche.order.wallet.server.entity.WithdrawalLogEntity;

public interface WithdrawalLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WithdrawalLogEntity record);

    int insertSelective(WithdrawalLogEntity record);

    WithdrawalLogEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WithdrawalLogEntity record);

    int updateByPrimaryKey(WithdrawalLogEntity record);
}