package com.ktg.mes.fg.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.qos.logback.classic.Logger;
import cn.hutool.core.bean.BeanUtil;
import com.ktg.common.annotation.Log;
import com.ktg.mes.fg.domain.*;
import com.ktg.mes.fg.domain.Dto.FgTosAndTosListDto;
import com.ktg.mes.fg.domain.Dto.ShipmentPart;
import com.ktg.mes.fg.mapper.FgChecklistMapper;
import com.ktg.mes.fg.mapper.FgInventoryMapper;
import com.ktg.mes.fg.mapper.FgTosMapper;
import com.sap.conn.jco.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import static com.ktg.mes.fg.utils.SAPUtil.ABAP_AS_POOLED;

/**
 * 后台执行程序
 *
 * @author JiangTingming
 * @EnableScheduling开启定时任务 spring的注解，引入spring的依赖即可
 * @date 2023-03-28
 */
//D:\22-FGWhsSystem_New\FGWhsSystem_New_Backend\fgwhssystem\ktg-mes\target\classes\com\ktg\mes\fgtils\AutoDownload_BitchPrint.class

@EnableScheduling
@RestController
@Component("Auto")
@MapperScan("com.ktg.mes.mapper")
@RequestMapping("/autotimer")
public class AutoDownload_BitchPrint {
    @Autowired
    FgChecklistMapper fgChecklistMapper;
    @Autowired
    FgTosMapper fgTosMapper;

    @Autowired
    FgInventoryMapper fgInventoryMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    // 日志记录（用于记录用户自行点击从SAP获取数据）
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AutoDownload_BitchPrint.class);

	/**
	 * 测试定时邮件
	 * */
//    @Scheduled(cron = "0 13 09 * * ?")
//    public void sendmail() throws javax.mail.MessagingException, IOException {
//        sendMail();
//        System.out.println("ok");
//    }

//    @Scheduled(cron = "0 34 10 * * ?")
//    public void lineTest() {
//        String fgChecklist = fgChecklistMapper.checkLine("000001576354");
//        System.out.println("生产线：" + fgChecklist);
//    }

	/**
	 * 测试定时导入走货资料
	 * */
//    @Scheduled(cron = "0 41 09 * * ?")
//    public void doTask1() throws Exception {
////        devanning_PMC();
////        generateTO_NO();
//        execute();
//    }

//    @Scheduled(cron = "0 13 14 * * ?")
//    public void dotask2() throws Exception {
//
//        QHisReady_Timer();
//    }

    /**
     * 定时任务  格式：秒 分 时 日 月 星期 年份
     * 每天7,11,15,19,23点执行（每隔4小时执行【7起始时间，4间隔时间】）
     * 也可@Scheduled(cron = "0 0 7,11,15,19,23 * * ?"),逗号区分
     */
//    @Scheduled(cron = "0 0 7/4 * * ?")
//    public void task01() throws Exception {
//        execute();
//    }

    /**
     * 后台执行程序 10分钟执行一次 -- ( 1.每晚12 : 30，自动从SAP导入成品送检单信息 2.日期范围默认是当天日期开始向后推1个月)
     */
    @Log(title = "定时导入SAP 101入数数据")
