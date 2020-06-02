package com.atzuche.order.open.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.vo.AddIncomeExamine;
import com.atzuche.order.commons.vo.AddIncomeExamineLog;
import com.atzuche.order.commons.vo.AddIncomeExamineVO;
import com.autoyol.commons.web.ResponseData;

@FeignClient(name="order-center-api")
//@FeignClient(url = "http://localhost:1412" ,name="order-center-api")
public interface FeignAddIncomeService {

	/**
	 * 获取追加收益文件列表
	 * @param req
	 * @return ResponseData<AddIncomeExcelVO>
	 */
	@PostMapping("/income/excel/list")
    public ResponseData<AddIncomeExcelVO> getAddIncomeExcelVO(@RequestBody AddIncomeExcelConsoleDTO req);
	
	/**
	 * 保存追加收益
	 * @param req
	 * @return ResponseData<?>
	 */
	@PostMapping("/income/excel/add")
	public ResponseData<?> saveAddIncomeExcel(@Valid @RequestBody AddIncomeImportDTO req);
	
	/**
	 * 追加收益操作
	 * @param req
	 * @return ResponseData<?>
	 */
	@PostMapping("/income/excel/update")
	public ResponseData<?> updateStatus(@Valid @RequestBody AddIncomeExcelOptDTO req);
	
	/**
	 * 获取追加收益审核列表(分页)
	 * @param req
	 * @return ResponseData<AddIncomeExamineVO>
	 */
	@PostMapping("/income/examine/list")
    public ResponseData<AddIncomeExamineVO> getAddIncomeExamineVO(@RequestBody AddIncomeExamineDTO req);
	
	/**
	 * 获取追加收益审核列表(非分页)
	 * @param req
	 * @return ResponseData<AddIncomeExamineVO>
	 */
	@PostMapping("/income/examine/listall")
    public ResponseData<List<AddIncomeExamine>> listAllAddIncomeExamine(@RequestBody AddIncomeExamineDTO req);
	
	/**
	 * 获取追加收益审核日志
	 * @param req
	 * @return ResponseData<List<AddIncomeExamineLog>>
	 */
	@GetMapping("/income/examinelog/list")
    public ResponseData<List<AddIncomeExamineLog>> listAddIncomeExamineLog(@RequestParam(value="id",required = true) Integer id);
	
	/**
	 * 追加收益审核操作
	 * @param req
	 * @return ResponseData<?>
	 */
	@PostMapping("/income/examineopt/update")
	public ResponseData<?> examineOpt(@Valid @RequestBody AddIncomeExamineOptDTO req);
}
