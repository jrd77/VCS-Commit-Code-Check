package com.atzuche.order.wallet.server.mapper;

import com.atzuche.order.wallet.server.entity.TransSupplementDetailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransSupplementDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TransSupplementDetailEntity record);

    int insertSelective(TransSupplementDetailEntity record);

    TransSupplementDetailEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TransSupplementDetailEntity record);

    int updateByPrimaryKey(TransSupplementDetailEntity record);

    List<TransSupplementDetailEntity>  findDebtByMemNo(String memNo);

    int updatePayStatus(Integer id);
}