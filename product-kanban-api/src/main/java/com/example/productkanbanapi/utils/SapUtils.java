package com.example.productkanbanapi.utils;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * SapUtils
 *
 * @author 丁国钊
 * @date 2024-1-18 9:54
 */
@Slf4j
public class SapUtils {

    private static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL_LEDLIGHT";

    public SapUtils() {
        try {
            Properties connectProperties = new Properties();
            // SAP服务器
            connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.31.2.3");
            // SAP系统编号
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
            // SAP集团
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "800");
            // SAP用户名
            connectProperties.setProperty(DestinationDataProvider.JCO_USER, "ituser");
            // SAP密码
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "itqaz2345");
            // SAP登录语言
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "ZH");
            // 最大连接数
            connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "10");
            // 最大连接线程
            connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "20");
            createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);
        } catch (Exception e) {
            log.error(e.toString());
        }
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
     * 获取SAP过账信息
     *
     * @param sapArea         SAP厂区
     * @param partNumber      物料号
     * @param batch           批次
     * @param transactionType 过账类型
     * @return SAP过账信息
     */
    public List<String[]> getPostInfo(String sapArea, String partNumber, String batch, String transactionType) {
        List<String[]> list = new ArrayList<>();
        try {
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
            destination.ping();
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YMMR04");
            if (function == null) {
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
            }
            JCoParameterList input = function.getImportParameterList();
            // 参数--工厂，物料号，批次，过账类型
            input.setValue("I_WERKS", sapArea);
            input.setValue("I_MATNR", partNumber);
            input.setValue("I_CHARG", batch);
            input.setValue("I_BWART", transactionType);
            try {
                // 函数执行
                function.execute(destination);
            } catch (AbapException e) {
                log.info(e.toString());
            }
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");
            for (int i = 0; i < rs.getNumRows(); i++) {
                String[] data = new String[12];
                if (rs.getString("WERKS") != null && !"".equals(rs.getString("WERKS"))) {
                    data[0] = rs.getString("WERKS");
                }
                if (rs.getString("MATNR") != null && !"".equals(rs.getString("MATNR"))) {
                    data[1] = rs.getString("MATNR");
                } else {
                    data[1] = "";
                }
                if (rs.getString("LGORT") != null && !"".equals(rs.getString("LGORT"))) {
                    data[2] = rs.getString("LGORT");
                }
                if (rs.getString("BWART") != null && !"".equals(rs.getString("BWART"))) {
                    data[3] = rs.getString("BWART");
                }
                // 过账编号
                if (rs.getString("MBLNR") != null && !"".equals(rs.getString("MBLNR"))) {
                    data[4] = rs.getString("MBLNR");
                }
                if (rs.getString("BUDAT") != null && !"".equals(rs.getString("BUDAT"))) {
                    data[5] = rs.getString("BUDAT");
                }
                if (rs.getString("CPUTM") != null && !"".equals(rs.getString("CPUTM"))) {
                    data[6] = rs.getString("CPUTM");
                } else {
                    data[6] = "";
                }
                data[7] = Integer.toString(rs.getInt("MENGE"));
                if (rs.getString("MEINS") != null && !"".equals(rs.getString("MEINS"))) {
                    data[8] = rs.getString("MEINS");
                }
                if (rs.getString("CHARG") != null && !"".equals(rs.getString("CHARG"))) {
                    data[9] = rs.getString("CHARG");
                } else {
                    data[9] = "";
                }
                if (rs.getString("AUFNR") != null && !"".equals(rs.getString("AUFNR"))) {
                    data[10] = rs.getString("AUFNR");
                }
                if (rs.getString("KUNNR") != null && !"".equals(rs.getString("KUNNR"))) {
                    data[11] = rs.getString("KUNNR");
                }
                list.add(data);
                rs.nextRow();
            }
        } catch (Exception ex) {
            log.info(ex.toString());
        }
        return list;
    }

    /**
     * 批量获取SAP过账信息
     *
     * @param sapArea         SAP厂区
     * @param partNumberArr   物料号
     * @param batchArr        批次
     * @param transactionType 过账类型
     * @return SAP过账信息
     */
    public List<String[]> getPostInfoBatch(String sapArea, String[] partNumberArr, String[] batchArr, String transactionType) {
        List<String[]> list = new ArrayList<>();
        try {
            // 获取连接池
            JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
            destination.ping();
            // 获取功能函数
            JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YMMR04");
            if (function == null) {
                throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
            }
            JCoParameterList input = function.getImportParameterList();
            // 厂区
            input.setValue("I_WERKS", sapArea);
            // 必传参数 默认第一个物料
            input.setValue("I_MATNR", partNumberArr[0]);
            // 移动类型
            input.setValue("I_BWART", transactionType);
            // 开始时间
            LocalDate localDateTime = LocalDate.now();
            LocalDate nowDateMinus3 = localDateTime.minusDays(3);
            input.setValue("I_BUDATS", DateTimeFormatter.ofPattern("yyyyMMdd").format(nowDateMinus3));
            // 结束时间
            LocalDate nowDate = LocalDate.now();
            input.setValue("I_BUDATE", DateTimeFormatter.ofPattern("yyyyMMdd").format(nowDate));
            // 传入多个P物料、批次
            JCoParameterList input1 = function.getTableParameterList();
            JCoTable PnTableInput = input1.getTable("IT_MATNR");
            for (String partNumber : partNumberArr) {
                PnTableInput.appendRow();
                PnTableInput.setValue("LOW", partNumber);
            }
            JCoTable BatchTableInput = input1.getTable("IT_CHARG");
            for (String batch : batchArr) {
                BatchTableInput.appendRow();
                BatchTableInput.setValue("LOW", batch);
            }
            try {
                function.execute(destination);
            } catch (AbapException e) {
                log.error(e.toString());
            }
            JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");
            for (int i = 0; i < rs.getNumRows(); i++) {
                String[] data = new String[12];
                if (rs.getString("WERKS") != null && !"".equals(rs.getString("WERKS"))) {
                    data[0] = rs.getString("WERKS");
                }
                if (rs.getString("MATNR") != null && !"".equals(rs.getString("MATNR"))) {
                    data[1] = rs.getString("MATNR");
                } else {
                    data[1] = "";
                }
                if (rs.getString("LGORT") != null && !"".equals(rs.getString("LGORT"))) {
                    data[2] = rs.getString("LGORT");
                }
                if (rs.getString("BWART") != null && !"".equals(rs.getString("BWART"))) {
                    data[3] = rs.getString("BWART");
                }
                // 过账编号
                if (rs.getString("MBLNR") != null && !"".equals(rs.getString("MBLNR"))) {
                    data[4] = rs.getString("MBLNR");
                }
                if (rs.getString("BUDAT") != null && !"".equals(rs.getString("BUDAT"))) {
                    data[5] = rs.getString("BUDAT");
                }
                if (rs.getString("CPUTM") != null && !"".equals(rs.getString("CPUTM"))) {
                    data[6] = rs.getString("CPUTM");
                } else {
                    data[6] = "";
                }
                data[7] = Integer.toString(rs.getInt("MENGE"));
                if (rs.getString("MEINS") != null && !"".equals(rs.getString("MEINS"))) {
                    data[8] = rs.getString("MEINS");
                }
                if (rs.getString("CHARG") != null && !"".equals(rs.getString("CHARG"))) {
                    data[9] = rs.getString("CHARG");
                } else {
                    data[9] = "";
                }
                if (rs.getString("AUFNR") != null && !"".equals(rs.getString("AUFNR"))) {
                    data[10] = rs.getString("AUFNR");
                }
                if (rs.getString("KUNNR") != null && !"".equals(rs.getString("KUNNR"))) {
                    data[11] = rs.getString("KUNNR");
                }
                list.add(data);
                rs.nextRow();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {

    }

}
