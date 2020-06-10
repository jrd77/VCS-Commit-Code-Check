/**
 * 
 */
package com.atzuche.order.admin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author: linqc
 * @date:   2014-12-10
 *
 */
public class ExcelUtils {
	private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
	private static final String CHECK_CELL = "checkCell";
	private static final String DESC_CELL = "descCell";
	private static final String REG_CELL = "regCell";
	private static final String VALI_CELL = "valiCell";
	private static final String REGTIME_CELL = "regTimeCell";
	private static final String DEALTIME_CELL = "dealTimeCell";
	private static final String VALITIME_CELL = "valiTimeCell";
	private static final String PREORDER_CELL = "preOrderCell";
	private static final String VOLUME_CELL = "volumeCell";
	private static final String BUYTIMES_CELL = "buyTimesCell";
	private static final String CHECKED = "已认证";
	private static final String REGISTERED_NOCHECKED = "已注册未认证";
	private static final String BAKGROUND_REGISERED_NOCHECKED = "后台注册未认证";
	private static final String NOREGISTERED = "未注册";
	private static final String SAME = "一致";
	private static final String DIFFIRENT = "不一致";
	private static final String YES = "是";
	private static final String NO = "否";
	private static final String CHECKINDEX="checkIndex";
	private static final String DESCRIPTIONINDEX="descriptionIndex";
	private static final String REGISTERINDEX="registerIndex";
	private static final String REGISTERTIMEINDEX="registerTimeIndex";
	private static final String VALIINDEX="valiIndex";
	private static final String VALITIMEINDEX="valiTimeIndex";
	private static final String DEALTIMEINDEX="dealTimeIndex";
	private static final String PREORDERINDEX="preOrderIndex";
	private static final String VOLUMEINDEX="volumeIndex";
	private static final String BUYTIMESINDEX="buyTimesIndex";
	
	
	
	/**
	 * 根据标题名称取得索引
	 * @param sheet
	 * @param headName
	 * @return
	 */
	public static int getCellIndexByHeadName(Sheet sheet,String headName)
	{
		Row headRow=sheet.getRow(1);
		int index=-1;
		int size = headRow.getLastCellNum();
		for(int i=0;i<size;i++)
		{
			Cell cell=headRow.getCell(i);
			
			String value=cell.getStringCellValue();
			if(cell!=null&&value!=null&&value.trim().equals(headName))
			{
				index=cell.getColumnIndex();
				return index;
			}
		}
		return index;
	}
	
	/**
	 * 根据标题名称及开始索引位置，取得索引
	 * @param sheet
	 * @param headName
	 * @return
	 */
	public static int getCellIndexByHeadName(Sheet sheet,String headName, int startIndex)
	{
		Row headRow=sheet.getRow(1);
		int index=-1;
		int size = headRow.getLastCellNum();
		for(int i=startIndex;i<size;i++)
		{
			Cell cell=headRow.getCell(i);
			
			String value=cell.getStringCellValue();
			if(cell!=null&&value!=null&&value.trim().equals(headName))
			{
				index=cell.getColumnIndex();
				return index;
			}
		}
		return index;
	}
	
	
	/**
	 * 根据版本得到excel工作区
	 * @param pushFile
	 * @return
	 * @throws IOException
	 */
	public static Workbook getWorkBook(String name,InputStream inputStream) throws IOException
	{
		Workbook workbook = null;
		//根据版本创建
		if (WDWUtil.isExcel2003(name))  
        {  
			workbook = new HSSFWorkbook(inputStream);  
        }  
        else  
        {  
        	workbook = new XSSFWorkbook(inputStream);  
        }  
		return workbook;
	}
	
	
	
	
	/**
	 * 根据行索引得到样式
	 * @param workbook
	 * @param index
	 * @return
	 */
	private static CellStyle getCellStyleByRow(Workbook workbook, int index)
	{
		Row firstContentRow=workbook.getSheetAt(0).getRow(index);
		CellStyle contentStyle = firstContentRow.getCell(0).getCellStyle();
		return contentStyle;
	}
	
