package com.atzuche.order.coreapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.accountownerincome.entity.AddIncomeExamine;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamineLog;
import com.atzuche.order.accountownerincome.entity.AddIncomeExamineVO;
import com.atzuche.order.accountownerincome.entity.AddIncomeExcelVO;
import com.atzuche.order.accountownerincome.service.AddIncomeExamineService;
import com.atzuche.order.accountownerincome.service.AddIncomeExcelService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.coreapi.service.AddIncomeService;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AddIncomeController {
	@Autowired
	private AddIncomeExcelService addIncomeExcelService;
	@Autowired
	private AddIncomeService addIncomeService;
	@Autowired
	private AddIncomeExamineService addIncomeExamineService;

	/**
	 * 获取追加收益文件列表
	 * @param req
	 * @return ResponseData<AddIncomeExcelVO>
	 */
	@PostMapping("/income/excel/list")
    public ResponseData<AddIncomeExcelVO> getAddIncomeExcelVO(@RequestBody AddIncomeExcelConsoleDTO req) {
		log.info("获取追加收益文件列表 AddIncomeExcelConsoleDTO=[{}]", req);
		AddIncomeExcelVO addIncomeExcelVO = addIncomeExcelService.getAddIncomeExcelVO(req);
    	return ResponseData.success(addIncomeExcelVO);
    }
	
	/**
	 * 保存追加收益
	 * @param req
	 * @param bindingResult
	 * @return ResponseData<?>
	 */
	@PostMapping("/income/excel/add")
	public ResponseData<?> saveAddIncomeExcel(@Valid @RequestBody AddIncomeImportDTO req, BindingResult bindingResult) {
		log.info("保存追加收益AddIncomeImportDTO=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		addIncomeService.saveAddIncomeExcel(req);
        return ResponseData.success();
    }
	
	
	/**
	 * 追加收益操作
	 * @param req
	 * @param bindingResult
	 * @return ResponseData<?>
	 */
	@PostMapping("/income/excel/update")
	public ResponseData<?> updateStatus(@Valid @RequestBody AddIncomeExcelOptDTO req, BindingResult bindingResult) {
		log.info("追加收益操作AddIncomeExcelOptDTO=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		addIncomeExcelService.updateStatus(req);
        return ResponseData.success();
    }
	
	
	/**
	 * 获取追加收益审核列表(分页)
	 * @param req
	 * @return ResponseData<AddIncomeExamineVO>
	 */
	@PostMapping("/income/examine/list")
    public ResponseData<AddIncomeExamineVO> getAddIncomeExamineVO(@RequestBody AddIncomeExamineDTO req) {
		log.info("获取追加收益审核列表(分页) AddIncomeExamineDTO=[{}]", req);
		AddIncomeExamineVO addIncomeExamineVO = addIncomeExamineService.getAddIncomeExamineVO(req);
    	return ResponseData.success(addIncomeExamineVO);
    }
	
	
	/**
	 * 获取追加收益审核列表(非分页)
	 * @param req
	 * @return ResponseData<AddIncomeExamineVO>
	 */
	@PostMapping("/income/examine/listall")
    public ResponseData<List<AddIncomeExamine>> listAllAddIncomeExamine(@RequestBody AddIncomeExamineDTO req) {
		log.info("获取追加收益审核列表(非分页) AddIncomeExamineDTO=[{}]", req);
		List<AddIncomeExamine> list = addIncomeExamineService.listAllAddIncomeExamine(req);
    	return ResponseData.success(list);
    }
	
	
	/**
	 * 获取追加收益审核日志
	 * @param req
	 * @return ResponseData<List<AddIncomeExamineLog>>
	 */
	@GetMapping("/income/examinelog/list")
    public ResponseData<List<AddIncomeExamineLog>> listAddIncomeExamineLog(@RequestParam(value="id",required = true) Integer id) {
		log.info("获取追加收益审核日志 addIncomeExamineId=[{}]", id);
		List<AddIncomeExamineLog> list = addIncomeExamineService.listAddIncomeExamineLog(id);
    	return ResponseData.success(list);
    }
	
	
	/**
	 * 追加收益审核操作
	 * @param req
	 * @param bindingResult
	 * @return ResponseData<?>
	 */
	@PostMapping("/income/examineopt/update")
	public ResponseData<?> examineOpt(@Valid @RequestBody AddIncomeExamineOptDTO req, BindingResult bindingResult) {
		log.info("追加收益审核操作AddIncomeExamineOptDTO=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		addIncomeService.examineOpt(req);
        return ResponseData.success();
    }
}
