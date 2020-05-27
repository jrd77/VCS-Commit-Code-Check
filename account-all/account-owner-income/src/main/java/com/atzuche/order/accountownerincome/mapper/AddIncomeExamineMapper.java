package com.atzuche.order.accountownerincome.mapper;

import java.util.List;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;

public interface AddIncomeExamineMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AddIncomeExamine record);

    int insertSelective(AddIncomeExamine record);

    AddIncomeExamine selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AddIncomeExamine record);

    int updateByPrimaryKey(AddIncomeExamine record);
    
    int saveAddIncomeExamineBatch(List<AddIncomeExamine> list);
    
    int getCountByAddIdAndStatus(Long addId);
    
    int delAddIncomeExamineByAddId(Long addId);
}