package com.atzuche.order.accountownerincome.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamineLog;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamineVO;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExamineLogMapper;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExamineMapper;
import com.atzuche.order.commons.PageBean;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineQueryDTO;
import com.atzuche.order.commons.enums.ExamineStatusEnum;

@Service
public class AddIncomeExamineService {
	
	@Autowired
	private AddIncomeExamineMapper addIncomeExamineMapper;
	@Autowired
	private AddIncomeExamineLogMapper addIncomeExamineLogMapper;

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
	
	/**
	 * 获取追加收益审核列表
	 * @param req
	 * @return List<AddIncomeExamine>
	 */
	public List<AddIncomeExamine> listAllAddIncomeExamine(AddIncomeExamineDTO req) {
		return addIncomeExamineMapper.listAllAddIncomeExamine(req);
	}
	
	
	/**
	 * 获取追加收益审核日志
	 * @param addIncomeExamineId
	 * @return List<AddIncomeExamineLog>
	 */
	public List<AddIncomeExamineLog> listAddIncomeExamineLog(Integer addIncomeExamineId) {
		return addIncomeExamineLogMapper.listAddIncomeExamineLog(addIncomeExamineId);
	}
	
	
	/**
	 * 获取追加收益根据id
	 * @param id
	 * @return AddIncomeExamine
	 */
	public AddIncomeExamine getAddIncomeExamine(Integer id) {
		return addIncomeExamineMapper.selectByPrimaryKey(id);
	}
	
	
	/**
	 * 修改收益审核状态
	 * @param req
	 * @return int
	 */
	public int updateExamine(AddIncomeExamineOptDTO req) {
		AddIncomeExamine examine = new AddIncomeExamine();
		examine.setId(req.getId());
		examine.setRemark(req.getRemark());
		examine.setStatus(req.getFlag());
		examine.setUpdateOp(req.getOperator());
		examine.setUpdateTime(new Date());
		return addIncomeExamineMapper.updateAddIncomeExaminePass(examine);
	} 
	
	
	/**
	 * 记录审核日志
	 * @param req
	 * @return int
	 */
	public int saveAddIncomeExamineLog(AddIncomeExamineOptDTO req) {
		AddIncomeExamineLog elog = new AddIncomeExamineLog();
		elog.setAddIncomeExamineId(req.getId());
		elog.setExamineOperator(req.getOperator());
		elog.setExamineResultStatus(req.getFlag());
		elog.setExamineResultStatusTxt(ExamineStatusEnum.getName(req.getFlag()));
		elog.setExamineTime(new Date());
		elog.setRemark(req.getRemark());
		return addIncomeExamineLogMapper.insertSelective(elog);
	}

}
