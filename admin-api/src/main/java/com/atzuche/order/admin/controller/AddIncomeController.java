package com.atzuche.order.admin.controller;

import java.util.Date;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atzuche.order.admin.service.AddIncomeRemoteService;
import com.atzuche.order.admin.util.oss.OSSUtils;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.autoyol.platformcost.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AutoDocVersion(version = "追加收益")
public class AddIncomeController {

	@Autowired
	private AddIncomeRemoteService addIncomeRemoteService;

	@AutoDocMethod(description = "追加收益文件列表", value = "追加收益文件列表", response = AddIncomeExcelVO.class)
	@RequestMapping(value = "console/income/excel/list", method = RequestMethod.GET)
	public ResponseData<AddIncomeExcelVO> getAddIncomeExcelVO(AddIncomeExcelConsoleDTO req) {
		log.info("AddIncomeController.getAddIncomeExcelVO req=[{}]", req);
		AddIncomeExcelVO addIncomeExcelVO = addIncomeRemoteService.getAddIncomeExcelVO(req);
		return ResponseData.success(addIncomeExcelVO);
	}

	/**
	 * 上传并解析excel
	 */
	@AutoDocMethod(description = "追加收益导入", value = "追加收益导入", response = ResponseData.class)
	@RequestMapping("console/income/excel/import")
	public ResponseData<?> importAddIncomeExcel(@RequestParam("addExcelFile") MultipartFile addExcelFile) {
		return null;
		/*
		 * Map<String,Object> result=new HashMap<String,Object>(); String msg = "";
		 * Workbook workbook; List<Map<String,Object>> datas = new
		 * ArrayList<Map<String,Object>>(); try { if(!addExcelFile.isEmpty()) { String
		 * fileType = addExcelFile.getContentType(); String
		 * fileName=addExcelFile.getOriginalFilename(); if (!fileType
		 * .equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
		 * && !fileType.equals("application/vnd.ms-excel") &&
		 * !fileType.equals("application/x-excel")) { msg = "上传文件类型错误，必须为xls或xlsx！";
		 * log.error("上传文件类型错误,fileType：{}", fileType); } else { workbook
		 * =ExcelUtils.getWorkBook(addExcelFile.getOriginalFilename(),addExcelFile.
		 * getInputStream()); Sheet sheet = workbook.getSheetAt(0); String
		 * str=CommonConstants.DATETIME_SEC_STR_FORMAT(new Date()); String
		 * suffixes=addExcelFile.getOriginalFilename().substring(addExcelFile.
		 * getOriginalFilename().lastIndexOf("."),
		 * addExcelFile.getOriginalFilename().length()); String key =
		 * "account_log/add/"+str.substring(0,
		 * 8)+"/"+CommonUtils.getRandomNumUpChar(8)+suffixes; for (int rowNum = 1;
		 * rowNum <= sheet.getLastRowNum(); rowNum++) { Map<String,Object> dataMap=new
		 * HashMap<String,Object>(); XSSFRow hssfRow =(XSSFRow) sheet.getRow(rowNum);
		 * //获取excel中的内容 String orderNo=CarHwXls.getValue(hssfRow.getCell(0));
		 * if(StringUtils.isBlank(orderNo)){ throw new
		 * Exception("导入的Excel文件中存在空订单号，导入失败"); } String
		 * memNo=CarHwXls.getValue(hssfRow.getCell(1)); String
		 * mobile=CarHwXls.getValue(hssfRow.getCell(2)); String
		 * type=CarHwXls.getValue(hssfRow.getCell(3)); String
		 * detailType=CarHwXls.getValue(hssfRow.getCell(4)); String
		 * amt=CarHwXls.getValue(hssfRow.getCell(5)); String
		 * describe=CarHwXls.getValue(hssfRow.getCell(6)); String
		 * department=CarHwXls.getValue(hssfRow.getCell(7)); String
		 * applyTime=CarHwXls.getValue(hssfRow.getCell(8)); String
		 * memType=CarHwXls.getValue(hssfRow.getCell(9))==null?"":CarHwXls.getValue(
		 * hssfRow.getCell(9)); dataMap.put("orderNo", orderNo);
		 * dataMap.put("memNo",memNo); dataMap.put("mobile", mobile);
		 * dataMap.put("type",type); dataMap.put("amt", amt);
		 * dataMap.put("describe",describe); dataMap.put("department", department);
		 * dataMap.put("applyTime",applyTime); dataMap.put("detailType", detailType);
		 * dataMap.put("memType", memType.trim()); datas.add(dataMap); }
		 * if(datas.size()<=0){ msg="文件内容为空，禁止上传"; result.put("msg", msg); return
		 * result; } //检查数据 String checkMsg=checkData(datas);
		 * if(!SUCCESS.equals(checkMsg)){ result.put("msg", checkMsg); return result; }
		 * //上传excel OSSUtils.uploadMultipartFile(key, addExcelFile); Map<String,
		 * Object> param=new HashMap<String,Object>(); param.put("loginName",
		 * getCurrOperator(request).getLoginName()); param.put("key", key);
		 * param.put("fileName", fileName); addIncomeExcelService.add(param, datas);
		 * msg="文件上传成功"; } }else{ msg = "请选择上传文件！"; } } catch (Exception e) {
		 * //msg="导入异常！"; logger.error("上传追加收益文件失败：", e); result.put("msg",
		 * e.getMessage()); return result; } result.put("msg", msg); return result;
		 */}
}
