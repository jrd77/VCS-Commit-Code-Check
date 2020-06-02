package com.atzuche.order.admin.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.service.AddIncomeRemoteService;
import com.atzuche.order.admin.service.AddIncomeService;
import com.atzuche.order.admin.util.CarHwXls;
import com.atzuche.order.admin.util.CommonUtils;
import com.atzuche.order.admin.util.ExcelUtils;
import com.atzuche.order.admin.util.oss.OSSUtils;
import com.atzuche.order.admin.vo.resp.AddIncomeExamineLogVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExamineOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelContextEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.exceptions.ImportAddIncomeExcelException;
import com.atzuche.order.commons.vo.AddIncomeExamine;
import com.atzuche.order.commons.vo.AddIncomeExamineLog;
import com.atzuche.order.commons.vo.AddIncomeExamineVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.autoyol.platformcost.DateUtils;

import feign.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AutoDocVersion(version = "追加收益")
public class AddIncomeController {

	@Autowired
	private AddIncomeRemoteService addIncomeRemoteService;
	@Autowired
	private AddIncomeService addIncomeService;

	@AutoDocMethod(description = "追加收益文件列表", value = "追加收益文件列表", response = AddIncomeExcelVO.class)
	@RequestMapping(value = "console/income/excel/list", method = RequestMethod.GET)
	public ResponseData<AddIncomeExcelVO> getAddIncomeExcelVO(AddIncomeExcelConsoleDTO req) {
		log.info("AddIncomeController.getAddIncomeExcelVO req=[{}]", req);
		AddIncomeExcelVO addIncomeExcelVO = addIncomeRemoteService.getAddIncomeExcelVO(req);
		return ResponseData.success(addIncomeExcelVO);
	}

	
	@AutoDocMethod(description = "追加收益导入", value = "追加收益导入", response = ResponseData.class)
	@RequestMapping("console/income/excel/import")
	public ResponseData<?> importAddIncomeExcel(@RequestParam("addExcelFile") MultipartFile addExcelFile,String filename) {
		if(addExcelFile == null || addExcelFile.isEmpty()) {
			return ResponseData.createErrorCodeResponse("111001", "请选择上传文件！");
		}
		String fileType = addExcelFile.getContentType();
		String fileName = StringUtils.isBlank(filename) ? addExcelFile.getOriginalFilename():filename;//"addImcomeTemplate.xlsx";
		log.info("追加收益导入fileName=[{}],fileType=[{}]",fileName,fileType);
		/*
		 * try { fileName = new
		 * String(addExcelFile.getOriginalFilename().getBytes("ISO-8859-1"), "UTF-8"); }
		 * catch (UnsupportedEncodingException e1) { log.error("追加收益导入获取文件名出错 e:", e1);
		 * }
		 */
		if (!fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
				&& !fileType.equals("application/vnd.ms-excel")
				&& !fileType.equals("application/x-excel")) {
			log.error("上传文件类型错误,fileType：{}", fileType);
			return ResponseData.createErrorCodeResponse("111002", "上传文件类型错误，必须为xls或xlsx！");
		}
		try {
			List<AddIncomeExcelContextEntity> list = new ArrayList<AddIncomeExcelContextEntity>();
			Workbook workbook =ExcelUtils.getWorkBook(fileName,addExcelFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			String str = DateUtils.formate(LocalDateTime.now(), DateUtils.DATE_DEFAUTE);
			String suffixes = fileName.substring(fileName.lastIndexOf("."), fileName.length());
			String key = "account_log/add/"+str.substring(0, 8)+"/"+CommonUtils.getRandomNumUpChar(8)+suffixes;
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				AddIncomeExcelContextEntity context = new AddIncomeExcelContextEntity();
				XSSFRow hssfRow =(XSSFRow) sheet.getRow(rowNum);
                //获取excel中的内容
                String orderNo=CarHwXls.getValue(hssfRow.getCell(0));
                String memNo=CarHwXls.getValue(hssfRow.getCell(1));
                String mobile=CarHwXls.getValue(hssfRow.getCell(2));
                String type=CarHwXls.getValue(hssfRow.getCell(3));
                String detailType=CarHwXls.getValue(hssfRow.getCell(4));
                String amt=CarHwXls.getValue(hssfRow.getCell(5));
                String describe=CarHwXls.getValue(hssfRow.getCell(6));
                String department=CarHwXls.getValue(hssfRow.getCell(7));
                String applyTime=CarHwXls.convertCellToString(hssfRow.getCell(8));
                applyTime = convertDate(applyTime);
                
                String memType=CarHwXls.getValue(hssfRow.getCell(9))==null?"":CarHwXls.getValue(hssfRow.getCell(9));
                Integer intMemType = null;
                if("车主".equals(memType)){
                	intMemType = 1;
    			}else if("租客".equals(memType)){
    				intMemType = 0;
    			}
                context.setOrderNo(StringUtils.isBlank(orderNo)?null:orderNo);
				context.setMemNo(StringUtils.isBlank(memNo)?null:Integer.valueOf(memNo));
				context.setMobile(StringUtils.isBlank(mobile)?null:Long.valueOf(mobile));
				context.setType(StringUtils.isBlank(type)?null:type);
				context.setAmt(StringUtils.isBlank(amt)?null:Integer.valueOf(amt));
				context.setDescribe(StringUtils.isBlank(describe)?null:describe);
				context.setDepartment(StringUtils.isBlank(department)?null:department);
				context.setApplyTime(StringUtils.isBlank(applyTime)?null:applyTime);
				context.setDetailType(StringUtils.isBlank(detailType)?null:detailType);
				context.setMemType(intMemType);
				list.add(context);
			}
			if(list.isEmpty()){
				return ResponseData.createErrorCodeResponse("111003", "文件内容为空，禁止上传");
			}
			// 检查数据
			checkExcelDate(list);
			// 上传excel
			OSSUtils.uploadMultipartFile(key, addExcelFile);
			AddIncomeExcelEntity excele = new AddIncomeExcelEntity();
			excele.setFileName(fileName);
			excele.setExcelHref(key);
			excele.setUploder(AdminUserUtil.getAdminUser().getAuthName());
			AddIncomeImportDTO req = new AddIncomeImportDTO();
			req.setAddIncomeExcel(excele);
			req.setContentList(list);
			addIncomeRemoteService.saveAddIncomeExcel(req);
		
		} catch (Exception e) {
			log.error("上传追加收益文件失败：", e);
			return ResponseData.createErrorCodeResponse("111004", e.getMessage());
		}	
		return ResponseData.success();
	}
	
	
	@AutoDocMethod(description = "追加收益操作", value = "追加收益操作", response = AddIncomeExcelVO.class)
	@RequestMapping(value = "console/income/excel/update", method = RequestMethod.POST)
	public ResponseData<?> updateStatus(@Valid @RequestBody AddIncomeExcelOptDTO req, BindingResult bindingResult) {
		log.info("AddIncomeController.updateStatus req=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		req.setOperator(AdminUserUtil.getAdminUser().getAuthName());
		addIncomeRemoteService.updateStatus(req);
		return ResponseData.success();
	}
	
	
	@AutoDocMethod(description = "获取追加收益审核列表(分页)", value = "获取追加收益审核列表(分页)", response = AddIncomeExamineVO.class)
	@RequestMapping(value = "console/income/examine/list", method = RequestMethod.GET)
	public ResponseData<AddIncomeExamineVO> getAddIncomeExamineVO(AddIncomeExamineDTO req) {
		log.info("AddIncomeController.getAddIncomeExamineVO req=[{}]", req);
		AddIncomeExamineVO addIncomeExamineVO = addIncomeRemoteService.getAddIncomeExamineVO(req);
		return ResponseData.success(addIncomeExamineVO);
	}
	
	
	@AutoDocMethod(description = "导出追加收益审核列表", value = "导出获取追加收益审核列表")
	@RequestMapping(value = "console/income/examine/export")
	public void exportExamine(AddIncomeExamineDTO req, HttpServletResponse response) {
		log.info("AddIncomeController.exportExamine req=[{}]", req);
		List<AddIncomeExamine> list = addIncomeRemoteService.listAllAddIncomeExamine(req);
		try {
            HSSFWorkbook wb = addIncomeService.export(list);
            response.setContentType("application/vnd.ms-excel");  
            response.setHeader("Content-disposition", "attachment;filename=settleList.xls");  
            OutputStream ouputStream = response.getOutputStream();  
            wb.write(ouputStream);  
            ouputStream.flush();  
            ouputStream.close(); 
        } catch (Exception e) {    
        	log.error("导出追加收益审核列表错误:", e);
        }  
	}
	
	
	@AutoDocMethod(description = "获取追加收益审核日志", value = "获取追加收益审核日志", response = AddIncomeExamineLogVO.class)
	@RequestMapping(value = "console/income/examinelog/list", method = RequestMethod.GET)
	public ResponseData<AddIncomeExamineLogVO> listAddIncomeExamineLog(@RequestParam(value="id",required = true) Integer id) {
		log.info("AddIncomeController.listAddIncomeExamineLog id=[{}]", id);
		List<AddIncomeExamineLog> list = addIncomeRemoteService.listAddIncomeExamineLog(id);
		AddIncomeExamineLogVO addIncomeExamineLogVO = new AddIncomeExamineLogVO();
		addIncomeExamineLogVO.setList(list);
		return ResponseData.success(addIncomeExamineLogVO);
	}
	
	
	@AutoDocMethod(description = "追加收益审核操作", value = "追加收益审核操作", response = ResponseData.class)
	@RequestMapping(value = "console/income/examineopt/update", method = RequestMethod.POST)
	public ResponseData<?> examineOpt(@Valid @RequestBody AddIncomeExamineOptDTO req, BindingResult bindingResult) {
		log.info("AddIncomeController.examineOpt req=[{}]", req);
		BindingResultUtil.checkBindingResult(bindingResult);
		req.setOperator(AdminUserUtil.getAdminUser().getAuthName());
		addIncomeRemoteService.examineOpt(req);
		return ResponseData.success();
	}
	
	
	/**
	 * 校验
	 * @param list
	 */
	public void checkExcelDate(List<AddIncomeExcelContextEntity> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		for (int i=0;i<list.size();i++) {
			AddIncomeExcelContextEntity context = list.get(i);
			if (StringUtils.isBlank(context.getOrderNo()) && 
					context.getMemNo() == null && context.getMobile() == null) {
				throw new ImportAddIncomeExcelException("第"+(i+1)+"行数据,订单号、会员号、手机号必须有一个存在！");
			}
			Integer memType = context.getMemType();
			if(memType == null){
				throw new ImportAddIncomeExcelException("第"+(i+1)+"行数据,用户类型为空或者格式不对！");
			}
		}
	}
	
	
	/**
	 * 格式化日期
	 * @param appdate
	 * @return String
	 */
	public String convertDate(String appdate) {
		if (StringUtils.isBlank(appdate)) {
			return null;
		}
		Date applyDate = com.atzuche.order.commons.DateUtils.parseDate(appdate, com.atzuche.order.commons.DateUtils.fmt_next_yyyyMMdd);
		appdate = com.atzuche.order.commons.DateUtils.formate(applyDate, com.atzuche.order.commons.DateUtils.fmt_yyyyMMdd);
		return appdate;
	}
}
