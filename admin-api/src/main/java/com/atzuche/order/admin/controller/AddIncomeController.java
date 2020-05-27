package com.atzuche.order.admin.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
import com.atzuche.order.admin.util.CarHwXls;
import com.atzuche.order.admin.util.CommonUtils;
import com.atzuche.order.admin.util.ExcelUtils;
import com.atzuche.order.admin.util.oss.OSSUtils;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelConsoleDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelContextEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelEntity;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelOptDTO;
import com.atzuche.order.commons.entity.dto.AddIncomeExcelVO;
import com.atzuche.order.commons.entity.dto.AddIncomeImportDTO;
import com.atzuche.order.commons.exceptions.ImportAddIncomeExcelException;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.autoyol.platformcost.DateUtils;

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

	
	@AutoDocMethod(description = "追加收益导入", value = "追加收益导入", response = ResponseData.class)
	@RequestMapping("console/income/excel/import")
	public ResponseData<?> importAddIncomeExcel(@RequestParam("addExcelFile") MultipartFile addExcelFile) {
		if(addExcelFile == null || addExcelFile.isEmpty()) {
			return ResponseData.createErrorCodeResponse("111001", "请选择上传文件！");
		}
		String fileType = addExcelFile.getContentType();
		String fileName = addExcelFile.getOriginalFilename();
		if (!fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
				&& !fileType.equals("application/vnd.ms-excel")
				&& !fileType.equals("application/x-excel")) {
			log.error("上传文件类型错误,fileType：{}", fileType);
			return ResponseData.createErrorCodeResponse("111002", "上传文件类型错误，必须为xls或xlsx！");
		}
		try {
			List<AddIncomeExcelContextEntity> list = new ArrayList<AddIncomeExcelContextEntity>();
			Workbook workbook =ExcelUtils.getWorkBook(addExcelFile.getOriginalFilename(),addExcelFile.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			String str = DateUtils.formate(LocalDateTime.now(), DateUtils.DATE_DEFAUTE);
			String suffixes = addExcelFile.getOriginalFilename().substring(addExcelFile.getOriginalFilename().lastIndexOf("."), addExcelFile.getOriginalFilename().length());
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
                String applyTime=CarHwXls.getValue(hssfRow.getCell(8));
                String memType=CarHwXls.getValue(hssfRow.getCell(9))==null?"":CarHwXls.getValue(hssfRow.getCell(9));
                Integer intMemType = null;
                if("车主".equals(memType)){
                	intMemType = 1;
    			}else if("租客".equals(memType)){
    				intMemType = 0;
    			}
                context.setOrderNo(orderNo);
				context.setMemNo(StringUtils.isBlank(memNo)?null:Integer.valueOf(memNo));
				context.setMobile(StringUtils.isBlank(mobile)?null:Long.valueOf(mobile));
				context.setType(type);
				context.setAmt(StringUtils.isBlank(amt)?null:Integer.valueOf(amt));
				context.setDescribe(describe);
				context.setDepartment(department);
				context.setApplyTime(applyTime);
				context.setDetailType(detailType);
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
		addIncomeRemoteService.updateStatus(req);
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
}
