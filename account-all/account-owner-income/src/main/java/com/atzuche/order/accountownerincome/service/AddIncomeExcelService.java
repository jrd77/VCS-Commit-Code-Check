package com.atzuche.order.accountownerincome.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountownerincome.entity.AddIncomeExcelContextEntity;
import com.atzuche.order.accountownerincome.entity.AddIncomeExcelEntity;
import com.atzuche.order.accountownerincome.entity.AddIncomeExcelVO;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExcelContextEntityMapper;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExcelEntityMapper;
import com.atzuche.order.commons.PageBean;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelQueryDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;

@Service
public class AddIncomeExcelService {

	@Autowired
	private AddIncomeExcelEntityMapper addIncomeExcelEntityMapper;
	@Autowired
	private AddIncomeExcelContextEntityMapper addIncomeExcelContextEntityMapper;
	
	/**
	 * 获取追加收益文件列表
	 * @param aie
	 * @return AddIncomeExcelVO
	 */
	public AddIncomeExcelVO getAddIncomeExcelVO(AddIncomeExcelConsoleDTO aie) {
		long pageNumber = 1L;
		long pageSize = PageBean.PAGE_SIZE;
		String fileName = null;
		if (aie != null) {
			pageNumber = StringUtils.isBlank(aie.getPageNumber()) ? 1L:Long.valueOf(aie.getPageNumber());
			pageSize = StringUtils.isBlank(aie.getPageSize()) ? PageBean.PAGE_SIZE:Long.valueOf(aie.getPageSize());
			fileName = aie.getFileName();
		}
		int totalCount = addIncomeExcelEntityMapper.queryListCount(aie);
		AddIncomeExcelQueryDTO addIncomeExcelQueryDTO = new AddIncomeExcelQueryDTO(pageNumber, totalCount, pageSize);
		addIncomeExcelQueryDTO.setFileName(fileName);
		List<AddIncomeExcelEntity> list = addIncomeExcelEntityMapper.queryList(addIncomeExcelQueryDTO);
		AddIncomeExcelVO addIncomeExcelVO = new AddIncomeExcelVO();
		addIncomeExcelVO.setTotalSize(totalCount);
		addIncomeExcelVO.setList(list);
		return addIncomeExcelVO;
	}
	
	
	/**
	 * 保存追加收益
	 * @param addIncomeImportDTO
	 */
	public void saveAddIncomeExcel(AddIncomeImportDTO addIncomeImportDTO) {
		if (addIncomeImportDTO == null) {
			return;
		}
		com.atzuche.order.commons.entity.dto.AddIncomeExcelEntity addIncomeExcel = addIncomeImportDTO.getAddIncomeExcel();
		if (addIncomeExcel == null) {
			return;
		}
		AddIncomeExcelEntity nowAddIncomeExcelEntity = new AddIncomeExcelEntity();
		BeanUtils.copyProperties(addIncomeExcel, nowAddIncomeExcelEntity);
		addIncomeExcelEntityMapper.insertSelective(nowAddIncomeExcelEntity);
		List<com.atzuche.order.commons.entity.dto.AddIncomeExcelContextEntity> contentList = addIncomeImportDTO.getContentList();
		if (contentList == null || contentList.isEmpty()) {
			return;
		}
		List<AddIncomeExcelContextEntity> nowContentList = new ArrayList<AddIncomeExcelContextEntity>();
		for (com.atzuche.order.commons.entity.dto.AddIncomeExcelContextEntity context:contentList) {
			AddIncomeExcelContextEntity nowContent = new AddIncomeExcelContextEntity();
			BeanUtils.copyProperties(context, nowContent);
			nowContent.setAddId(nowAddIncomeExcelEntity.getId());
			nowContentList.add(nowContent);
		}
		addIncomeExcelContextEntityMapper.saveAddIncomeExcelContextBatch(nowContentList);
	}
	
	
	/**
	 * 追加收益操作
	 * @param addIncomeExcelOptDTO
	 */
	public void updateStatus(AddIncomeExcelOptDTO addIncomeExcelOptDTO) {
		AddIncomeExcelEntity addIncomeExcelEntity = new AddIncomeExcelEntity();
		addIncomeExcelEntity.setId(Long.valueOf(addIncomeExcelOptDTO.getId().toString()));
		addIncomeExcelEntity.setOperate(addIncomeExcelOptDTO.getOperator());
		addIncomeExcelEntity.setOperateTime(new Date());
		addIncomeExcelEntity.setStatus(addIncomeExcelOptDTO.getFlag());
		addIncomeExcelEntityMapper.updateByPrimaryKeySelective(addIncomeExcelEntity);
	}
}
