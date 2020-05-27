package com.atzuche.order.accountownerincome.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamineVO;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExamineMapper;
import com.atzuche.order.commons.PageBean;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineQueryDTO;

@Service
public class AddIncomeExamineService {
	
	@Autowired
	private AddIncomeExamineMapper addIncomeExamineMapper;

	/**
	 * 获取追加收益审核列表
	 * @param aie
	 * @return AddIncomeExcelVO
	 */
	public AddIncomeExamineVO getAddIncomeExamineVO(AddIncomeExamineDTO req) {
		long pageNumber = 1L;
		long pageSize = PageBean.PAGE_SIZE;
		if (req != null) {
			pageNumber = StringUtils.isBlank(req.getPageNumber()) ? 1L:Long.valueOf(req.getPageNumber());
			pageSize = StringUtils.isBlank(req.getPageSize()) ? PageBean.PAGE_SIZE:Long.valueOf(req.getPageSize());
		}
		int totalCount = addIncomeExamineMapper.queryCount(req);
		AddIncomeExamineQueryDTO queryDTO = new AddIncomeExamineQueryDTO(pageNumber, totalCount, pageSize);
		if (req != null) {
			BeanUtils.copyProperties(req, queryDTO);
		}
		List<AddIncomeExamine> list = addIncomeExamineMapper.queryAddIncomeExamine(queryDTO);
		AddIncomeExamineVO addIncomeExamineVO = new AddIncomeExamineVO();
		addIncomeExamineVO.setTotalSize(totalCount);
		addIncomeExamineVO.setList(list);
		return addIncomeExamineVO;
	}

}
