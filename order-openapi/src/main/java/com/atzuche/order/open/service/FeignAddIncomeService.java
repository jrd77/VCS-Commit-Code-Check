package com.atzuche.order.open.service;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.autoyol.commons.web.ResponseData;

@FeignClient(name="order-center-api")
public interface FeignAddIncomeService {

	/**
	 * 获取追加收益文件列表
	 * @param req
	 * @return ResponseData<AddIncomeExcelVO>
	 */
	@GetMapping("/income/excel/list")
    public ResponseData<AddIncomeExcelVO> getAddIncomeExcelVO(@Valid AddIncomeExcelConsoleDTO aie);
	
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
}
