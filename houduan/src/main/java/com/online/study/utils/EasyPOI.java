package com.online.study.utils;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

//工具类
public class EasyPOI {
	
	/**
	 * 导出功能
	 * @param response
	 * @param object
	 * @param list
	 * @param title
	 */
	public static void EasyExport(HttpServletResponse response,Class<?> object,List<?> list,String fileName,String title) {
		try {
			// 设置响应输出的头类型
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename="+fileName);
			Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(title, title), object, list);
			workbook.write(response.getOutputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入功能
	 * @param <E>
	 * @param inputstream
	 * @param pojoClass
	 * @return
	 */
	public static <E> List<E> EasyImport(InputStream inputstream,Class<?> pojoClass){
		List<E> list=null;
		try {
			ImportParams params = new ImportParams();
			params.setHeadRows(1);
			list = ExcelImportUtil.importExcel(inputstream, pojoClass, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}


