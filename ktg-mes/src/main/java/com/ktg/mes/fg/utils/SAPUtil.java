package com.ktg.mes.fg.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ktg.mes.fg.domain.FgChecklist;
import com.ktg.mes.fg.domain.FgShipmentInfo;
import com.ktg.mes.fg.domain.SAPPBQEntity;
import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * SAP接口
 * @author JiangTingming
 * @date 2023-03-18 start
 */
public class SAPUtil {

    // 172.31.2.3正式 （变量字符可随意改变）
    static String ABAP_AS = "ABAP_AS_WITHOUT_POOL_LEDLIGHT";
    // 172.31.2.6
    static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL_LEDLIGHT";
    static String ABAP_MS = "ABAP_MS_WITHOUT_POOL_LEDLIGHT";

    public SAPUtil() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sap.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, p.getProperty("JCO_ASHOST"));
        System.out.println(p.getProperty("JCO_ASHOST"));

        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, p.getProperty("JCO_SYSNR"));
        System.out.println(p.getProperty("JCO_SYSNR"));

        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, p.getProperty("JCO_CLIENT"));
        System.out.println(p.getProperty("JCO_CLIENT"));
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, p.getProperty("JCO_USER"));
        System.out.println(p.getProperty("JCO_USER"));

        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, p.getProperty("JCO_PASSWD"));
        System.out.println(p.getProperty("JCO_PASSWD"));

        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, p.getProperty("JCO_LANG"));
        System.out.println(p.getProperty("JCO_LANG"));

        createDataFile(ABAP_AS, "jcoDestination", connectProperties);

        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, p.getProperty("JCO_POOL_CAPACITY"));
        System.out.println(p.getProperty("JCO_POOL_CAPACITY"));
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, p.getProperty("JCO_PEAK_LIMIT"));
        System.out.println(p.getProperty("JCO_PEAK_LIMIT"));
        createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);
        createDataFile(ABAP_MS, "jcoDestination", connectProperties);
    }

    private void createDataFile(String name, String suffix, Properties properties) {
        File cfg = new File(name + "." + suffix);
        if (!cfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
            }
        }
    }

    /**
     *  从SAP导入成品送检单数据；
     *  650开头的物料只取客户为CK00和”物料描述“包含TAOKIT或LCDKIT的信息
     * @param plant pn startDate endDate movetype
     * @return List<FgChecklist>
     */
    public List<FgChecklist> Z_HTMES_YMMR04(String plant, String pn, String startDate, String endDate, String movetype) {

        List<FgChecklist> list = new ArrayList();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try {
            // 获取连接池（连接池连了一次后就默认作为下次的连接地址，需要重新定义连接池变量【在配置文件更换地址没用】）
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_MS);
            // 可查看连接地址
            System.out.println(destination);
            destination.ping();
            System.out.println(pn);
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YMMR04");
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

            // 给功能函数输入参数
            JCoParameterList input = function.getImportParameterList();
            // 参数--工厂，物料号，开始时间，结束时间，移动类型
            input.setValue("I_MATNR", pn);
            input.setValue("I_WERKS", plant);
            input.setValue("I_BUDATS", startDate);
            input.setValue("I_BUDATE", endDate);
            input.setValue("I_BWART", movetype);

            try {
                // 函数执行
                function.execute(destination);
            } catch (AbapException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            // 获取输出
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");

            DateTime dateTime = new DateTime();
            // 注：目前获取的所有数据字段都没有进行非空判断
            for (int j = 0; j < rs.getNumRows(); j++) {
                // 650开头的PN，客户为CK00的才需要写入数据库
                if (rs.getString("MATNR").toString().substring(0, 3).equals("650") && rs.getString("KUNNR").toString().equals("CK00")) {
                    // 获取”物料描述“包含TAOKIT或LCDKIT的信息
                    if (rs.getString("MAKTX").toString().contains("TAOKIT") || rs.getString("MAKTX").toString().contains("LCDKIT")) {
                        FgChecklist fgChecklist = new FgChecklist();
                        // 工厂
                        fgChecklist.setPlant(rs.getString("WERKS"));
                        // PN
                        fgChecklist.setPn(rs.getString("MATNR"));
                        // fromstock
                        fgChecklist.setFromStock(rs.getString("LGORT"));
                        // 生产日期
                        dateTime = DateUtil.parse(rs.getString("BUDAT"));
                        fgChecklist.setProductionDate(dateTime);
                        // 批次/批号
                        fgChecklist.setBatch(rs.getString("CHARG"));
                        // 工单（订单号）
                        fgChecklist.setWo(rs.getString("AUFNR"));
                        // 批量
                        fgChecklist.setBatchQty(rs.getLong("MENGE"));
                        // 物料文件（过账编号）
                        fgChecklist.setSap101(rs.getString("MBLNR"));
                        // 设置uid_no默认值
                        fgChecklist.setUidNo(0l);
                        // 设置默认status
                        fgChecklist.setStatus(0);

                        list.add(fgChecklist);
                        rs.nextRow();
                    } else {
                        rs.nextRow();
                    }
                } else if (rs.getString("MATNR").toString().substring(0, 3).equals("660")){
                    // PN为660开头的直接写入数据库
                    FgChecklist fgChecklist = new FgChecklist();
                    // 工厂
                    fgChecklist.setPlant(rs.getString("WERKS"));
                    // PN
                    fgChecklist.setPn(rs.getString("MATNR"));
                    // fromstock
                    fgChecklist.setFromStock(rs.getString("LGORT"));
                    // 生产日期
                    dateTime = DateUtil.parse(rs.getString("BUDAT"));
                    fgChecklist.setProductionDate(dateTime);
                    // 批次/批号
                    fgChecklist.setBatch(rs.getString("CHARG"));
                    // 工单（订单号）
                    fgChecklist.setWo(rs.getString("AUFNR"));
                    // 批量
                    fgChecklist.setBatchQty(rs.getLong("MENGE"));
                    // 物料文件（过账编号）
                    fgChecklist.setSap101(rs.getString("MBLNR"));
                    // 设置uid_no默认值
                    fgChecklist.setUidNo(0l);
                    // 设置默认status
                    fgChecklist.setStatus(0);

                    list.add(fgChecklist);
                    rs.nextRow();
                } else {
                    rs.nextRow();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return list;
    }

    /**
     * 查询QA检验结果
     * @param plant pn - startDate - endDate
     * @return List<String[]>
     * @自动更新QA结果在什么时候？
     * **/
    public List<String[]> Z_HTMES_YMMR04_2(String plant, String pn,String startDate,String endDate) {

        System.out.println("查询QA检验结果Start~");
        List<String[]> list = new ArrayList();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try {
            // 获取连接池（连接池连了一次后就默认作为下次的连接地址，需要重新定义连接池变量【在配置文件更换地址没用】）
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
            // 可查看连接地址
            System.out.println(destination);
            destination.ping();
            System.out.println(pn);

            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YMMR04");
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

            // 给功能函数输入参数
            JCoParameterList input = function.getImportParameterList();
            // 参数--工厂，物料号，开始时间，结束时间，移动类型
            input.setValue("I_MATNR", pn);
            input.setValue("I_WERKS", plant);
            input.setValue("I_BUDATS", startDate);
            input.setValue("I_BUDATE", endDate);
            System.out.println(pn + plant + startDate + endDate);
            try {
                long before = System.currentTimeMillis();
                // 函数执行
                function.execute(destination);
                long after = System.currentTimeMillis();
                System.out.println("耗时：" + (after - before));
            } catch (AbapException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            // 获取输出
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");

            // 注：目前获取的所有数据字段都没有进行非空判断
            for (int j = 0; j < rs.getNumRows(); j++) {
                String[] data = new String[2];
                if (rs.getString("CHARG") != null && !rs.getString("CHARG").equals("")) {
                    data[0] = rs.getString("CHARG").toString();
                }
                if (rs.getString("BWART") != null && !rs.getString("BWART").equals("")) {
                    data[1] = rs.getString("BWART").toString();
                }
                list.add(data);
                rs.nextRow();
            }
            System.out.println("1221121==" + list.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;

    }

    /**
     * 查询PO信息
     * @param pn
     * @return List<String[]>
     *
     */
    public List<String[]> getPoInfo(String pn) {

        List<String[]> list = new ArrayList();
        try {
            System.out.println(pn);
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
            System.out.println(pn);
            destination.ping();
            System.out.println(pn);
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YSDR16");
            System.out.println(pn);
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

            JCoParameterList input = function.getImportParameterList();
            // 参数--物料号
            input.setValue("I_MATNR", pn);

            try {
                // 函数执行
                function.execute(destination);
            } catch (AbapException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            // 获取输出
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");
            for (int i = 0; i < rs.getNumRows(); i++) {

                String data[] = new String[3];
                // 物料号
                if (rs.getString("MATNR") != null && !"".equals(rs.getString("MATNR"))) {
                    data[0] = rs.getString("MATNR");
                }
                // 客户订单号
                if (rs.getString("BSTKD") != null && !"".equals(rs.getString("BSTKD"))) {
                    data[1] = rs.getString("BSTKD");
                } else {
                    data[1] = "";
                }
                // 收货人采购单号
                if (rs.getString("BSTKD_E") != null && !"".equals(rs.getString("BSTKD_E"))) {
                    data[2] = rs.getString("BSTKD_E");
                }
                list.add(data);
                rs.nextRow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 打印650型号送检单时查询SAP BOM 层次中对应的660型号
     * @param pn plant
     * @return String
     *
     */
    public String get660Info(String pn,String plant) {

        String pn_660 = "";
        try {
            System.out.println(pn + plant);
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
            destination.ping();
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_CS15");
            System.out.println(pn);
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

            JCoParameterList input = function.getImportParameterList();
            // 参数--工厂，物料号，批次
            input.setValue("I_MATNR", pn);
            input.setValue("I_WERKS", plant);

            try {
                // 函数执行
                function.execute(destination);
            } catch (AbapException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            // 获取输出
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");
            if (rs.getString("MATNR") != null && rs.getString("MATNR") != "") {
                pn_660 = rs.getString("MATNR").toString();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pn_660;
    }

    /**
     * 根据客户PN查询鸿通PN/或反之
     * @param khpn htpn
     * @return List<String[]>
     *
     */
    public List<String[]> getHt_PnByKh_Pn(String khpn, String htpn) {

        List<String[]> list = new ArrayList();
        try {
            System.out.println(khpn);
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
            System.out.println(destination);
            destination.ping();
            System.out.println(khpn);
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_CUSTPN");
            System.out.println(khpn);
            if (function == null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

            JCoParameterList input = function.getImportParameterList();
            // 参数--客户物料号
            input.setValue("I_IDNRK", khpn);
            //input.setValue("I_MATNR", htpn);

            try {
                // 函数执行
                function.execute(destination);
            } catch (AbapException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            // 获取输出
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");
            for (int i = 0; i < rs.getNumRows(); i++) {

                String data[] = new String[3];
                // 工厂
                if (rs.getString("WERKS") != null && !"".equals(rs.getString("WERKS"))) {
                    data[0] = rs.getString("WERKS");
                }
                // 鸿通PN
                if (rs.getString("MATNR") != null && !"".equals(rs.getString("MATNR"))) {
                    data[1] = rs.getString("MATNR");
                } else {
                    data[1] = "";
                }
                // 客户PN
                if (rs.getString("IDNRK") != null && !"".equals(rs.getString("IDNRK"))) {
                    data[2] = rs.getString("IDNRK");
                }
                list.add(data);
                rs.nextRow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 导入走货资料
     * @param startDate
     *        endDate
     * @return List<FgShipmentInfo>
     */
    public List<FgShipmentInfo> Z_HTMES_ZSDSHIPLS(String startDate, String endDate) {

        List<FgShipmentInfo> list = new ArrayList();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        try {
            // 获取连接池（连接池连了一次后就默认作为下次的连接地址，需要重新定义连接池变量【在配置文件更换地址没用】）
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_MS);
            // 可查看连接地址
            System.out.println(destination);
            destination.ping();
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_ZSDSHIPLS");
            if (function == null)
                throw new RuntimeException("Z_HTMES_ZSDSHIPLS not found in SAP.");

            // 给功能函数输入参数
            JCoParameterList input = function.getImportParameterList();

            input.setValue("I_SDATE", startDate);
            input.setValue("I_EDATE", endDate);

            try {
                // 函数执行
                function.execute(destination);
            } catch (AbapException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            // 获取输出
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");

            DateTime dateTime = new DateTime();
            // 注：目前获取的所有数据字段都没有进行非空判断
            for (int j = 0; j < rs.getNumRows(); j++) {
                FgShipmentInfo fgShipmentInfo = new FgShipmentInfo();
                // 工厂
                if (rs.getString("WERKS") != null && !"".equals(rs.getString("WERKS"))) {
                    fgShipmentInfo.setPlant(rs.getString("WERKS").toString());
                }
                // SAP型号（PN）
                if (rs.getString("MATNR") != null && !"".equals(rs.getString("MATNR"))) {
                    fgShipmentInfo.setSapPn(rs.getString("MATNR").toString());
                }
                // 走货单
                if (rs.getString("SHIP_NO") != null && !"".equals(rs.getString("SHIP_NO"))) {
                    fgShipmentInfo.setShipmentNO(rs.getString("SHIP_NO").toString());
                }
                // 数量
                if (rs.getString("NTGEW") != null && !"".equals(rs.getString("NTGEW"))) {
                    fgShipmentInfo.setQuantity(rs.getLong("NTGEW"));
                } else {
                    fgShipmentInfo.setQuantity(0l);
                }
                // 卡板数
                if (rs.getString("PELLET") != null && !"".equals(rs.getString("PELLET"))) {
                    fgShipmentInfo.setPelletQty(rs.getLong("PELLET"));
                } else {
                    fgShipmentInfo.setPelletQty(0l);
                }
                // 箱数
                if (rs.getString("BOX") != null && !"".equals(rs.getString("BOX"))) {
                    fgShipmentInfo.setBoxQty(Double.parseDouble(rs.getString("BOX")));
                }
                // PCS/箱
                if (rs.getString("ZZBOX01") != null && !"".equals(rs.getString("ZZBOX01"))) {
                    fgShipmentInfo.setPcsQty(rs.getLong("ZZBOX01"));
                }
                // PO
                if (rs.getString("BSTNK") != null && !"".equals(rs.getString("BSTNK"))) {
                    fgShipmentInfo.setPo(rs.getString("BSTNK").toString());
                }
                 // 走货日期
                dateTime = DateUtil.parse(rs.getString("MOVE_DATE"));
                System.out.println("测试获取时间" +rs.getString("MOVE_DATE") + dateTime);
                fgShipmentInfo.setShipmentDate(dateTime);
                // 车牌号
                if (rs.getString("CPHM") != null && !"".equals(rs.getString("CPHM"))) {
                    fgShipmentInfo.setCarNo(rs.getString("CPHM").toString());
                }
                // 走货类型
                if (rs.getString("BEZEI") != null && !"".equals(rs.getString("BEZEI"))) {
                    fgShipmentInfo.setShipmentType(rs.getString("BEZEI").toString());
                }
                // 车辆预计到达时间
                dateTime = DateUtil.parse(rs.getString("INDICATE_TIME1"));
                fgShipmentInfo.setArriveDate(dateTime);
                // 走货车辆
                if (rs.getString("CAR_TEXT") != null && !"".equals(rs.getString("CAR_TEXT"))) {
                    fgShipmentInfo.setCar(rs.getString("CAR_TEXT").toString());
                }
                // remark
                if (rs.getString("REMARK2") != null && !"".equals(rs.getString("REMARK2"))) {
                    fgShipmentInfo.setRemark(rs.getString("REMARK2").toString());
                }
                // remark
                if (rs.getString("STATUS") != null && !"".equals(rs.getString("STATUS"))) {
                    fgShipmentInfo.setLastComfirm(rs.getString("STATUS").toString());
                }
                // 客户代码
                if (rs.getString("KUNNR") != null && !"".equals(rs.getString("KUNNR"))) {
                    fgShipmentInfo.setClientCode(rs.getString("KUNNR").toString());
                }
                // 客户PN
                if (rs.getString("KDMAT") != null && !"".equals(rs.getString("KDMAT"))) {
                    fgShipmentInfo.setClientPN(rs.getString("KDMAT").toString());
                }
                // 走货地点
                if (rs.getString("VSTEL") != null && !"".equals(rs.getString("VSTEL"))) {
                    fgShipmentInfo.setShipmentPlace(rs.getString("VSTEL").toString());
                }
                // 变动日期
                String updateDate = "";
                if (rs.getString("ERSDA") != null && !"".equals(rs.getString("ERSDA"))) {
                    updateDate= rs.getString("ERSDA").toString();
                }
                String updateTime = "";
                // 变动时间
                if (rs.getString("ERZET") != null && !"".equals(rs.getString("ERZET"))) {
                    updateTime = rs.getString("ERZET").toString();
                }
                fgShipmentInfo.setUpdateDatetime(updateDate + " " + updateTime);
                // 状态
                fgShipmentInfo.setStatus(0);

                list.add(fgShipmentInfo);
                rs.nextRow();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return list;
    }

    //获取SAP-P/N,批次，数量
    public List<SAPPBQEntity> getSAPPBQ() {
        List<SAPPBQEntity> list = new ArrayList<SAPPBQEntity>();
        try {
//获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
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
            ZDC.setValue("LOW", "RH80");
            ZDC.appendRow();
            ZDC.setValue("SIGN", "I");
            ZDC.setValue("OPTION", "EQ");
            ZDC.setValue("LOW", "CU80");
            ZDC.appendRow();
            ZDC.setValue("SIGN", "I");
            ZDC.setValue("OPTION", "EQ");
            ZDC.setValue("LOW", "BS81");
            ZDC.appendRow();
            ZDC.setValue("SIGN", "I");
            ZDC.setValue("OPTION", "EQ");
            ZDC.setValue("LOW", "BC81");
            try {
                function.execute(destination); //函数执行
                JCoTable rs = function.getTableParameterList().getTable("ET_MCHB");
                for (int j = 0; j < rs.getNumRows(); j++) {
                    SAPPBQEntity sAPPBQEntity = new SAPPBQEntity();
                    if ("RH80".equals(rs.getString("LGORT")) || "CU80".equals(rs.getString("LGORT"))) {
                        sAPPBQEntity.setLocation(rs.getString("LGORT"));
                    } else {
                        sAPPBQEntity.setLocation("RH80");
                    }
                    sAPPBQEntity.setRoom(rs.getString("GROES"));
                    sAPPBQEntity.setPN(rs.getString("MATNR"));
                    sAPPBQEntity.setBatch(rs.getString("CHARG"));
                    if ("RH80".equals(rs.getString("LGORT")) || "CU80".equals(rs.getString("LGORT"))) {
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
        return list;
    }



}
