package com.honortone.api.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.honortone.api.entity.FgShipmentInfo;
import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sap")
public class SAPUtil {
	static String ABAP_AS = "ABAP_AS_WITHOUT_POOL_LEDLIGHT";
	static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL_LEDLIGHT";
	static String ABAP_MS = "ABAP_MS_WITHOUT_POOL_LEDLIGHT";

	public SAPUtil() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sap.properties");
		/*InputStreamReader isr= new InputStreamReader(inputStream);
		BufferedReader bf = new BufferedReader(isr);*/
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		Properties connectProperties = new Properties();
	    connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,p.getProperty("JCO_ASHOST"));
		System.out.println(p.getProperty("JCO_ASHOST"));

	    connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,p.getProperty("JCO_SYSNR"));
		System.out.println(p.getProperty("JCO_SYSNR"));

	    connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,p.getProperty("JCO_CLIENT"));
		System.out.println(p.getProperty("JCO_CLIENT"));
	    connectProperties.setProperty(DestinationDataProvider.JCO_USER,p.getProperty("JCO_USER"));
		System.out.println(p.getProperty("JCO_USER"));

	    connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,p.getProperty("JCO_PASSWD"));
		System.out.println(p.getProperty("JCO_PASSWD"));

	    connectProperties.setProperty(DestinationDataProvider.JCO_LANG,p.getProperty("JCO_LANG"));
		System.out.println(p.getProperty("JCO_LANG"));

	    createDataFile(ABAP_AS, "jcoDestination", connectProperties);
	
	    connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY,p.getProperty("JCO_POOL_CAPACITY"));
		System.out.println(p.getProperty("JCO_POOL_CAPACITY"));
	    connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,p.getProperty("JCO_PEAK_LIMIT"));
		System.out.println(p.getProperty("JCO_PEAK_LIMIT"));
	    createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);
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

	public List<String[]> getInfo(String plant,String partnumber,String batch,String transactiontype) {

		List<String[]> list = new ArrayList();
		try {
			System.out.println(plant + partnumber + batch);
			// 获取连接池
			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
			System.out.println(plant + partnumber + batch);
			destination.ping();
			System.out.println(plant + partnumber + batch);
			// 获取功能函数
			JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YMMR04");
			System.out.println(plant + partnumber + batch);
			if (function == null)
				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

			JCoParameterList input = function.getImportParameterList();
			// 参数--工厂，物料号，批次
			input.setValue("I_WERKS", plant);
			input.setValue("I_MATNR", partnumber);
			input.setValue("I_CHARG", batch);
			input.setValue("I_BWART", transactiontype);

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
				// 工厂
					/*String WERKS = rs.getString("WERKS");
					// 物料号
					String MATNR = rs.getString("MATNR");
					// 存储地点
					String LGORT = rs.getString("LGORT");
					// 异动类型（存货管理）
					String BWART = rs.getString("BWART");
					// 物料文件号
					String MBLNR = rs.getString("MBLNR");
					// 文件中的过账日期
					String BUDAT = rs.getString("BUDAT").toString();
					// 输入时间
					String CPUTM = rs.getString("CPUTM").toString();
					// 数量
					int MENGE = rs.getInt("MENGE");
					// 基础计量单位
					String MEINS = rs.getString("MEINS");
					// 批次号
					String CHARG = rs.getString("CHARG");
					// 订单号
					String AUFNR = rs.getString("AUFNR");
					// 客户账号
					String KUNNR = rs.getString("KUNNR");*/


				String data[] = new String[12];
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
			ex.printStackTrace();
		}
		return list;

	}


	public int getInfo1(String plant,String partnumber,String batch,String transactiontype){

		int n = 0;
		List<String[]> list = new ArrayList();
		try {
			System.out.println(plant+partnumber+batch);
			// 获取连接池
			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
			System.out.println(plant+partnumber+batch);
			destination.ping();
			System.out.println(plant+partnumber+batch);
			// 获取功能函数
			JCoFunction function = destination.getRepository().getFunction("Z_HTMES_YMMR04");
			System.out.println(plant+partnumber+batch);
			if (function == null)
				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

			JCoParameterList input = function.getImportParameterList();
			// 参数--工厂，物料号，批次
			input.setValue("I_WERKS", plant);
			input.setValue("I_MATNR", partnumber);
			input.setValue("I_CHARG", batch);
			input.setValue("I_BWART", transactiontype);

			try {
				// 函数执行
				function.execute(destination);
			} catch (AbapException e) {
				e.printStackTrace();
				System.out.println(e.toString());
			}
			// 获取输出
			JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");
			n = rs.getNumRows();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return n;
	}

	/**
	 * 根据客户PN查询鸿通PN/或反之
	 *
	 * @param
	 *
	 */
	public List<String[]> getHt_PnByKh_Pn(String khpn, String htpn) {

		List<String[]> list = new ArrayList();
		try {
			System.out.println(khpn);
			// 获取连接池
			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
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
	 * 获取QA检验结果
	 *
	 * **/
	@PostMapping("/ymm04")
	public List<String[]> Z_HTMES_YMMR04(String plant, String pn,String startDate,String endDate) {
		{
			System.out.println("查询QA检验结果Start~");
			List<String[]> list = new ArrayList();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			try {

				// 获取连接池（连接池连了一次后就默认作为下次的连接地址，需要重新定义连接池变量【在配置文件更换地址没用】）
				JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
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
				input.setValue("I_MATNR", "660-HGU1127-00R3");
				input.setValue("I_WERKS", "1100");
				input.setValue("I_BUDATS", "20230711");
				input.setValue("I_BUDATE", "20230711");

				try {
					long before = System.currentTimeMillis();
					// 函数执行
					function.execute(destination);
					long after = System.currentTimeMillis();
					System.out.println("耗时："+(after-before));
				} catch (AbapException e) {
					e.printStackTrace();
					System.out.println(e.toString());
				}
				// 获取输出
				JCoTable rs = function.getTableParameterList().getTable("ET_RESULT");

				// 注：目前获取的所有数据字段都没有进行非空判断
				for (int j = 0; j < rs.getNumRows(); j++) {

					String[] data = new String[3];
					// 移动类型
					if (rs.getString("BWART") != null && !rs.getString("BWART").equals("")) {
						data[0] = rs.getString("BWART").toString();
					}
					// 库存地点
					if (rs.getString("LGORT") != null && !rs.getString("LGORT").equals("")) {
						data[1] = rs.getString("LGORT").toString();
					}
					// 批次
					if (rs.getString("CHARG") != null && !rs.getString("CHARG").equals("")) {
						data[2] = rs.getString("CHARG").toString();
					}
					list.add(data);
					rs.nextRow();
				}
				System.out.println("1221121=="+list.size());

			} catch (Exception ex) {
				ex.printStackTrace();

			}
			return list;
		}

	}

	public List<FgShipmentInfo> Z_HTMES_ZSDSHIPLS_1(String startDate, String endDate) {

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
				// 箱数
				if (rs.getString("BOX") != null && !"".equals(rs.getString("BOX"))) {
					fgShipmentInfo.setBoxQty((long)Double.parseDouble(rs.getString("BOX")));
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
				// 变动日期
				if (rs.getString("ERSDA") != null && !"".equals(rs.getString("ERSDA"))) {
					fgShipmentInfo.setUpdateDate(rs.getString("ERSDA").toString());
				}
				// 变动时间
				if (rs.getString("ERZET") != null && !"".equals(rs.getString("ERZET"))) {
					fgShipmentInfo.setUpdateTime(rs.getString("ERZET").toString());
				}

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

}
