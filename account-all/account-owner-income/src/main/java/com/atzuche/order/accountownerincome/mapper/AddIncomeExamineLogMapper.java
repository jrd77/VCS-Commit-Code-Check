package com.atzuche.order.accountownerincome.mapper;

import java.util.List;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamineLog;

public interface AddIncomeExamineLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AddIncomeExamineLog record);

    int insertSelective(AddIncomeExamineLog record);

    AddIncomeExamineLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AddIncomeExamineLog record);

    int updateByPrimaryKey(AddIncomeExamineLog record);
    
    List<AddIncomeExamineLog> listAddIncomeExamineLog(Integer addIncomeExamineId);
}