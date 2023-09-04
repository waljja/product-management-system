package com.ktg;

import ch.qos.logback.classic.Logger;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.bstek.ureport.console.UReportServlet;
import com.honortone.api.ProductTransferApiApplication;
import com.ktg.mes.fg.domain.FgChecklist;
import com.ktg.mes.fg.domain.FgShipmentInfo;
import com.ktg.mes.fg.domain.FgToList;
import com.ktg.mes.fg.domain.FgTos;
import com.ktg.mes.fg.mapper.FgChecklistMapper;
import com.ktg.mes.fg.utils.AutoDownload_BitchPrint;
import com.ktg.mes.fg.utils.SAPUtil;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import javax.xml.crypto.Data;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 启动程序
 * 
 * @author ruoyi
 * alert by JiangTingming 2023-3-20
 */

@EnableScheduling
@RestController
@Component
@EnableAsync
@ImportResource("classpath:ureport-console-context.xml")
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@MapperScan("com.ktg.mes.mapper")
public class RuoYiApplication
{

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AutoDownload_BitchPrint.class);
    @Autowired
    private FgChecklistMapper fgChecklistMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  KTM-MES启动成功   ლ(´ڡ`ლ)ﾞ  \n");
    }

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("SqlServer","sqlserver");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    @Bean
    public ServletRegistrationBean urportServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new UReportServlet());
        bean.addUrlMappings("/ureport/*");
        return bean;
    }

    // 调用SAP接口根据PN查询PO测试
    @Test
    public void SapTest() throws Exception {

        AutoDownload_BitchPrint autoDownloadBitchPrint = new AutoDownload_BitchPrint();
        autoDownloadBitchPrint.execute();

    }

    @Test
    public void Date01() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -6);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(calendar.getTime());
        System.out.println(date);



    }

    // 获取PN相关信息导入数据库测试
    // 执行  格式：秒 分 时 日 月 星期 年份
    @Scheduled(cron = "0 10 09 * * ?")
    @Test
    public void SapTest02() {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDate = simpleDateFormat.format(date);
        System.out.println("开始时间：" + startDate);

        SAPUtil sapUtil = new SAPUtil();
        // 有效PO 3700177401    5800800649   5300041177
        List<FgChecklist> fgChecklists = sapUtil.Z_HTMES_YMMR04("1100", "660*650*", "20220303", "20220303", "101");
        System.out.println("数据量：" + fgChecklists.size());
        for (FgChecklist fgChecklist : fgChecklists) {
            System.out.println(fgChecklist.toString());
        }
    }

    /**
     * 测试：通过客户PN查询鸿通PN
     */
    @Test
    public void SapTest03() {

        SAPUtil sapUtil = new SAPUtil();
        // 300005118905
        List<String[]> list = sapUtil.getHt_PnByKh_Pn("300005118905", "");
        System.out.println("数据量：" + list.size());
        for (int i = 0; i < list.size(); i++) {
            String[] s = list.get(i);
            System.out.println(s[1]);
        }
    }

    @Test
    public void localDateTest() {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(1);
        System.out.println(startDate);
        System.out.println(endDate);
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
        System.out.println(sdf.format(startdate));
        System.out.println(sdf.format(enddate));
    }

    // 正式环境
    @Test
    public void SapTest05() {

        SAPUtil sapUtil = new SAPUtil();
        // 300005118905
        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS("20230424","20230424");
        System.out.println("数据量：" + list.size());
        for (int i = 0; i < list.size(); i++) {
            FgShipmentInfo fgShipmentInfo = list.get(i);
            System.out.println(fgShipmentInfo.toString());
        }
    }

    @Test
    public void generateTo_No(){

        String To_No = "";
        try {
//            Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//            int second = calendar.get(Calendar.SECOND);
//            To_No = "FG" + year + month + day + hour + minute + second;
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            To_No = "FG" + simpleDateFormat.format(date);
            System.out.println(To_No);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    @Test
    public void DateTest01() {

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 获取当天和后2天的走货编号（共三天的数据）
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDate = simpleDateFormat.format(date);
        String endDate = simpleDateFormat.format(calendar.getTime());
        System.out.println(startDate + "==" + endDate);

        SAPUtil sapUtil = new SAPUtil();
        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS_2("A-20230901-62099");
        System.out.println(simpleDateFormat.format(list.get(0).getShipmentDate()));
        for (int i = 0;i < list.size();i++) {
            System.out.println(list.get(i).toString());
        }
    }

    @Test
    public void sendMail() throws MessagingException, javax.mail.MessagingException {
        //简单邮件
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("测试");
        mailMessage.setText("JiangTingming");
        mailMessage.setTo("tingming.jiang@honortone.com");
        mailMessage.setFrom(from);
        javaMailSender.send(mailMessage);
        //复杂邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
        messageHelper.setText("<b>jtm</b>",true);
        messageHelper.setSubject("K");
        messageHelper.addAttachment("KN5B6931.jpg",new File("C://Users/jiangtingming/Desktop/KN5B6931.jpg"));//D://hh/kk.jpg
        messageHelper.setTo("\"tingming.jiang@honortone.com\"");
        messageHelper.setFrom(from);
        javaMailSender.send(mimeMessage);
    }

    @Test
    public void testMethod() {

        FgTos fgTos = new FgTos();
        fgTos = testMethod1("11","11",11,"11","11",1);
        System.out.println(fgTos.toString());


    }
    public FgTos testMethod1(String toNo, String shipmentNo, long quantity, String carNo, String plent, int status) {

        FgTos fgTos = new FgTos();
        fgTos.setTo_No(toNo);
        fgTos.setShipmentNO(shipmentNo);
        fgTos.setSap_qty(quantity);
        fgTos.setCarNo(carNo);
        fgTos.setPlant(plent);
        fgTos.setStatus(status);

        return fgTos;

    }

    @Test
    public void aas() {
        int[] A = {1, 2, 3, 4, 5};
        int B = 5;
        int result = countSubsets(A, B);
        System.out.println("Total number of subsets with sum equal to B: " + result);
    }

    public int countSubsets(int[] A, int B) {

        int[][] dp = new int[A.length][B + 1];
        // 初始化第一行
        if (A[0] <= B) {
            dp[0][A[0]] = 1;
        }
        // 初始化第一列
        for (int i = 0; i < A.length; i++) {
            dp[i][0] = 1;
        }
        // 填充dp数组
        for (int i = 1; i < A.length; i++) {
            for (int j = 1; j <= B; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= A[i]) {
                    dp[i][j] += dp[i - 1][j - A[i]];
                }
            }
        }
        System.out.println(dp[A.length - 1][B]);
        return dp[A.length - 1][B];
    }





}
