package cn.appsys.tools;

import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;


import cn.appsys.pojo.AppInfo;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {

	public static void exportData(HttpServletResponse response,
			String sheetName, String[] titles,
			List<AppInfo> appInfoList) throws Exception {
		String fileName = sheetName + UUID.randomUUID();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ new String((fileName + ".xls").getBytes(), "iso-8859-1"));
		// 根据传进来的file对象创建可写入的Excel工作薄
		OutputStream os = response.getOutputStream();

		try {
			// （1）创建工作室
			WritableWorkbook wbook = null;
			wbook = Workbook.createWorkbook(os);

			// （2）创建工作表对象
			WritableSheet sheet = wbook.createSheet(sheetName, 0);
			// 生成表头
			Label label = null;
			for (int i = 0; i < titles.length; i++) {
				label = new Label(i, 0, titles[i]);
				sheet.addCell(label);
			}

			// 判断表中是否有数据
			if (appInfoList != null && appInfoList.size() > 0) {
				// 循环写入表中数据
				for (int i = 0; i < appInfoList.size(); i++) {

					// 转换成map集合{activyName:测试功能,count:2}
					AppInfo appInfo = (AppInfo)appInfoList.get(i);

					// 循环输出map中的子集：既列值
					sheet.addCell(new Label(0, i + 1, String.valueOf(appInfo.getId())));
					sheet.addCell(new Label(1, i + 1, String.valueOf(appInfo.getSoftwareName())));
					sheet.addCell(new Label(2, i + 1, String.valueOf(appInfo.getAPKName())));
					sheet.addCell(new Label(3, i + 1, String.valueOf(appInfo.getSoftwareSize())));
					sheet.addCell(new Label(4, i + 1, String.valueOf(appInfo.getFlatformName())));
					sheet.addCell(new Label(5, i + 1, String.valueOf(appInfo.getCategoryLevel1Name())));
					sheet.addCell(new Label(6, i + 1, String.valueOf(appInfo.getCategoryLevel2Name())));
					sheet.addCell(new Label(7, i + 1, String.valueOf(appInfo.getCategoryLevel3Name())));
					sheet.addCell(new Label(8, i + 1, String.valueOf(appInfo.getStatusName())));
					sheet.addCell(new Label(9, i + 1, String.valueOf(appInfo.getDownloads())));
					sheet.addCell(new Label(10, i + 1, String.valueOf(appInfo.getVersionNo())));
				}

			}
			wbook.write();
			wbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
