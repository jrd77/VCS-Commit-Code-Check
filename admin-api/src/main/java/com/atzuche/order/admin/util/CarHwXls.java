/**
 * 
 */
package com.atzuche.order.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangjing
 * @function
 * @datetime 2014年9月4日下午2:06:02
 * @version 1.0
 */
public class CarHwXls {
	private static Logger logger = LoggerFactory.getLogger(CarHwXls.class);
	
	/**
     * 读取xls文件内容
     * 
     * @return List<XlsDto>对象
     * @throws IOException
     *             输入/输出(i/o)异常
     */
    public static Map<String, String> readXls(String xlspath) throws IOException {
    	Map<String, String> map = new HashMap<String, String>();
    	/** 检查文件名是否为空或者是否是Excel格式的文件 */   
        if (xlspath == null || !(WDWUtil.isExcel2003(xlspath) || WDWUtil.isExcel2007(xlspath)))  
        {  
            logger.info("文件名不是excel格式");
            map.put("msg", "文件名不是excel格式");
            return map;  
        }  
  
        /** 检查文件是否存在 */  
        File file = new File(xlspath);  
        if (file == null || !file.exists())  
        {  
        	logger.info("文件不存在");
            map.put("msg", "文件不存在");
            return map; 
        }  
        
        InputStream is = new FileInputStream(xlspath);
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        /** 根据版本选择创建Workbook的方式 */  
        
        Workbook wb = null;  

        if (WDWUtil.isExcel2003(xlspath))  
        {  
            wb = new HSSFWorkbook(is);  
        }  
        else  
        {  
            wb = new XSSFWorkbook(is);  
        }  
        
//        CellStyle cellStyle = wb.createCellStyle(); 
//        DataFormat format = wb.createDataFormat(); 
//        cellStyle.setDataFormat(format.getFormat("@"));
        
        
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
            Sheet sheet = wb.getSheetAt(numSheet);   //HSSF
//            System.out.println("sheet:" + sheet);
            if (sheet == null) {
                continue;
            }
            // 循环行Row,从第一行开始。
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);  //HSSF
//                System.out.println("row:"+row);
                if (row == null) {
                    continue;
                }
                // 循环列Cell
                // 0学号 1姓名 2学院 3课程名 4 成绩
                // for (int cellNum = 0; cellNum <=4; cellNum++) {
                Cell cell0 = row.getCell(0);  //HSSF
//                cell0.setCellStyle(cellStyle);  //设置为文本型
//                System.out.println("@@cell0:" + getValue(cell0));
                if (cell0 == null || !getValue(cell0).startsWith("96779")) {   //不已96779开头的记录。  
                    continue;
                }
                
                
                Cell cell1 = row.getCell(1);  //HSSF
//                cell1.setCellStyle(cellStyle);  //设置为文本型
//                System.out.println("$$cell1:" + getValue(cell1));
                if (cell1 == null || getValue(cell1).length() != 8) {
                    continue;
                }
               
                
                map.put(getValue(cell0), getValue(cell1));
            }
        }
