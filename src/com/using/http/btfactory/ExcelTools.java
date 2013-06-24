package com.using.http.btfactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * excel操作工具类
 * @author using
 */
public class ExcelTools {
	
	/**
	 * 读取excel
	 */
	public static List<String> getDownUrl(String filePath) {
		List<String> list = new ArrayList<String>(0);
		Workbook book;
		try {
			File file = new File(filePath);
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("UTF-8");
			book = Workbook.getWorkbook(file, workbookSettings);
			Sheet sheet = book.getSheet(0);
			for (int i = 0; i < sheet.getRows(); i++) {
				for (int j = 0; j < sheet.getColumns(); j++) {
					Cell cell = sheet.getCell(j, i);
					String result = cell.getContents();
					list.add(result);
				}
			}
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 写入excel
	 */
	public static void saveDownUrl(List<LinkBeans> listLinkBeans,String filePath) {
		WritableWorkbook wwb = null;
		try {
			// 创建可写入的工作簿对象
			wwb = Workbook.createWorkbook(new File(filePath));
			if (wwb != null) {
				// 在工作簿里创建可写入的工作表，第一个参数为工作表名，第二个参数为该工作表的所在位置
				WritableSheet ws = wwb.createSheet("BT工场种子下载地址", 0);
				if (ws != null) {
					// 行
					for (int i = 0; i < listLinkBeans.size(); i++) {
						LinkBeans linkBeans = listLinkBeans.get(i);
						// 列
						for (int j = 0; j < 1; j++) {
							// Label构造器中有三个参数，第一个为列，第二个为行，第三个则为单元格填充的内容
							Label label = new Label(j, i, linkBeans.getLinkUrl());
							// 将被写入数据的单元格添加到工作表
							ws.addCell(label);
						}
					}
					// 从内存中写入到文件
					wwb.write();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				wwb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
