package com.ktg.mes.fg.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ktg.mes.fg.domain.Dto.ShipmentList;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/email")
public class ExcelController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 测试获取上传的走货资料数据、测试产生拆箱/备货单等报表的邮件通知（将上传的数据赋值给报表再发送邮件）
     * @param file
     * 状态：OK
     * */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        try {
            //List<String> content = new ArrayList<>();
            List<ShipmentList> list = new ArrayList<>();

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

            XSSFSheet sheet = workbook.getSheetAt(0);
            System.out.println("所用行数：" + sheet.getLastRowNum());

            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                XSSFRow row = (XSSFRow) iterator.next();
                ShipmentList shipmentList = new ShipmentList();
                System.out.println("行数：" + row.getRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();

                if (row.getRowNum() >= 11) {
                    int n = row.getRowNum();
                    XSSFRow row1 = sheet.getRow(n);
                    XSSFCell cell1 = row1.getCell(0);
                    if (cell1 == null || cell1.equals("") || cell1.equals("0")) {
                        continue;
                    } else {
                        while (cellIterator.hasNext()) {

                            XSSFCell cell = (XSSFCell) cellIterator.next();

                            // 读取第一列
                            if (cell.getColumnIndex() == 0) {
                                shipmentList.setId((long) cell.getNumericCellValue());
                            }
                            if (cell.getColumnIndex() == 1) {
                                shipmentList.setClient(cell.toString());
                            }
                            if (cell.getColumnIndex() == 2) {
                                shipmentList.setSap_pn(cell.toString());
                            }
                            if (cell.getColumnIndex() == 3) {
                                shipmentList.setCustomer_pn(cell.toString());
                            }
                            if (cell.getColumnIndex() == 8) {
                                shipmentList.setQty((long) cell.getNumericCellValue());
                            }
                        }
                    }
                    list.add(shipmentList);
                    System.out.println(shipmentList.toString());
                }
            }
            workbook.close();
            System.out.println(list.toString());

            //1. 创建一个工作簿对象
            XSSFWorkbook workbook2 = new XSSFWorkbook();
            //2. 创建一个工作表对象
            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
            // 设置表头
            String[] headers = {"id", "client", "sap_pn", "customer_pn", "qty"};
            XSSFRow headerRow = sheet2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 在表格中写入数据
            int rowCount = 1;
            for (ShipmentList content : list) {
                String s = content.toString1();
                String[] contents = s.split(",");
                XSSFRow row = sheet2.createRow(rowCount++);
                for (int i = 0; i < contents.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(contents[i]);
                }
            }
            // 自适应宽度
            for (int i = 0; i < headers.length; i++) {
                sheet2.autoSizeColumn(i);
            }
            //4. 创建文件并保存
            File file1 = new File("example.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file1);
            workbook2.write(outputStream);
            outputStream.close();
            workbook2.close();
            //5. 创建一个邮件对象
            MimeMessage message2 = javaMailSender.createMimeMessage();
            MimeMessageHelper helper2 = new MimeMessageHelper(message2, true);
            //6. 设置发件人、收件人、抄送、密送、主题、内容和附件
            helper2.setFrom(from);
            helper2.setTo("tingming.jiang@honortone.com");
            helper2.setCc("tingming.jiang@honortone.com");
            // 密送
            // helper2.setBcc("tingming.jiang@honortone.com");
            helper2.setSubject("测试邮件");
            helper2.setText("请查收附件，谢谢！", true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            helper2.addAttachment(MimeUtility.encodeWord("example.xlsx"), file1);
            //7. 发送邮件
            javaMailSender.send(message2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ok";


//        System.out.println("jinru");
//        // 1. 检查文件是否为空
//        if (file.isEmpty()) {
//            return "请选择上传文件！";
//        }
//        // 2. 创建一个工作簿对象
//        try (InputStream is = file.getInputStream()) {
//            XSSFWorkbook workbook = new XSSFWorkbook(is);
//            // 3. 获取第一个工作表
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            // 4. 遍历行并打印单元格的值
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    System.out.print(cell.getStringCellValue() + "\t");
//                }
//                System.out.println();
//            }
//            return "上传成功！";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "上传失败！";
//        }
    }




}
