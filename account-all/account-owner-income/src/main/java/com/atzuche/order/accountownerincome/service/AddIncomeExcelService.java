package com.atzuche.order.accountownerincome.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.accountownerincome.entity.AddIncomeExcelContextEntity;
import com.atzuche.order.accountownerincome.entity.AddIncomeExcelEntity;
import com.atzuche.order.accountownerincome.entity.AddIncomeExcelVO;
import com.atzuche.order.accountownerincome.exception.AddIncomeCanNotWithdrawException;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExamineMapper;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExcelContextEntityMapper;
import com.atzuche.order.accountownerincome.mapper.AddIncomeExcelEntityMapper;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.PageBean;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelQueryDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.exceptions.ImportAddIncomeExcelException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddIncomeExcelService {

	@Autowired
	private AddIncomeExcelEntityMapper addIncomeExcelEntityMapper;
	@Autowired
	private AddIncomeExcelContextEntityMapper addIncomeExcelContextEntityMapper;
	@Autowired
	private AddIncomeExamineMapper addIncomeExamineMapper;
	
	private static final int ADD_INCOME_PASS = 1;
	private static final int ADD_INCOME_WITHDRAW = 3;
	private static final int ADD_INCOME_DELL = 4;
	
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
	@Transactional(rollbackFor=Exception.class)
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
		try {
			addIncomeExcelContextEntityMapper.saveAddIncomeExcelContextBatch(nowContentList);
		} catch (Exception e) {
			String resultMsg = exceptionProcess(e);
			if (StringUtils.isNotBlank(resultMsg)) {
				throw new ImportAddIncomeExcelException(resultMsg);
			}
			throw e;
		}
	}
	
	
	/**
	 * 追加收益操作
	 * @param addIncomeExcelOptDTO
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateStatus(AddIncomeExcelOptDTO addIncomeExcelOptDTO) {
		if (addIncomeExcelOptDTO == null) {
			return;
		}
		int flag = addIncomeExcelOptDTO.getFlag() == null ? 0:addIncomeExcelOptDTO.getFlag().intValue();
		Long addId = addIncomeExcelOptDTO.getId() == null ? null:Long.valueOf(addIncomeExcelOptDTO.getId().toString());
		if (addId == null) {
			return;
		}
		if (flag == ADD_INCOME_PASS) {
			// 审核通过
			passAddIncome(addId);
		} else if (flag == ADD_INCOME_WITHDRAW) {
			// 撤回
			withdrawAddIncome(addId);
		} else if (flag == ADD_INCOME_DELL) {
			addIncomeExcelEntityMapper.delAddIncomeExcel(addIncomeExcelOptDTO);
		}
		if (flag != ADD_INCOME_DELL) {
			// 查询该批次下已经操作过的追加收益数量
			int count = addIncomeExamineMapper.getCountByAddIdAndStatus(addId);
			if (count > 0) {
				throw new AddIncomeCanNotWithdrawException();
			}
			AddIncomeExcelEntity addIncomeExcelEntity = new AddIncomeExcelEntity();
			addIncomeExcelEntity.setId(addId);
			addIncomeExcelEntity.setOperate(addIncomeExcelOptDTO.getOperator());
			addIncomeExcelEntity.setOperateTime(new Date());
			addIncomeExcelEntity.setStatus(addIncomeExcelOptDTO.getFlag());
			addIncomeExcelEntityMapper.updateByPrimaryKeySelective(addIncomeExcelEntity);
			addIncomeExcelContextEntityMapper.updateDelete(addId);
		}
	}
	
	/**
	 * 审核通过
	 * @param addId
	 */
	public void passAddIncome(Long addId) {
		List<AddIncomeExcelContextEntity> contextList = addIncomeExcelContextEntityMapper.listAddIncomeExcelContextByAddId(addId);
		if (contextList == null || contextList.isEmpty()) {
			return;
		}
		List<AddIncomeExamine> examineList = new ArrayList<AddIncomeExamine>();
		for (AddIncomeExcelContextEntity context:contextList) {
			AddIncomeExamine examine = new AddIncomeExamine();
			BeanUtils.copyProperties(context, examine);
			examine.setAddId(addId);
			String applyTime = context.getApplyTime();
			if (StringUtils.isNotBlank(applyTime)) {
				Date applyDate = DateUtils.parseDate(applyTime, DateUtils.fmt_yyyyMMdd);
				examine.setApplyTime(applyDate);
			}
			examineList.add(examine);
		}
		addIncomeExamineMapper.saveAddIncomeExamineBatch(examineList);
	}
	
	/**
	 * 追加收益撤回
	 * @param addId
	 */
	public void withdrawAddIncome(Long addId) {
		// 查询该批次下已经操作过的追加收益数量
		int count = addIncomeExamineMapper.getCountByAddIdAndStatus(addId);
		if (count > 0) {
			throw new AddIncomeCanNotWithdrawException();
		}
		// 删除该批次的追加收益
		addIncomeExamineMapper.delAddIncomeExamineByAddId(addId);
	}
	
	
	public String exceptionProcess(Exception e){
		String keyStart="Duplicate entry";
		String keyEnd="for key 'order_detail_type_amt_unique'";
		String result = null;
		if(e==null){
			return result;
		}
		Throwable t = e.getCause();
		if (t == null) {
			return result;
		}
		String msg = t.getMessage();
		int index=0;
		if (StringUtils.isNotBlank(msg)){
			if (msg.matches(keyStart+" .+ "+keyEnd)) {
				String uiniqueContent=msg.replace(keyStart, "").replace(keyEnd, "");
				index=uiniqueContent.indexOf("-");
				if(index!=-1){
					String [] uiniqueContents = uiniqueContent.split("-");
 					result="订单号:"+uiniqueContents[1]+"已追加收益！";
				} 
			}
		}
		return result;
	}
	
}