//        System.out.println("SIZE:" + list.size());
        return map;
    }

    /**
     * 得到Excel表中的值
     * 
     * @param hssfCell
     *            Excel中的每一个格子
     * @return Excel中每一个格子中的值
     */
    @SuppressWarnings("static-access")
    public static String getValue(Cell cell) {  //HSSF
        /*if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(cell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(cell.getStringCellValue());
        }*/
    	String cellValue = "";
    	if (null != cell)  
        {  
            // 以下是判断数据的类型  
            switch (cell.getCellType())  
            {  
            case HSSFCell.CELL_TYPE_NUMERIC: // 数字  
//                cellValue = cell.getNumericCellValue() + "";  
                DecimalFormat df = new DecimalFormat("0");     //避免科学计数法显示。
                cellValue = df.format(cell.getNumericCellValue());  
                
//            	cellValue = cell.getStringCellValue();  
                break;  

            case HSSFCell.CELL_TYPE_STRING: // 字符串  
                cellValue = cell.getStringCellValue();  
                break;  

            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean  
                cellValue = cell.getBooleanCellValue() + "";  
                break;  

            case HSSFCell.CELL_TYPE_FORMULA: // 公式  
                cellValue = cell.getCellFormula() + "";  
                break;  

            case HSSFCell.CELL_TYPE_BLANK: // 空值  
                cellValue = "";  
                break;  

            case HSSFCell.CELL_TYPE_ERROR: // 故障  
                cellValue = "非法字符";  
                break;  

            default:  
                cellValue = "未知类型";  
                break;  
            }  
        }  
    	return cellValue;
    	
    }
    
    
    /**
     * 得到Excel表中的值
     * 
     * @param hssfCell
     *            Excel中的每一个格子
     * @return Excel中每一个格子中的值
     */
    @SuppressWarnings("static-access")
    public static String getValueNew(Cell cell) {  //HSSF
    	String cellValue = "";
    	if (null != cell)  
        {  
            // 以下是判断数据的类型  
            switch (cell.getCellType())  
            {  
            case HSSFCell.CELL_TYPE_NUMERIC: // 数字  
                cellValue = cell.getNumericCellValue()+"";
                break;  

            case HSSFCell.CELL_TYPE_STRING: // 字符串  
                cellValue = cell.getStringCellValue();  
                break;  

            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean  
                cellValue = cell.getBooleanCellValue() + "";  
                break;  

            case HSSFCell.CELL_TYPE_FORMULA: // 公式  
                cellValue = cell.getCellFormula() + "";  
                break;  

            case HSSFCell.CELL_TYPE_BLANK: // 空值  
                cellValue = "";  
                break;  

            case HSSFCell.CELL_TYPE_ERROR: // 故障  
                cellValue = "非法字符";  
                break;  

            default:  
                cellValue = "未知类型";  
                break;  
            }  
        }  
    	return cellValue;
    	
    }
    
    
	public static String convertCellToString(Cell cell) {
		
		// 如果为null会抛出异常，应当返回空字符串
		if (cell == null)
			return "";
		// POI对单元格日期处理很弱，没有针对的类型，日期类型取出来的也是一个double值，所以同样作为数值类型
		// 解决日期2006/11/02格式读入后出错的问题，POI读取后变成“02-十一月-2006”格式
		if (cell.toString().contains("-")) {
			String ans = "";
			try {
				ans = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
			} catch (Exception e) {
				ans = cell.toString();
			}
			return ans;
		}

		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 判断是否是“02-十一月-2006”格式的日期类型
	 */
	private static boolean checkDate(String str) {
		String[] dataArr = str.split("-");
		try {
			if (dataArr.length == 3) {
				int x = Integer.parseInt(dataArr[0]);
				String y = dataArr[1];
				int z = Integer.parseInt(dataArr[2]);
				if (x > 0 && x < 32 && z > 0 && z < 10000 && y.endsWith("月")) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, String> map = null;
		try {
			map = CarHwXls.readXls("d://硬件号.xlsx");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map.Entry<String, String> entry : map.entrySet()){
			System.err.println("entry.getKey():" + entry.getKey() + " --- " + entry.getValue());
		}
	}

}

class WDWUtil  
{  
    /** 
     * @描述：是否是2003的excel，返回true是2003 
     * @参数：@param filePath　文件完整路径 
     * @参数：@return 
     * @返回值：boolean 
     */
    public static boolean isExcel2003(String filePath)  
    {  
        return filePath.matches("^.+\\.(?i)(xls)$");  
    }  
  
    
    /** 
     * @描述：是否是2007的excel，返回true是2007 
     * @参数：@param filePath　文件完整路径 
     * @参数：@return 
     * @返回值：boolean 
     */  
    public static boolean isExcel2007(String filePath)  
    {  
        return filePath.matches("^.+\\.(?i)(xlsx)$");  
    }
}  