//    @Scheduled(cron = "0 */10 * * * ?")
    public void doTask() throws Exception {
        execute();
    }

    /**
     * SAP导入成品送检单信息
     */
    @Log(title = "定时导入SAP 101入数数据")
    public String execute() throws Exception {

        String returnMessage = "";
        try {
            System.out.println("Starting..  BitchPrint start.....");

            // 如果物料模糊查询,过账日期不传值,则默认当天（日期格式20230331）
            // 如果物料模糊查询,过账日期不传值,则默认当天; 如果开始日期有值,结束日期没有值,则结束日期默认当天
//			Date date = new Date();
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//			String startDate = simpleDateFormat.format(date);
            // 获取当天日期和一个月后日期
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusMonths(1);
            // 将此日期时间与时区相结合以创建 ZonedDateTime
            ZonedDateTime zonedDateTimeStart = startDate.atZone(ZoneId.systemDefault());
            ZonedDateTime zonedDateTimeEnd = endDate.atZone(ZoneId.systemDefault());
            // 本地时间线LocalDateTime到即时时间线Instant时间戳
            Instant instantStart = zonedDateTimeStart.toInstant();
            Instant instantEnd = zonedDateTimeEnd.toInstant();
            // UTC时间(世界协调时间,UTC + 00:00)转北京(北京,UTC + 8:00)时间
            Date startdate = Date.from(instantStart);
            Date enddate = Date.from(instantEnd);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            // 开始日期 结束日期
            String startDate1 = sdf.format(startdate);
            String endDate1 = sdf.format(enddate);
            SAPUtil sap = new SAPUtil();

            List<FgChecklist> fgChecklistList = new ArrayList<>();
            // 查询SAP过账接口1100工厂
            List<FgChecklist> list1 = sap.Z_HTMES_YMMR04("1100", "660*", startDate1, endDate1, "101");
            logger.info("1100工厂获取660*数量：" + list1.size());
            //insertInto(list1);
            List<FgChecklist> list2 = sap.Z_HTMES_YMMR04("1100", "650*", startDate1, endDate1, "101");
            System.out.println("---" + list2.size());
            logger.info("1100工厂获取650*数量：" + list2.size());
            //insertInto(list2);

            // 查询SAP过账接口5000工厂
            List<FgChecklist> list3 = sap.Z_HTMES_YMMR04("5000", "660*", startDate1, endDate1, "101");
            logger.info("5000工厂获取660*数量：" + list3.size());
            //insertInto(list3);
            List<FgChecklist> list4 = sap.Z_HTMES_YMMR04("5000", "650*", startDate1, endDate1, "101");
            logger.info("5000工厂获取650*数量：" + list4.size());
            //insertInto(list4);
            fgChecklistList.addAll(list1);
            fgChecklistList.addAll(list2);
            fgChecklistList.addAll(list3);
            fgChecklistList.addAll(list4);
            insertInto(fgChecklistList);

            returnMessage = "导入成功";
            System.out.println("The end...." + new Date());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            returnMessage = "导入失败";
        }
        return returnMessage;
    }

    /**
     * 定时更新欠货是否已补货状态
     */
    @Log(title = "更新欠货单状态")
    @Scheduled(cron = "0 */3 * * * ?")
    @PostMapping("/timer")
    public String QHisReady_Timer() {

        /**
         * 1.查询TOS管理表中QH状态为3的欠货单 为一个集合1
         * 2.遍历集合1去TOList明细表中查询欠货单对应的欠货明细单 为一个集合2
         * 3.遍历集合2去库存表查询是否已补货:
         *                              1:遍历完都没有补货，集合1直接遍历下一个欠货单，生成下一个集合2
         *                              2:有其中一些欠货单已补货，查询TO管理表该走货单对应备货单状态是否为0,0则UID总数累加，并根据该备货单生成备货明细；若不为0则生成新备货单和备货明细，
         *                                  生成后修改库存状态开已备货
         *   若集合2为空，则该欠货单已补全货，修改状态为已补货状态
         * */

        String returnMessage = "";
        List<FgTos> list1 = fgTosMapper.getQHList();
        if (list1.size() == 0) {
            return returnMessage = "没有欠货单";
        }
        for (int i = 0;i < list1.size();i++) {

            // 是否存在备货单/欠货单
            FgTos fgTos2 = fgTosMapper.checkBHstatus(list1.get(i).getShipmentNO().toString());
            String BH = "";
            if (fgTos2 != null) {
                BH = fgTos2.getTo_No().toString();
            }
            List<FgToList> list2 = fgTosMapper.getToListByQH(list1.get(i).getTo_No() == null ? "" : list1.get(i).getTo_No().toString());
            if (list2.size() == 0) {
                // 更新TOS管理的欠货单状态为已补货（注：补货完成后，下一次执行才会更新 即有一定延迟）
                fgTosMapper.updateTosQH(list1.get(i).getTo_No().toString(), list1.get(i).getShipmentNO().toString());
            } else {
                for (int j = 0;j < list2.size();j++) {
                    FgInventory fgInventory = fgTosMapper.checkInventory(list2.get(j));
                    if (fgInventory != null) {
                        // 批量和欠货总数问题，什么时候拣货完成，定时检测？
                        FgTos fgTos = fgTosMapper.checkBHstatus(list1.get(i).getShipmentNO().toString());
                        if (fgTos != null) {
                            fgTosMapper.sumBH(list2.get(j).getQuantity(), list1.get(i).getShipmentNO().toString());
                            fgTosMapper.updateQHStatus(list2.get(j));
                            FgToList fgToList = new FgToList();
                            fgToList.setStatus(0);
                            fgToList.setTo_No(fgTos.getTo_No().toString());
                            fgToList.setUid(fgInventory.getUid().toString());
                            fgToList.setPo(list2.get(j).getPo().toString());
                            fgToList.setPn(list2.get(j).getPn().toString());
                            fgToList.setQuantity(list2.get(j).getQuantity());
                            fgToList.setSap_qty(fgTos.getSap_qty() + list2.get(j).getQuantity());
                            fgToList.setStock(fgInventory.getStock().toString());
                            fgToList.setBatch(fgInventory.getBatch().toString());
                            fgTosMapper.insertFgTolist(fgToList);
                            // fgTosMapper.updatebatchSum(list2.get(j).getQuantity(), fgTos.getTo_No().toString());
                            fgTosMapper.updateInventoryStatus(fgInventory.getUid().toString());

                        } else {
                            FgTos fgTos1 = new FgTos();
                            BeanUtil.copyProperties(list1.get(i), fgTos1, true);
                            fgTos1.setTo_No(generateTo_No("备货单"));
                            fgTos1.setStatus(0);
                            // fgTos1.setSap_qty(list2.get(j).getQuantity());
                            fgTosMapper.insertFgTos(fgTos1);
                            fgTosMapper.updateQHStatus(list2.get(j));
                            FgToList fgToList = new FgToList();
                            fgToList.setStatus(0);
                            fgToList.setTo_No(fgTos1.getTo_No().toString());
                            fgToList.setUid(fgInventory.getUid().toString());
                            fgToList.setPo(list2.get(j).getPo().toString());
                            fgToList.setPn(list2.get(j).getPn().toString());
                            fgToList.setQuantity(fgTos1.getSap_qty());
                            fgToList.setSap_qty(fgTos1.getSap_qty());
                            fgToList.setStock(fgInventory.getStock().toString());
                            fgToList.setBatch(fgInventory.getBatch().toString());
                            fgTosMapper.insertFgTolist(fgToList);
                            fgTosMapper.updateInventoryStatus(fgInventory.getUid().toString());
                        }
                    }
                }
            }
        }
        return returnMessage;
    }

    // @Scheduled(cron = "0 */1 * * * ?")
