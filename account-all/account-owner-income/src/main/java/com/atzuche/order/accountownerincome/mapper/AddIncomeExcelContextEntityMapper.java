package com.atzuche.order.accountownerincome.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.accountownerincome.entity.AddIncomeExcelContextEntity;
@Mapper
public interface AddIncomeExcelContextEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AddIncomeExcelContextEntity record);

    int insertSelective(AddIncomeExcelContextEntity record);

    AddIncomeExcelContextEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AddIncomeExcelContextEntity record);

    int updateByPrimaryKeyWithBLOBs(AddIncomeExcelContextEntity record);

    int updateByPrimaryKey(AddIncomeExcelContextEntity record);
    
    int saveAddIncomeExcelContextBatch(List<AddIncomeExcelContextEntity> list);
    
    List<AddIncomeExcelContextEntity> listAddIncomeExcelContextByAddId(Long addId);
}