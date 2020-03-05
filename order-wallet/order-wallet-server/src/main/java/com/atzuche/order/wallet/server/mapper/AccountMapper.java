package com.atzuche.order.wallet.server.mapper;


import com.atzuche.order.wallet.server.entity.AccountEntity;

import java.util.List;

public interface AccountMapper {

    List<AccountEntity> findByMemNo(String memNo);

    int insert(AccountEntity record);

    int insertSelective(AccountEntity record);

    AccountEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountEntity record);

    int updateByPrimaryKey(AccountEntity record);
}