//    /**
//     * 定时更新欠货是否已补货状态
//     */
//    @Log(title = "更新欠货单状态")
//    @PostMapping("/timer")
//    public String QHisReady_Timer() {
//
//        /**
//         * 1.查询TOS管理表中QH状态为3的欠货单 为一个集合1
//         * 2.遍历集合1去TOList明细表中查询欠货单对应的欠货明细单 为一个集合2
//         * 3.遍历集合2去库存表查询是否已补货:
//         *                              1:遍历完都没有补货，集合1直接遍历下一个欠货单，生成下一个集合2
//         *                              2:有其中一些欠货单已补货，查询TO管理表该走货单对应备货单状态是否为0,0则UID总数累加，并根据该备货单生成备货明细；若不为0则生成新备货单和备货明细，
//         *                                  生成后修改库存状态开已备货
//         *   若集合2为空，则该欠货单已补全货，修改状态为已补货状态
//         * */
//
//        String returnMessage = "";
//        List<FgTos> list1 = fgTosMapper.getQHList();
//        if (list1.size() == 0) {
//            return returnMessage = "没有欠货单";
//        }
//        for (int i = 0;i < list1.size();i++) {
//
//            // 是否存在备货单/欠货单
//            FgTos fgTos2 = fgTosMapper.checkBHstatus(list1.get(i).getShipmentNO().toString());
//            String BH = "";
//            if (fgTos2 != null) {
//                BH = fgTos2.getTo_No().toString();
//            }
//            List<FgToList> list2 = fgTosMapper.getToListByQH(list1.get(i).getTo_No() == null ? "" : list1.get(i).getTo_No().toString());
//            if (list2.size() == 0) {
//                // 更新TOS管理的欠货单状态为已补货（注：补货完成后，下一次执行才会更新 即有一定延迟）
//                fgTosMapper.updateTosQH(list1.get(i).getTo_No().toString(), list1.get(i).getShipmentNO().toString());
//            } else {
//                for (int j = 0;j < list2.size();j++) {
//                    FgInventory fgInventory = fgTosMapper.checkInventory(list2.get(j));
//                    List<FgInventory> fgInventory2 = fgTosMapper.checkInventory2(list2.get(j));
//                    if (fgInventory != null) {
//                        // 批量和欠货总数问题，什么时候拣货完成，定时检测？
//                        FgTos fgTos = fgTosMapper.checkBHstatus(list1.get(i).getShipmentNO().toString());
//                        if (fgTos != null) {
//                            fgTosMapper.sumBH(list2.get(j).getQuantity(), list1.get(i).getShipmentNO().toString());
//                            fgTosMapper.updateQHStatus(list2.get(j));
//                            FgToList fgToList = new FgToList();
//                            fgToList.setStatus(0);
//                            fgToList.setTo_No(fgTos.getTo_No().toString());
//                            fgToList.setUid(fgInventory.getUid().toString());
//                            fgToList.setPo(list2.get(j).getPo().toString());
//                            fgToList.setPn(list2.get(j).getPn().toString());
//                            fgToList.setQuantity(list2.get(j).getQuantity());
//                            fgToList.setSap_qty(fgTos.getSap_qty() + list2.get(j).getQuantity());
//                            fgToList.setStock(fgInventory.getStock().toString());
//                            fgToList.setBatch(fgInventory.getBatch().toString());
//                            fgTosMapper.insertFgTolist(fgToList);
//                            // fgTosMapper.updatebatchSum(list2.get(j).getQuantity(), fgTos.getTo_No().toString());
//                            fgTosMapper.updateInventoryStatus(fgInventory.getUid().toString());
//
//                        } else {
//                            FgTos fgTos1 = new FgTos();
//                            BeanUtil.copyProperties(list1.get(i), fgTos1, true);
//                            fgTos1.setTo_No(generateTo_No("备货单"));
//                            fgTos1.setStatus(0);
//                            // fgTos1.setSap_qty(list2.get(j).getQuantity());
//                            fgTosMapper.insertFgTos(fgTos1);
//                            fgTosMapper.updateQHStatus(list2.get(j));
//                            FgToList fgToList = new FgToList();
//                            fgToList.setStatus(0);
//                            fgToList.setTo_No(fgTos1.getTo_No().toString());
//                            fgToList.setUid(fgInventory.getUid().toString());
//                            fgToList.setPo(list2.get(j).getPo().toString());
//                            fgToList.setPn(list2.get(j).getPn().toString());
//                            fgToList.setQuantity(fgTos1.getSap_qty());
//                            fgToList.setSap_qty(fgTos1.getSap_qty());
//                            fgToList.setStock(fgInventory.getStock().toString());
//                            fgToList.setBatch(fgInventory.getBatch().toString());
//                            fgTosMapper.insertFgTolist(fgToList);
//                            fgTosMapper.updateInventoryStatus(fgInventory.getUid().toString());
//                        }
//                    } else if (fgInventory2.size() > 0) {
//                        FgTos fgTos3 = new FgTos();
//                        fgTos3.setShipmentNO(list1.get(i).getShipmentNO().toString());
//                        fgTos3.setSap_qty(list1.get(i).getSap_qty());
//                        fgTos3.setCarNo(list1.get(i).getCarNo().toString());
//                        fgTos3.setPlant(list1.get(i).getPlant().toString());
//                        fgTos3.setStatus(0);
//                        if ("".equals(BH)) {
//                            BH = generateTo_No("备货单");
//                            fgTos3.setTo_No(BH);
//                            fgTosMapper.insertFgTos(fgTos3);
//                        }
//                        for (int k = 0;k < fgInventory2.size();k++) {
//                            FgToList fgToList = new FgToList();
//                            fgToList.setTo_No(BH);
//                            fgToList.setUid(fgInventory2.get(k).getUid().toString());
//                            fgToList.setPo(fgInventory2.get(k).getPo().toString());
//                            fgToList.setStatus(0);
//                            fgToList.setPn(fgInventory2.get(k).getPartnumber().toString());
//                            fgToList.setSap_qty(list1.get(i).getSap_qty() + fgInventory2.get(k).getQuantity());
//                            fgToList.setBatch(fgInventory2.get(k).getBatch().toString());
//                            fgToList.setStock(fgInventory2.get(k).getStock().toString());
//                            fgToList.setQuantity(fgInventory2.get(k).getQuantity());
//                            // 总数等于欠货总数
//                            if (fgInventory2.get(k).getSumQuantity() == list2.get(j).getSap_qty()) {
//                                //
//                               long qty = list1.get(i).getSap_qty() + fgInventory2.get(k).getQuantity();
//                               fgTos3.setSap_qty(list1.get(i).getSap_qty() + fgInventory2.get(k).getQuantity());
//                               fgTosMapper.insertFgTolist(fgToList);
//                               fgInventoryMapper.updateStatusByUid(fgInventory2.get(k).getUid().toString());
//                               fgTosMapper.updateTosQuantity(qty, BH);
//                               fgTosMapper.updateTolistQuantity(qty, BH);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return returnMessage;
//    }

    //获取SAP-P/N,批次，数量
    @PostMapping("/task")
    public List<SAPPBQEntity> getSAPPBQ() {
        List<SAPPBQEntity> list = new ArrayList<SAPPBQEntity>();
        try {
            System.out.println("start-----" + new Date());
//获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_JAVA_GET_STOCK");
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//给功能函数输入参数
            JCoParameterList input = function.getImportParameterList();
//销售文件
            input.setValue("I_WERKS", "1100");
//抬头项
            JCoTable ZDC = function.getTableParameterList().getTable("IT_LGORT");
            ZDC.appendRow();
            ZDC.setValue("SIGN", "I");
            ZDC.setValue("OPTION", "EQ");
            ZDC.setValue("LOW", "FG80");
//            ZDC.appendRow();
//            ZDC.setValue("SIGN", "I");
//            ZDC.setValue("OPTION", "EQ");
//            ZDC.setValue("LOW", "CU80");
//            ZDC.appendRow();
//            ZDC.setValue("SIGN", "I");
//            ZDC.setValue("OPTION", "EQ");
//            ZDC.setValue("LOW", "BS81");
//            ZDC.appendRow();
//            ZDC.setValue("SIGN", "I");
//            ZDC.setValue("OPTION", "EQ");
//            ZDC.setValue("LOW", "BC81");
            try {
                function.execute(destination); //函数执行
                JCoTable rs = function.getTableParameterList().getTable("ET_MCHB");
                for (int j = 0; j < rs.getNumRows(); j++) {
                    SAPPBQEntity sAPPBQEntity = new SAPPBQEntity();
                    if ("FG80".equals(rs.getString("LGORT")) || "FG80".equals(rs.getString("LGORT"))) {
                        sAPPBQEntity.setLocation(rs.getString("LGORT"));
                    } else {
                        sAPPBQEntity.setLocation("RH80");
                    }
                    sAPPBQEntity.setRoom(rs.getString("GROES"));
                    sAPPBQEntity.setPN(rs.getString("MATNR"));
                    sAPPBQEntity.setBatch(rs.getString("CHARG"));
                    if ("FG80".equals(rs.getString("LGORT")) || "FG80".equals(rs.getString("LGORT"))) {
                        sAPPBQEntity.setQuantity(rs.getFloat("CLABS") + rs.getFloat("CUMLM"));
                    } else {
                        sAPPBQEntity.setQuantity(rs.getFloat("CUMLM"));
                    }
                    list.add(sAPPBQEntity);
                    rs.nextRow();
                }
            } catch (AbapException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("end-----" + new Date());
        for (int i = 0;i < list.size();i++) {
            System.out.println(list.get(i).toString());
        }
        return list;
    }


    /**
     * SAP导入PMC确认的走货信息并判断是否拆箱
     */
//    public void devanning_PMC() throws Exception {
//        String isok = "";
//        try {
//            System.out.println("starting..  download start.....");
//
//            Date date = new Date();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//            String startDate = simpleDateFormat.format(date);
//
//            SAPUtil sapUtil = new SAPUtil();
//            List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS(startDate, startDate);
//            if (list.size() == 0)
//                throw new RuntimeException("not find info...");
//
//            // 从SAP导入走货资料
//            for (int i = 0; i < list.size(); i++) {
//                FgShipmentInfo fgShipmentInfo = list.get(i);
//                if (fgShipmentInfo.getSapPn() != null && fgShipmentInfo.getLastComfirm() != null && fgShipmentInfo.getLastComfirm().toString().equals("PMC") &&
//                        fgTosMapper.checkInfoFs(fgShipmentInfo.getSapPn().toString(), fgShipmentInfo.getPo().toString(), fgShipmentInfo.getQuantity(), fgShipmentInfo.getLastComfirm().toString()) == 0) {
//
//                    int n = fgTosMapper.insertFgShipmentInfo(fgShipmentInfo);
//                }
//            }
//
//            // 根据 PN PO关联库存表查询（是否拆箱操作）
//            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPart("PMC");
//            for (int i = 0; i < list1.size(); i++) {
//
//                List<FgTosAndTosListDto> fgTosAndTosListDtos = fgTosMapper.getTosAndTOListInfo(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "PMC");
//                if (fgTosAndTosListDtos.size() == 0) {
//                    System.out.println("不需要拆箱");
//                    continue;
//                } else {
//                    long sum_uidno = 0;
//                    for (int j = 0; j < fgTosAndTosListDtos.size(); j++) {
//                        FgUnpacking fgUnpacking = new FgUnpacking();
//                        if (fgTosAndTosListDtos.get(j).getSum_uidno() == list1.get(i).getBatchsum()) {
//                            // 退出整个j循环，直接查询下一条PN  PO数据（回到i循环）
//                            break;
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() > list1.get(i).getBatchsum()) {
//                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
//                            long qty = -1;
//                            while (sum_uidno >= list1.get(i).getBatchsum()) {
//                                qty = (sum_uidno - list1.get(i).getBatchsum()) == 0 ? 0 : fgTosAndTosListDtos.get(j).getQuantity() - (sum_uidno - list1.get(i).getBatchsum());
//                                fgUnpacking.setUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                fgUnpacking.setUid_no(fgTosAndTosListDtos.get(j).getQuantity());
//                                fgUnpacking.setDemandQty(qty);
//                                fgUnpacking.setCreateTime(fgTosAndTosListDtos.get(j).getCreateTime());
//                                int n = fgTosMapper.insertFgUnpacking(fgUnpacking);
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "PMC");
//                                // 邮件提醒拆箱
//                                System.out.println("邮件提醒拆箱！");
//                                break;
//                            }
//                            if (qty != -1 && qty >= 0) {
//                                break;
//                            }
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() < list1.get(i).getBatchsum()) {
//                            long qty = list1.get(i).getBatchsum() - fgTosAndTosListDtos.get(j).getSum_uidno();
//                            // 邮件提醒欠料？
//                            System.out.println("邮件提醒欠料！");
//                        }
//                    }
//                }
//            }
//            isok = "OK";
//            System.out.println("The end...." + new Date());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * SAP导入船务确认的走货信息并产生备货单/走货单
     */
//    public void generateTO_NO() throws Exception {
//        String isok = "";
//        try {
//            System.out.println("starting..  download start.....");
//
//            Date date = new Date();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//            String startDate = simpleDateFormat.format(date);
//            SAPUtil sap = new SAPUtil();
//
//            SAPUtil sapUtil = new SAPUtil();
//            List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS(startDate, startDate);
//            if (list.size() == 0)
//                throw new RuntimeException("not find info...");
//
//            // 从SAP导入走货资料（判空、查重、船务）
//            for (int i = 0; i < list.size(); i++) {
//                FgShipmentInfo fgShipmentInfo = list.get(i);
//                if (fgShipmentInfo.getSapPn() != null && fgShipmentInfo.getLastComfirm() != null && fgShipmentInfo.getLastComfirm().toString().equals("船务") &&
//                        fgTosMapper.checkInfoFs(fgShipmentInfo.getSapPn().toString(), fgShipmentInfo.getPo().toString(), fgShipmentInfo.getQuantity(), fgShipmentInfo.getLastComfirm().toString()) == 0) {
//
//                    int n = fgTosMapper.insertFgShipmentInfo(fgShipmentInfo);
//                }
//            }
//
//            // 统计相同走货信息（相同则数量累加【相当于去重】）
//            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPart("船务");
//            for (int i = 0; i < list1.size(); i++) {
//
//                System.out.println("测试2" + list1.get(i).getPn().toString() + list1.get(i).getPo().toString() + "ss" + list1.get(i).getBatchsum());
//                // 走货信息逐条 根据 PN PO 船务关联库存表查询
//                List<FgTosAndTosListDto> fgTosAndTosListDtos = fgTosMapper.getTosAndTOListInfo(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                if (fgTosAndTosListDtos.size() == 0) {
//                    continue;
//                } else {
//                    long sum_uidno = 0;
//                    FgTos fgTos = new FgTos();
//                    FgToList fgToList = new FgToList();
//                    // 一条走货信息关联到多条库存数据，该数据根据条件逐条产生备货单/欠货单
//                    for (int j = 0; j < fgTosAndTosListDtos.size(); j++) {
//                        boolean flag = fgTosMapper.checkInfoTos(fgTosAndTosListDtos.get(j).getPn().toString(), fgTosAndTosListDtos.get(j).getPo().toString(), fgTosAndTosListDtos.get(j).getShipmentNO().toString()) == 0;
//                        System.out.println("测试" + fgTosMapper.checkInfoTos(fgTosAndTosListDtos.get(j).getPn().toString(), fgTosAndTosListDtos.get(j).getPo().toString(), fgTosAndTosListDtos.get(j).getShipmentNO().toString()));
//                        // 走货信息的批量 == 关联库存数据的总数量（即uid_no之和）
//                        if (fgTosAndTosListDtos.get(j).getSum_uidno() == list1.get(i).getBatchsum()) {
//                            // 将数据插入TO管理和TO明细表
//                            // To管理
//                            if (flag) {
//                                // a,b为对象
//                                // BeanUtils.copyProperties(a, b);
//                                // BeanUtils是org.springframework.beans.BeanUtils，a拷贝到b
//                                // cn.hutool.core.bean.BeanUtil  a拷贝到b
//                                // BeanUtils是org.apache.commons.beanutils.BeanUtils，b拷贝到a
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgTos, true);
//                                fgTos.setSapPn(fgTosAndTosListDtos.get(j).getPn().toString());
//                                // 产生备货单
//                                fgTos.setTo_No(generateTo_No("备货单"));
//                                fgTos.setStatus(1);
//                                int n = fgTosMapper.insertFgTos(fgTos);
//                                // 更新状态，确保下次执行不会再关联该条走货信息（PMC和船务公用一个SQL语句）
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                                System.out.println("产生备货单：等于");
//                            }
//                            System.out.println("产生备货单1");
//                            // TO明细
//                            BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                            // 使用TO管理的备货单（即产生关联）
//                            fgToList.setTo_No(fgTos.getTo_No().toString());
//                            fgToList.setStatus(1);
//                            int n = fgTosMapper.insertFgTolist(fgToList);
//
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() > list1.get(i).getBatchsum()) {
//                            if (flag) {
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgTos, true);
//                                fgTos.setSapPn(fgTosAndTosListDtos.get(j).getPn().toString());
//                                // 产生备货单
//                                fgTos.setTo_No(generateTo_No("备货单"));
//                                int n = fgTosMapper.insertFgTos(fgTos);
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                                System.out.println("产生备货单：大于");
//                            }
//                            System.out.println("产生备货单2");
//                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
//                            long qty = -1;
//                            if (sum_uidno > list1.get(i).getBatchsum()) {
//                                // 提醒拆分该成品单后 减去多出的部分 生成欠货单（后续判断库存表是否有拆分的数据 再生成新备货单）
//                                qty = sum_uidno - list1.get(i).getBatchsum();
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList.setUid("");
//                                fgToList.setQuantity(qty);
//                                fgToList.setStatus(0);
//                                fgToList.setTo_No(generateTo_No("欠货单"));
//                                int n = fgTosMapper.insertFgTolist(fgToList);
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                                AutoDownload_BitchPrint autoDownloadBitchPrint = new AutoDownload_BitchPrint();
//                                autoDownloadBitchPrint.sendMail();
//                                System.out.println("邮件提醒某UID成品单需要拆分成***");
//                            } else if (sum_uidno == list1.get(i).getBatchsum()) {
//                                // 是否刚好等于，是 则备货后直接break退出循环
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList.setStatus(1);
//                                fgToList.setTo_No(fgTos.getTo_No().toString());
//                                int n = fgTosMapper.insertFgTolist(fgToList);
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                                break;
//                            } else {
//                                // TO明细
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList.setTo_No(fgTos.getTo_No().toString());
//                                fgToList.setStatus(1);
//                                int n = fgTosMapper.insertFgTolist(fgToList);
//                            }
////                            if (qty != -1 && qty >= 0) {
////                                break;
////                            }
//                            System.out.println("邮件提醒还未拆箱");
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() < list1.get(i).getBatchsum()) {
//                            // 总数小于 批次总数则产生备货单
//                            if (flag) {
//                                long qty = list1.get(i).getBatchsum() - fgTosAndTosListDtos.get(j).getSum_uidno();
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgTos, true);
//                                fgTos.setSapPn(fgTosAndTosListDtos.get(j).getPn().toString());
//                                // 产生欠货单
//                                fgTos.setTo_No(generateTo_No("欠货单"));
//                                fgToList.setStatus(0);
//                                int n = fgTosMapper.insertFgTos(fgTos);
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//
//                                // 将欠料数存到TO明细表
//                                fgToList.setQuantity(qty);
//                                fgToList.setTo_No(fgTos.getTo_No().toString());
//                                fgToList.setStatus(0);
//                                int n2 = fgTosMapper.insertFgTolist(fgToList);
//                            }
//                            // 邮件提醒欠料？
//                            System.out.println("邮件提醒欠料！");
//                        }
//                    }
//                }
//            }
//
//            isok = "OK";
//            System.out.println("The end...." + new Date());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 根据FG + BH或QH + 年、月、日、时、分、秒、毫秒（三位数） 生成备货单/备货单
     *
     * @param tono 描述是备货单还是欠货单
     * @return String
     **/
    public String generateTo_No(String tono) {

        String To_No = "";
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            if (tono.equals("备货单")) {
                // BH + 年月日时分秒 毫秒
                To_No = "BH" + simpleDateFormat.format(date);
                System.out.println(To_No);
            } else if (tono.equals("欠货单")) {
                // QH + 年月日时分秒 毫秒
                To_No = "QH" + simpleDateFormat.format(date);
                System.out.println(To_No);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return To_No;

    }

    /**
     * 导入走货资料(用于产生备料单/欠料单)
     **/
    public void checkInventory() {

        FgInventory inventory = new FgInventory();

    }

    /**
     * 导入走货资料(用于产生备料单/欠料单)
     **/
    public void DownloadShipmentInfo() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String startDate = sdf.format(date);
        SAPUtil sapUtil = new SAPUtil();
        List<FgShipmentInfo> list1 = sapUtil.Z_HTMES_ZSDSHIPLS("20230418", "20230418");
        List<FgShipmentInfo> list2 = new ArrayList();
        if (list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                FgShipmentInfo fgShipmentInfo = new FgShipmentInfo();
                fgShipmentInfo = list1.get(i);
                if (fgShipmentInfo.getSapPn().toString().substring(0, 3).equals("660")) {
                    list2.add(fgShipmentInfo);
                }
            }
        } else {
            System.out.println("未查询到走货资料！");
        }


    }

    /**
     * 导入数据库(用于生成送检单模板)
     **/
    public void insertInto(List<FgChecklist> list) {

        for (int i = 0; i < list.size(); i++) {
            FgChecklist fgChecklist = list.get(i);
            System.out.println(fgChecklist.toString());
            System.out.println(fgChecklist.getSap101().toString() + "---" + fgChecklist.getPn().toString());
            String sap101 = fgChecklist.getSap101().toString();
            String pn = fgChecklist.getPn().toString();
            // 判断该数据是否已在数据库
//            List<FgChecklist> list1 = fgChecklistMapper.checkinfo(sap101, pn);
            int n = fgChecklistMapper.checkinfo(sap101, pn);
            // System.out.println(list1.size());
            if (n == 0) {
                // 获取拉别 (建立索引，否则很慢)
                String line = fgChecklistMapper.checkLine(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
                // 获取检验员
                String qasign = fgChecklistMapper.checkQasign(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
                System.out.println(fgChecklist.getWo().toString().toString() + "----121212");
                fgChecklist.setQaSign(qasign);
                fgChecklist.setLine(line);
                // 插入数据
                fgChecklistMapper.insertFgChecklist(fgChecklist);
            } else {
                System.out.println(fgChecklist.getWo().toString().toString() + "----121212");
                System.out.println("该条数据已存在" + fgChecklist.getSap101().toString());
            }
        }
    }

    /**
     * 根据日期生成备货单
     **/
    public String generateTo_No() {

        String To_No = "";
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            // FG + 年月日时分秒 毫秒
            To_No = "BH" + simpleDateFormat.format(date);
            System.out.println(To_No);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return To_No;

    }

    /**
     * 根据日期生成欠料单
     **/
    public String generateTo_No_1() {

        String To_No = "";
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            // FG + 年月日时分秒 毫秒
            To_No = "QH" + simpleDateFormat.format(date);
            System.out.println(To_No);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return To_No;

    }

    /**
     * 邮件
     */
    public void sendMail() throws MessagingException, javax.mail.MessagingException, IOException {

        // 创建 Excel 表格
//		HSSFWorkbook workbook = new HSSFWorkbook();
//		HSSFSheet sheet = workbook.createSheet("测试表格");
//		HSSFRow header = sheet.createRow(0);
//		header.createCell(0).setCellValue("ID");
//		header.createCell(1).setCellValue("Name");
//		sheet.createRow(1).createCell(0).setCellValue("1");
//		sheet.getRow(1).createCell(1).setCellValue("张三");
//		sheet.createRow(2).createCell(0).setCellValue("2");
//		sheet.getRow(2).createCell(1).setCellValue("李四");
//
//		// 创建邮件消息
//		MimeMessage message = javaMailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//		// 设置消息目标、标题、内容
//		helper.setFrom(from);
//		helper.setTo("tingming.jiang@honortone.com");
//		helper.setSubject("发送带表格附件的邮件");
//		helper.setText("jtm");
//
//		// 将 Excel 表格写入输出流并作为附件发送
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		workbook.write(out);
//		helper.addAttachment("测试表格.xlsx", new ByteArrayResource(out.toByteArray()));
//
//		// 发送消息
//		javaMailSender.send(message);
//		System.out.println("邮件已发送");

        try {
            System.out.println("进入");
            List<String> contentList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                contentList.add("jtm" + i + "," + "20," + "man");
            }

            //1. 创建一个工作簿对象
            XSSFWorkbook workbook2 = new XSSFWorkbook();
            //2. 创建一个工作表对象
            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
            // 设置表头
            String[] headers = {"姓名", "年龄", "性别"};
            XSSFRow headerRow = sheet2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 在表格中写入数据
            int rowCount = 1;
            for (String content : contentList) {
                String[] contents = content.split(",");
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
            File file = new File("example.xlsx");
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
            helper2.setSubject("拆箱提醒");
            helper2.setText("请查收附件，谢谢！", true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            helper2.addAttachment(MimeUtility.encodeWord("example.xlsx"), file);
            //7. 发送邮件
            javaMailSender.send(message2);
        } catch (Exception e) {
            e.printStackTrace();
        }


//		String path = "D:\\EXCEL\\";
//		//创建一个工作簿
//		Workbook workbook1 = new XSSFWorkbook();
//		//创建一个工作表
//		Sheet sheet1 = workbook1.createSheet("sheet01");
//		//创建行
//		Row row01 = sheet1.createRow(0);//行从0开始，第一行
//		//创建一个单元格
//		Cell cell01 = row01.createCell(0);//列也是从0开始
//		//设置单元格中的内容
//		cell01.setCellValue("码农");
//
//		Cell cell02 = row01.createCell(1);
//		cell02.setCellValue("IT民工");
//
//		FileOutputStream outputStream = new FileOutputStream(path + "07码农.xlsx");
//		//工作簿通过流写出
//		workbook.write(outputStream);
//		outputStream.close();
//
//		System.out.println(">>>>>>>>excel 07 write finish");

//		//复杂邮件
//		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
//		messageHelper.setText("<b>jtm</b>", true);
//		messageHelper.setSubject("注塑机数据");
//		//定义Excel文件
//		File file = new File("C://Users/jiangtingming/Desktop/5#&6# 注塑机打印数据（2022.09.01-2022.12.30）.xlsx");
//		//使用ByteArrayResource将Excel文件转换为InputStreamSource
//		InputStreamSource source = new ByteArrayResource(org.apache.commons.io.FileUtils.readFileToByteArray(file));
//
//		messageHelper.addAttachment("注塑机打印数据", source, "application/vnd.ms-excel;charset=UTF-8");//D://hh/kk.jpg
//		messageHelper.setTo("tingming.jiang@honortone.com");
//		messageHelper.setFrom(from);
//		javaMailSender.send(mimeMessage);
    }

}
