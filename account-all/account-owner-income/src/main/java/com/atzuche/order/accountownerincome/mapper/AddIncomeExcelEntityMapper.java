package com.atzuche.order.accountownerincome.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.atzuche.order.accountownerincome.entity.AddIncomeExcelEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelQueryDTO;
@Mapper
public interface AddIncomeExcelEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AddIncomeExcelEntity record);

    int insertSelective(AddIncomeExcelEntity record);

    AddIncomeExcelEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AddIncomeExcelEntity record);

    int updateByPrimaryKey(AddIncomeExcelEntity record);
    
    int queryListCount(AddIncomeExcelConsoleDTO addIncomeExcelConsoleDTO);
    
    List<AddIncomeExcelEntity> queryList(AddIncomeExcelQueryDTO addIncomeExcelQueryDTO);
}