package com.atzuche.order.wallet.server.mapper;


import com.atzuche.order.wallet.server.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface AccountMapper {

    AccountEntity getById(Integer id);

    List<AccountEntity> findByMemNo(String memNo);

    int insert(AccountEntity record);

    int insertSelective(AccountEntity record);

    AccountEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AccountEntity record);

    int updateByPrimaryKey(AccountEntity record);
}