	/**
	 * 创建单元格
	 * @param content
	 * @param row
	 * @param index
	 * @param cellStyle
	 */
	private static void createCell(String content,Row row, int index, CellStyle cellStyle)
	{
		Cell desCell = row.createCell(index);
		desCell.setCellValue(content);
		desCell.setCellStyle(cellStyle);
	}
	
	
	
	 
	 /**
	  * 判断是否为数字
	  * @param str
	  * @return
	  */
	 public static boolean isNumeric(String str)
     {
		   if(str==null||str.equals(""))
		   {
			   return false;
		   }
           Pattern pattern = Pattern.compile("[0-9]*");
           Matcher isNum = pattern.matcher(str);
           if( !isNum.matches() )
           {
                 return false;
           }
           return true;
     }
	 
	 /**
	  * 没有标题列时，创建标题列
	  * @param workbook
	  * @param headRow
	  * @param headStyle
	  */
	 public static void createHeadCell(Map<String,Integer> indexMap ,Row headRow,CellStyle headStyle)
	 {
		//新添加两列，并设置样式
			if(indexMap.get(CHECKINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("核实", headRow, columnIndex, headStyle);
			}
			if(indexMap.get(DESCRIPTIONINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("备注", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(REGISTERINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("注册", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(REGISTERTIMEINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("注册时间", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(VALIINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("认证", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(VALITIMEINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("认证时间", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(DEALTIMEINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("首次成功交易时间", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(PREORDERINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("支付订金次数", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(VOLUMEINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("成单次数", headRow, columnIndex, headStyle);
			}
			
			if(indexMap.get(BUYTIMESINDEX)==-1)
			{
				int columnIndex = headRow.getLastCellNum();
				createCell("成功交易次数", headRow, columnIndex, headStyle);
			}
	 }
	
	 
	 /**
	  * 检查标题是否存在，并返回索引，不存在的列号返回-1,所有结果保存在map返回
	  * @param workbook
	  * @return
	  */
	public static Map<String,Integer> checkHeadIndex(Workbook workbook)
	{
		Map<String,Integer> map = new HashMap<String,Integer>();
		int checkIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "核实");
		int descriptionIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "备注");
		int registerIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "注册", 6);
		int registerTimeIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "注册时间");
		int approveIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "认证", 6);
		int approveTimeIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "认证时间");
		int dealTimeIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "首次成功交易时间");
		int preOrderIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "支付订金次数");
		int volumeIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "成单次数");
		int buyTimesIndex = getCellIndexByHeadName(workbook.getSheetAt(0), "成功交易次数");
		map.put(CHECKINDEX, checkIndex);
		map.put(DESCRIPTIONINDEX, descriptionIndex);
		map.put(REGISTERINDEX, registerIndex);
		map.put(REGISTERTIMEINDEX, registerTimeIndex);
		map.put(VALIINDEX, approveIndex);
		map.put(VALITIMEINDEX, approveTimeIndex);
		map.put(DEALTIMEINDEX, dealTimeIndex);
		map.put(PREORDERINDEX, preOrderIndex);
		map.put(VOLUMEINDEX, volumeIndex);
		map.put(BUYTIMESINDEX, buyTimesIndex);
		return map;
		
	}
	
	/**时间格式转换如:20152018125423
	 * @param time
	 * @return
	 */
	public static String timeFormat(String time)
	{
		StringBuffer dtime = new StringBuffer();
		dtime.append(time.substring(0, 4));
		dtime.append("-");
		dtime.append(time.substring(4, 6));
		dtime.append("-");
		dtime.append(time.substring(6,8));
		dtime.append(" ");
		dtime.append(time.substring(8,10));
		dtime.append(":");
		dtime.append(time.substring(10,12));
		dtime.append(":");
		dtime.append(time.substring(12, 14));
		return dtime.toString();
	}
	
	 
	 public static void main(String[] args)
	{
		ExcelUtils excel = new ExcelUtils();
		System.out.println(excel.isNumeric("d123"));
	}

}
