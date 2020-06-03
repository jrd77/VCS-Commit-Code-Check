package com.atzuche.order.admin.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.vo.AddIncomeExamine;

@Service
public class AddIncomeService {

	public HSSFWorkbook export(List<AddIncomeExamine> list) {
		String[] excelHeader = {"订单号", "会员身份", "会员号","会员手机号","会员姓名","追加类型","追加明细类型","追加金额","追加说明","申请部门","追加收益审核状态","备注","申请日期","创建时间","审核时间"};  
        HSSFWorkbook wb = new HSSFWorkbook();  
        HSSFSheet sheet = wb.createSheet("追加收益列表");  
        sheet.setColumnWidth(0, 3766);
        sheet.setColumnWidth(1, 3766);
        sheet.setColumnWidth(2, 3766);
        sheet.setColumnWidth(3, 3766);
        sheet.setColumnWidth(4, 3766);
        sheet.setColumnWidth(5, 3766);
        sheet.setColumnWidth(6, 3766);
        sheet.setColumnWidth(7, 3766);
        sheet.setColumnWidth(8, 3766);
        sheet.setColumnWidth(9, 3766);
        sheet.setColumnWidth(10, 3766);
        sheet.setColumnWidth(11, 3766);
        sheet.setColumnWidth(12, 3766);
        sheet.setColumnWidth(13, 3766);
        sheet.setColumnWidth(14, 3766);
        HSSFCellStyle style = wb.createCellStyle();  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
        style.setWrapText(true); //设置是否能够换行，能够换行为true  
        
        HSSFRow row = sheet.createRow((int) 0); 
        for (int i = 0; i < excelHeader.length; i++) {  
            HSSFCell cell = row.createCell(i);  
            cell.setCellValue(excelHeader[i]);  
            cell.setCellStyle(style);  
        }  
        for (int i = 0; i < list.size(); i++) {  
            row = sheet.createRow(i + 1);  
            AddIncomeExamine ts = list.get(i);  
            HSSFCell cell = null;
            cell = row.createCell(0);
			cell.setCellValue(strConvert(ts.getOrderNo()));
            cell.setCellStyle(style);
            
            cell = row.createCell(1);
            cell.setCellValue(strConvert(ts.getMemTypeStr()));
            cell.setCellStyle(style);
            
            cell = row.createCell(2);
			cell.setCellValue(ts.getMemNo() == null ? "":ts.getMemNo().toString());
            cell.setCellStyle(style);
                         
            cell = row.createCell(3);
 			cell.setCellValue(ts.getMobile() == null ? "":ts.getMobile().toString());
            cell.setCellStyle(style);
            
            cell = row.createCell(4);
 			cell.setCellValue(strConvert(ts.getName()));
            cell.setCellStyle(style);
            
            cell = row.createCell(5);       
            cell.setCellValue(strConvert(ts.getType()));
            cell.setCellStyle(style);
            
            cell = row.createCell(6);
            cell.setCellValue(strConvert(ts.getDetailType()));
            cell.setCellStyle(style);
            
            cell = row.createCell(7);
            cell.setCellValue(ts.getAmt() == null ? "":ts.getAmt().toString());
            cell.setCellStyle(style);
            
            cell = row.createCell(8);
			cell.setCellValue(strConvert(ts.getDescribe()));
            cell.setCellStyle(style);
            
            cell = row.createCell(9);
			cell.setCellValue(strConvert(ts.getDepartment()));
            cell.setCellStyle(style);
            
            cell = row.createCell(10);
			cell.setCellValue(strConvert(ts.getStatusStr()));
            cell.setCellStyle(style);
            
            cell = row.createCell(11);
            cell.setCellValue(strConvert(ts.getRemark()));  
            cell.setCellStyle(style);
            
            cell = row.createCell(12);
            cell.setCellValue(strConvert(ts.getApplyTimeStr()));  
            cell.setCellStyle(style);
            
            cell = row.createCell(13);
            cell.setCellValue(strConvert(ts.getCreateTimeStr()));  
            cell.setCellStyle(style);
            
            cell = row.createCell(14);
            cell.setCellValue(strConvert(ts.getExamineTime()));  
            cell.setCellStyle(style);
            
        }
        return wb; 
	}
	
	
	private String strConvert(String s) {
		if (s == null) {
			return "";
		}
		return s.trim();
	}
}
