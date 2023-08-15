package com.honortone.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honortone.api.entity.Inventory;
import com.honortone.api.entity.InventoryOut;
import com.honortone.api.mapper.OutStockMapper;
import com.honortone.api.service.OutStockService;
import com.ktg.common.annotation.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Service
public class OutStockServiceImpl extends ServiceImpl<OutStockMapper, Inventory> implements OutStockService {

    @Autowired
    private OutStockMapper outStockMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public String outStock(String cpno) {

        String returnMessage = "";
        InventoryOut inventoryOut = new InventoryOut();
        System.out.println(cpno);
        Inventory inventory = outStockMapper.checkCreateTime(cpno);
        System.out.println(inventory.getUid().toString());
        BeanUtil.copyProperties(inventory, inventoryOut, true);
        System.out.println(inventoryOut.toString());
        int n = outStockMapper.insertInventoryOut(inventoryOut);
        if (n > 0) {
            int n1 = outStockMapper.deleteInventoryById(inventory.getId());
            if (n1 > 0) {
                returnMessage = "下架成功";
            } else {
                returnMessage = "下架失败，删除库存失败";
            }
        } else {
            returnMessage = "下架失败，下架表保存失败";
        }
        return returnMessage;

    }

    /**
     * 定时任务（半年重检提醒）
     * */
    @Log(title = "半年重检提醒")
    @Scheduled(cron = "0 0 07 * * ?")
    public void task() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -6);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String recTime = simpleDateFormat.format(calendar.getTime());
        System.out.println(recTime);

        // 查询库存成品是否超过半年
        List<Inventory> list = outStockMapper.checkTimeInfo(recTime);
        if (list.size() > 0) {
            sendEmail(list);
        }
    }

//    @Override
//    public String outStock(String cpno) {
//
//        String returnMessage = "";
//        InventoryOut inventoryOut = new InventoryOut();
//        Inventory inventory = outStockMapper.checkCreateTime(cpno);
//        if (inventory == null)
//            return returnMessage = "未查询到该成品";
//
//        // Date类型转为LocalDateTime类型
//        Instant instant = inventory.getProduction_date().toInstant();
//        ZoneId zoneId = ZoneId.systemDefault();
//        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zoneId);
//        LocalDateTime currentDate = LocalDateTime.now();
//        // 获取当前日期与过账日期之间月数
//        long between = ChronoUnit.MONTHS.between(currentDate, dateTime);
//        System.out.println(between);
//        if (between <= -6) {
//            // 到期未重检,直接下架处理,并发邮件通知
//            BeanUtil.copyProperties(inventory, inventoryOut);
//            int n = outStockMapper.insertInventoryOut(inventoryOut);
//            if (n > 0) {
//                outStockMapper.deleteInventoryById(inventory.getId());
//                returnMessage = "入库时间大于半年，已做下架处理，请根据邮件提示重检";
//            }
//            System.out.println("入库时间大于6个月");
//            returnMessage = sendEmail(inventoryOut);
//        } else {
////            int n = outStockMapper.updateStatus(cpno);
////            if (n > 0) {
////                returnMessage = "成品已出库";
////            } else {
////                returnMessage = "成品出库失败，请检查成品UID是否输入正确";
////            }
//            returnMessage = "成品在库且不需要重检";
//        }
//        return returnMessage;
//
//    }

    public void sendEmail(List<Inventory> list) {

        try {
            System.out.println("进入");

            //1. 创建一个工作簿对象
            XSSFWorkbook workbook2 = new XSSFWorkbook();
            //2. 创建一个工作表对象
            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
            // 设置表头
            String[] headers = {"工单", "型号", "PO", "批次", "UID", "数量", "库位", "工厂", "过账日期"};
            XSSFRow headerRow = sheet2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Cell cell = null;
            // 在表格中写入数据
            int rowCount = 1;

            for (int i = 0;i < list.size();i++) {
                XSSFRow row = sheet2.createRow(rowCount++);
                int cellCount = 0;
                // 工单
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getWo() == null ? "" : list.get(i).getWo().toString());
                // PN
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getPn() == null ? "" : list.get(i).getPn().toString());
                // PO
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getPo() == null ? "" : list.get(i).getPo().toString());
                // 批次
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getBatch() == null ? "" : list.get(i).getBatch().toString());
                // UID
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getUid() == null ? "" : list.get(i).getUid().toString());
                // 数量
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getUid_no());
                // 库位
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getStock() == null ? "" : list.get(i).getStock().toString());
                // 工厂
                cell = row.createCell(cellCount++);
                cell.setCellValue(list.get(i).getPlant() == null ? "" : list.get(i).getPlant().toString());
                // 过账日期
                cell = row.createCell(cellCount++);
                Date date = list.get(i).getProduction_date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                cell.setCellValue(simpleDateFormat.format(date));

            }
            // 自适应宽度
            for (int i = 0; i < headers.length; i++) {
                sheet2.autoSizeColumn(i);
            }
            //4. 创建文件并保存
            File file = new File("半年重检成品.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
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
            helper2.setSubject("重检提醒");
            helper2.setText("请查收附件，谢谢！", true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            helper2.addAttachment(MimeUtility.encodeWord("半年重检成品.xlsx"), file);
            //7. 发送邮件
            javaMailSender.send(message2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String sendEmail2(InventoryOut inventoryOut) {

        String returnMessage = "";
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // 设置发件人、收件人、抄送、密送、主题、内容和附件
            helper.setFrom(from);
            helper.setTo("tingming.jiang@honortone.com");
            helper.setCc("tingming.jiang@honortone.com");
            // 密送
            // helper.setBcc("tingming.jiang@honortone.com");
            helper.setSubject("重检提醒");
            helper.setText("库存超过半年，请重检！库位：" + inventoryOut.getStock().toString() + "、型号：" + inventoryOut.getPn().toString() +
                    "、批次：" + inventoryOut.getBatch().toString(), true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            // helper.addAttachment(MimeUtility.encodeWord("example.xlsx"), file);
            // 发送邮件
            javaMailSender.send(message);
            returnMessage = "发送成功";

        } catch (Exception e) {
            e.printStackTrace();
            returnMessage = "发送失败";
        }
        return returnMessage;

    }
}
