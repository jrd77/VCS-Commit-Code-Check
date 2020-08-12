package com.atzuche.order.accountownerincome.mapper;

import java.util.List;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineQueryDTO;

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
    
    List<AddIncomeExamine> queryAddIncomeExamine(AddIncomeExamineQueryDTO addIncomeExamineQueryDTO);
    
    List<AddIncomeExamine> listAllAddIncomeExamine(AddIncomeExamineDTO addIncomeExamineDTO);
    
    int queryCount(AddIncomeExamineDTO addIncomeExamineDTO);
    
    int updateAddIncomeExaminePass(AddIncomeExamine record);

    /**
     * 查询会员追加收益明细列表
     *
     * @param memNo 会员号
     * @return List<AddIncomeExamine>
     */
    List<AddIncomeExamine> selectByMemNo(String memNo);
}