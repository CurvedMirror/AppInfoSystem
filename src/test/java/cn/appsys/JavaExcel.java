package cn.appsys;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.junit.Test;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;


/**
 * @author jie
 * @date 2019/3/4 -19:43
 */
public class JavaExcel {
    WritableWorkbook book;

    /**
     *
     */
    @Test
    public void importToExcel() {
        try {
            System.out.println("开始生成");
            book = Workbook.createWorkbook(new File("test.xls"));
            WritableSheet sheet = book.createSheet("first sheet",0);
            Label label = new Label(0,0,"This is test");
            sheet.addCell(label);
            book.write();
            book.close();
            System.out.println("生成结束");
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出到java
     */
    @Test
    public void exportToJava(){
        try {
            Workbook bookReader = Workbook.getWorkbook(new File("test.xls"));
            //第一个
            Sheet sheet = bookReader.getSheet(0);
            //获取第一个单元格的内容的值
            Cell cell = sheet.getCell(0,0);
            String content = cell.getContents();
            System.out.println("This is the content"+content);
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }

    }
}
