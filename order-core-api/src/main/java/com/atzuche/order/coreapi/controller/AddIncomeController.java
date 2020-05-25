package com.atzuche.order.coreapi.controller;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.accountownerincome.entity.AddIncomeExcelVO;
import com.atzuche.order.accountownerincome.service.AddIncomeExcelService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.autoyol.commons.web.ResponseData;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AddIncomeController {
	
	private AddIncomeExcelService addIncomeExcelService;

	/**
	 * 获取追加收益文件列表
	 * @param req
	 * @return ResponseData<AddIncomeExcelVO>
	 */
	@GetMapping("/income/excel/list")
    public ResponseData<AddIncomeExcelVO> getAddIncomeExcelVO(@Valid AddIncomeExcelConsoleDTO aie, BindingResult bindingResult) {
		log.info("获取追加收益文件列表 AddIncomeExcelConsoleDTO=[{}]", aie);
		BindingResultUtil.checkBindingResult(bindingResult);
		AddIncomeExcelVO addIncomeExcelVO = addIncomeExcelService.getAddIncomeExcelVO(aie);
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
		addIncomeExcelService.saveAddIncomeExcel(req);
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
}
