package com.ktg.mes.fg.utils;

//import ht.entity.AccApply;
//import ht.entity.BitchPrint;
//import ht.entity.Grn_543;
//import ht.entity.MG;
//import ht.entity.MGPN;
//import ht.entity.Mb52;
//import ht.entity.NewSupplier;
//import ht.entity.OsPoList;
//import ht.entity.PlanOrder;
//import ht.entity.PoList;
//import ht.entity.PonewETATemp;
//import ht.entity.SAP_321;
//import ht.entity.SchedulList;
//import ht.entity.UIDSplit;
//import ht.entity.VendorRid;
//import ht.entity.WorkOrder;
//import ht.entity.YFIR112018;
//import ht.entity.YSDR29E3;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
//import com.opensymphony.xwork2.ActionContext;
//import com.sap.conn.jco.AbapException;
//import com.sap.conn.jco.JCoContext;
//import com.sap.conn.jco.JCoDestination;
//import com.sap.conn.jco.JCoDestinationManager;
//import com.sap.conn.jco.JCoException;
//import com.sap.conn.jco.JCoFunction;
//import com.sap.conn.jco.JCoParameterList;
//import com.sap.conn.jco.JCoStructure;
//import com.sap.conn.jco.JCoTable;
//import com.sap.conn.jco.ext.DestinationDataProvider;


public class SAPService {
//	static String ABAP_AS = "ABAP_AS_WITHOUT_POOL_LEDLIGHT";
//	static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL_LEDLIGHT";
//	static String ABAP_MS = "ABAP_MS_WITHOUT_POOL_LEDLIGHT";
//
//	public SAPService() {
//	  InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sap.properties");
//  	  Properties p = new Properties();
//  	  try {
//  	   p.load(inputStream);
//  	  } catch (IOException e1) {
//  	   e1.printStackTrace();
//  	  }
//
//		try {
//			Properties connectProperties = new Properties();
//			connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,p.getProperty("JCO_ASHOST"));
//			connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,p.getProperty("JCO_SYSNR"));
//			//connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,"01");//测试机
//			connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,p.getProperty("JCO_CLIENT"));
//			connectProperties.setProperty(DestinationDataProvider.JCO_USER,p.getProperty("JCO_USER"));
//			connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,p.getProperty("JCO_PASSWD"));
//			connectProperties.setProperty(DestinationDataProvider.JCO_LANG,p.getProperty("JCO_LANG"));
//			createDataFile(ABAP_AS, "jcoDestination", connectProperties);
//
//			connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, p.getProperty("JCO_POOL_CAPACITY"));
//	        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,    p.getProperty("JCO_PEAK_LIMIT"));
//			createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);
//
//		} catch (Exception e) {
//			System.out.print("取得SAP链接有问题:"+e.getMessage());
//			e.printStackTrace();
//		}
//
//	}
//
//	private void createDataFile(String name, String suffix,
//			Properties properties) {
//		File cfg = new File(name + "." + suffix);
//		if (!cfg.exists()) {
//			try {
//				FileOutputStream fos = new FileOutputStream(cfg, false);
//				properties.store(fos, "for tests only !");
//				fos.close();
//			} catch (Exception e) {
//				throw new RuntimeException(
//						"Unable to create the destination file "
//								+ cfg.getName(), e);
//			}
//		}
//	}
//
//	 //内表
//    public String[] getvendor(String P_MBLNR,String P_ZEILE)
//    {
//    	 String[] str = new String[2];
//    	try
//    	{
//
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_JAVA_I_GETVENDOR");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	     	 input.setValue("P_MBLNR",P_MBLNR);
//	     	 input.setValue("P_ZEILE",P_ZEILE);
//	         try
//	         {
//	             function.execute(destination); //函数执行
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	             return null;
//	         }
//	         //直接返回值取值
//	        JCoParameterList exports = function.getExportParameterList();
//	         str[0] = exports.getString("E_VENDOR");
//	         str[1] = exports.getString("E_MPQ");
//	         return str;
//
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    }
//	    return null;
//    }
//
//
//	/**
//	 * 根据工厂查找仓管员
//	 * @param args
//	 */
//	public Set<String> findCangs(int plant){
//		try {
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					"Z_JAVA_I_GETCWY");// 接口方法名字
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("Z_WERKS", plant);// 设置参数
//
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			//
//
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable companies = tables.getTable("Z_ZTSTRMGT");
//			Set s=new HashSet();
//	        if (companies.getNumRows()>0)
//	        {
//	        	for(int i=0;i<companies.getNumRows();i++){
//	        		companies.setRow(i);
//	        		s.add(companies.getString("STRER"));
//	        	}
//	        }
//	        return s;
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return null;
//	}
//
//
//	//根據批次修改
//	public  void modify_data(String RFCName, String pra1,String pra2,String pra3)throws Exception {
//
//
//		try {
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					RFCName);// 接口方法名字
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("ZMBLNR", pra1);// 设置参数
//			input.setValue("ZMATNR", pra2);// 设置参数
//			input.setValue("ZWERKS", pra3);// 设置参数
//
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//	}
//
//
//	public List<YSDR29E3> Z_DC_GET_YSDR29E3(String plant,String I_MATNR,String I_AUDAT_BEGIN,String I_AUDAT_END,List<MG> listMG,List<MGPN> listMGPN){
//		List<YSDR29E3> list  =  new ArrayList();
//		try {
//			System.out.println("YSDR29E3 1:"+(new Date()).toString() );
//
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					"Z_DC_GET_YSDR29E3");// 接口方法名字
//			JCoFunction function2 = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("I_MATNR", I_MATNR);// 设置参数
//			input.setValue("I_WERKS", plant);// 设置参数
//			input.setValue("I_AUDAT_BEGIN", I_AUDAT_BEGIN);// 设置参数
//			input.setValue("I_AUDAT_END", I_AUDAT_END);// 设置参数
//
//			try {
//				function.execute(destination); //
//				System.out.println("YSDR29E3 2:"+(new Date()).toString());
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("E_OUT");
//	     	for (int j = 0; j < rs.getNumRows(); j++) {
//
//	     	    //System.out.println(rs.getString("MATNR")+"+++++++++"+rs.getBigDecimal("BACKQTY"));
//	     		String MATNR = rs.getString("MATNR");
//	     		String PLANT = rs.getString("WERKS");
//	     		//根据物料组+工厂进行判断
//				boolean d = false;
//				for(int m= 0;m<listMG.size();m++){
//					if(MATNR.startsWith(listMG.get(m).getMG()) && PLANT.equals(listMG.get(m).getPlant())){
//						d = true;
//						break;
//					}
//				}
//				//根据工厂物料进行判断
//				if(!d){
//					for(int n= 0;n<listMGPN.size();n++){
//						if(MATNR.equals(listMGPN.get(n).getPN()) && PLANT.equals(listMGPN.get(n).getPlant())){
//							d = true;
//							break;
//						}
//					}
//				}
//
//	     	    if(d){
//	     	    YSDR29E3 y3 = new YSDR29E3();
//           	    y3.setPLANT(rs.getString("WERKS"));
//           	    y3.setMATNR(rs.getString("MATNR"));
//           	    y3.setE_date(rs.getString("EDATU"));
//           	    y3.setE_WMENG_COUN(rs.getBigDecimal("BACKQTY").intValue()*(-1));
//           	    y3.setBugroup(rs.getString("ZZBU"));
//           	    y3.setCustcode(rs.getString("ZZCUSTOMER"));
//           	    y3.setMccode(rs.getString("EKGRP"));
//           	    y3.setMcname(rs.getString("EKNAM"));
//           	    y3.setDESCRIPTION(rs.getString("MAKTX"));
//           	    y3.setMTLTYPE(rs.getString("MTART"));
//           	    y3.setUOM(rs.getString("MEINS"));
//           	    y3.setGR_PT(rs.getString("WEBAZ"));
//           	    y3.setLT(rs.getString("PLIFZ"));
//           	    y3.setMOQ(rs.getDouble("BSTMI"));
//           	    y3.setMPQ(rs.getDouble("BSTRF"));
//           	    y3.setLIFNR_FIX(rs.getString("LIFNR_FIX"));
//
//           	//查询其他信息 Z_WAP_GET_MATERIAL
//            	   JCoParameterList input2 = function2.getImportParameterList();
//            	    input2.clear();
//     			input2.setValue("I_MATNR", rs.getString("MATNR"));// partNumber
//     			input2.setValue("I_WERKS", rs.getString("WERKS"));// plent
//     			function2.execute(destination);
//     			JCoStructure E_ZCPMP2 = function2.getExportParameterList().getStructure("E_ZCPMP2");
//     			String PMCP = E_ZCPMP2.getString("PMCP");
//     			y3.setPmcgroup(PMCP);
//           	    list.add(y3);
//
//	     	    }
//
//	     	    rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//	public List<YSDR29E3> Z_DC_GET_YSDR29E3WIP(String plant,String I_MATNR,String I_AUDAT_BEGIN,String I_AUDAT_END,List<MG> listMG,List<MGPN> listMGPN){
//		List<YSDR29E3> list  =  new ArrayList();
//		try {
//			System.out.println("YSDR29E3 1 :"+(new Date()).toString() );
//
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					"Z_DC_GET_YSDR29E3");// 接口方法名字
//			JCoFunction function2 = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("I_MATNR", I_MATNR);// 设置参数
//			input.setValue("I_WERKS", plant);// 设置参数
//			input.setValue("I_AUDAT_BEGIN", I_AUDAT_BEGIN);// 设置参数
//			input.setValue("I_AUDAT_END", I_AUDAT_END);// 设置参数
//
//			try {
//				function.execute(destination); //
//				System.out.println("YSDR29E3 2:"+(new Date()).toString());
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("E_OUT");
//	     	for (int j = 0; j < rs.getNumRows(); j++) {
//
//	     	    //System.out.println(rs.getString("MATNR")+"+++++++++"+rs.getBigDecimal("BACKQTY"));
//	     		String MATNR = rs.getString("MATNR");
//	     		String PLANT = rs.getString("WERKS");
//	     		//根据物料组+工厂进行判断
//				boolean d = false;
//				for(int m= 0;m<listMG.size();m++){
//					if(MATNR.startsWith(listMG.get(m).getMG()) && PLANT.equals(listMG.get(m).getPlant())){
//						d = true;
//						break;
//					}
//				}
//				//根据工厂物料进行判断
//				if(!d){
//					for(int n= 0;n<listMGPN.size();n++){
//						if(MATNR.equals(listMGPN.get(n).getPN()) && PLANT.equals(listMGPN.get(n).getPlant())){
//							d = true;
//							break;
//						}
//					}
//				}
//
//	     	    if(d){
//	     	    YSDR29E3 y3 = new YSDR29E3();
//           	    y3.setPLANT(rs.getString("WERKS"));
//           	    y3.setMATNR(rs.getString("MATNR"));
//           	    y3.setE_date(rs.getString("EDATU"));
//           	    y3.setE_WMENG_COUN(rs.getBigDecimal("BACKQTY").intValue()*(-1));
//           	    y3.setBugroup(rs.getString("ZZBU"));
//           	    y3.setCustcode(rs.getString("ZZCUSTOMER"));
//           	    y3.setMccode(rs.getString("EKGRP"));
//           	    y3.setMcname(rs.getString("EKNAM"));
//           	    y3.setDESCRIPTION(rs.getString("MAKTX"));
//           	    y3.setMTLTYPE(rs.getString("MTART"));
//           	    y3.setUOM(rs.getString("MEINS"));
//           	    y3.setGR_PT(rs.getString("WEBAZ"));
//           	    y3.setLT(rs.getString("PLIFZ"));
//           	    y3.setMOQ(rs.getDouble("BSTMI"));
//           	    y3.setMPQ(rs.getDouble("BSTMI"));
//           	    y3.setLIFNR_FIX(rs.getString("LIFNR_FIX"));
//
//				//查询其他信息 Z_WAP_GET_MATERIAL
//           	   JCoParameterList input2 = function2.getImportParameterList();
//           	    input2.clear();
//    			input2.setValue("I_MATNR", rs.getString("MATNR"));// partNumber
//    			input2.setValue("I_WERKS", rs.getString("WERKS"));// plent
//    			function2.execute(destination);
//    			// 获取输出
//    			JCoStructure E_MARC = function2.getExportParameterList().getStructure("E_MARC");
//    			String DZEIT = E_MARC.getString("DZEIT");
//    			String BESKZ = E_MARC.getString("BESKZ");
//    			JCoStructure E_T024D = function2.getExportParameterList().getStructure("E_T024D");
//    			String DISPO = E_T024D.getString("DISPO");
//    			String DSNAM = E_T024D.getString("DSNAM");
//    			JCoStructure E_ZCPMP2 = function2.getExportParameterList().getStructure("E_ZCPMP2");
//    			String PMCP = E_ZCPMP2.getString("PMCP");
//    			y3.setPmcgroup(PMCP);
//    			y3.setDISPO(DISPO);
//    			y3.setDSNAM(DSNAM);
//    			y3.setDZEIT(DZEIT);
//
//
//
//    			if(y3.getMATNR().startsWith("660") || y3.getMATNR().startsWith("661") || y3.getMATNR().startsWith("662") || y3.getMATNR().startsWith("663")){
//    				list.add(y3);
//
//    			}else{
//    				if(!BESKZ.equals("F")){
//	    				list.add(y3);
//	        	    }
//    			}
//
//	     	    }
//
//	     	    rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//	/**
//	 * 查询物料信息 -- 接口名字：Z_DC_GET_YMMR46
//	 * @param po
//	 * @param I_MATNR 物料编码
//	 * @param I_LIFNR 供应商编号
//	 * @param I_EXTWG 客户编号
//	 * @param I_WERKS 工厂
//	 * @param I_MFRPN 制造商物料编码
//	 * @return
//	 */
//	public List<PlanOrder> Z_DC_GET_YMMR46(SchedulList sl,String id,List<MG> listMG,List<MGPN> listMGPN)throws Exception{
//		List<PlanOrder> list  =  new ArrayList();
//		    JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_YMMR46");// 接口方法名字
//			if (function == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			 JCoTable ZDC2 = function.getTableParameterList().getTable("I_MATNR");
//
//		      ZDC2.appendRow();
//			  ZDC2.setValue("MATNR",sl.getPn());//getPn
//
//
//			  JCoTable ZDC = function.getTableParameterList().getTable("I_WERKS");
//
//		      ZDC.appendRow();
//			  ZDC.setValue("WERKS",sl.getPlant());//WERKS
//
//
//			  try {
//					function.execute(destination); //
//				} catch (AbapException e) {
//					e.printStackTrace();
//					System.out.println(e.toString());
//				}
//
//				JCoParameterList tables=function.getTableParameterList();
//				JCoTable rs = function.getTableParameterList().getTable("E_OUT");
//
//
//				for (int j = 0; j < rs.getNumRows(); j++) {
//	            		//根据物料组+工厂进行判断
//						boolean d = false;
//						for(int m= 0;m<listMG.size();m++){
//							if(rs.getString("IDNRK").startsWith(listMG.get(m).getMG()) && rs.getString("WERKS").equals(listMG.get(m).getPlant())){
//								d = true;
//								break;
//							}
//						}
//						//根据工厂物料进行判断
//						if(!d){
//							for(int n= 0;n<listMGPN.size();n++){
//								if(rs.getString("IDNRK").equals(listMGPN.get(n).getPN()) && rs.getString("WERKS").equals(listMGPN.get(n).getPlant())){
//									d = true;
//									break;
//								}
//							}
//						}
//
//	            	    if(d){
//
//	            	    PlanOrder po = new PlanOrder();
//	            	    po.setSchedul_id(Integer.parseInt(id));
//
//	            	    po.setPLANT(rs.getString("WERKS"));
//	            	    po.setSO("");
//	            	    po.setPN(rs.getString("MATNR"));
//	            	    po.setPART(rs.getString("IDNRK"));
//
//	            	    po.setDESCRIPTION(rs.getString("OJTXP"));
//	            	    po.setOMSPN(rs.getString("BISMT"));//x
//	            	    po.setCustomerPN(rs.getString("NORMT"));//x
//	            	    po.setMTLTYPE(rs.getString("MTART"));
//	            	    po.setEFFDate(rs.getString("DATUV"));
//
//	            	    po.setPURTYPE(rs.getString("BESKZ"));//x
//	            	    po.setUOM(rs.getString("MEINS"));
//
//	            	    po.setALP_GRP(rs.getString("ALPGR"));
//	            	    po.setPrior(rs.getString("ALPRF"));
//	            	    po.setUsge_pro(rs.getString("EWAHR"));
//	            	    po.setABC(rs.getString("MAABC"));//x
//	            	    po.setSafeStock(rs.getString("EISBE"));
//	            	    po.setGR_PT(rs.getString("WEBAZ"));//x
//	            	    po.setBLKQty(rs.getDouble("SPEME"));//x
//	            	    po.setBlock(rs.getDouble("BLOCK"));//x
//	            	    po.setIn_Transit(rs.getDouble("TRANSIT"));//x
//	            	    po.setUsage(rs.getDouble("MNGLG"));
//	            	    po.setLT(rs.getString("PLIFZ"));//x
//	            	    po.setMOQ(rs.getDouble("BSTMI"));//x
//	            	    po.setMPQ(rs.getDouble("BSTRF"));//x
//
//	            	    po.setMCCode(rs.getString("EKGRP"));
//	            	    po.setMCName(rs.getString("EKNAM"));//x
//	            	    po.setLIFNR_FIX(rs.getString("LIFNR_FIX"));
//	            	    po.setBugroup(rs.getString("ZZBU"));//x
//	            	    po.setPmcgroup(rs.getString("ZDESC"));//x
//	            	    po.setCustcode(rs.getString("ZZCUSTOMER"));//x
//	            	    list.add(po);
//	            	    }
//	            	    rs.nextRow();
//	            }
//
//		return list;
//	}
//	//YMMR46替代方法
//	public List<PlanOrder> Z_DC_GET_YMMR46_2(SchedulList sl,String id,List<MG> listMG,List<MGPN> listMGPN)throws Exception{
//	    List<PlanOrder> list  =  new ArrayList();
//	    JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction("Z_JAVA_CS_BOM_02");// 接口方法名字
//		JCoFunction function2 = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//
//		if (function == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//
//
//		JCoParameterList input = function.getImportParameterList();
//		input.setValue("ZMATNR", sl.getPn());// partNumber
//		input.setValue("ZWERKS", sl.getPlant());// plent
//
//
//
//		  try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ITAB_ALL");
//			//System.out.println(sl.getPn()+"-----"+sl.getPlant());
//
//			//SAPService sap=new SAPService();
//			for (int j = 0; j < rs.getNumRows(); j++) {
//            		//根据物料组+工厂进行判断
//					boolean d = false;
//					for(int m= 0;m<listMG.size();m++){
//						if(rs.getString("IDNRK").startsWith(listMG.get(m).getMG()) && rs.getString("WERKS").equals(listMG.get(m).getPlant())){
//							d = true;
//							break;
//						}
//					}
//					//根据工厂物料进行判断
//					if(!d){
//						for(int n= 0;n<listMGPN.size();n++){
//							if(rs.getString("IDNRK").equals(listMGPN.get(n).getPN()) && rs.getString("WERKS").equals(listMGPN.get(n).getPlant())){
//								d = true;
//								break;
//							}
//						}
//					}
//					//System.out.println(j+"----"+rs.getString("IDNRK")+"-----"+rs.getString("XTLNR")+"----"+rs.getString("DUMPS"));
//            	    if(d){
//
//            	    	System.out.println(j+"----"+rs.getString("IDNRK")+"-----"+rs.getString("XTLNR")+"----"+rs.getString("DUMPS"));
//            	    	PlanOrder po = new PlanOrder();
//	            	    po.setSchedul_id(Integer.parseInt(id));
//
//	            	    po.setPLANT(rs.getString("WERKS"));
//	            	    po.setSO("");
//	            	    po.setPN(sl.getPn());
//	            	    po.setPART(rs.getString("IDNRK"));
//	            	    po.setDESCRIPTION(rs.getString("OJTXP"));
//
//	            	    //po.setOMSPN(rs.getString("BISMT"));//x
//	            	    //po.setCustomerPN(rs.getString("NORMT"));//x
//	            	    po.setMTLTYPE(rs.getString("MTART"));
//	            	    po.setEFFDate(rs.getString("DATUV"));
//
//	            	    //po.setPURTYPE(rs.getString("BESKZ"));//x
//
//
//	            	    po.setALP_GRP(rs.getString("ALPGR"));
//	            	    po.setPrior(rs.getString("ALPRF"));
//	            	    po.setUsge_pro(rs.getString("EWAHR"));
//	            	    //po.setABC(rs.getString("MAABC"));//x
//	            	    //po.setSafeStock(rs.getString("EISBE"));//X
//	            	    //po.setGR_PT(rs.getString("WEBAZ"));//x
//	            	    //po.setBLKQty(rs.getDouble("SPEME"));//x
//	            	    //po.setBlock(rs.getDouble("BLOCK"));//x
//	            	    //po.setIn_Transit(rs.getDouble("TRANSIT"));//x
//
//	            	    po.setUOM(rs.getString("MMEIN"));//基本单位
//	            	    String meins =rs.getString("MEINS");//bom实际单位
//
//	            	  //  IF ibom-meins = ibom-mmein.
//	            	  //     ibom-mnglg = ibom-mnglg.  po.setUsage(rs.getDouble("MNGLG"));//用量
//	            	  //  ELSE.
//	            	  //  ibom-mnglg = ibom-mngko * ibom-umrez / ibom-umren.
//	            	    //             (rs.getDouble("MNGKO")*rs.getDouble("UMREZ"))/rs.getDouble("UMREN")
//	            	    // po.setUsage(rs.getDouble("MNGLG"));//用量
//	            	  // ENDIF.
//	            	    if(po.getUOM().equals(meins)){
//	            	    	po.setUsage(rs.getDouble("MNGLG"));//用量
//	            	    }else{
//	            	    	po.setUsage((rs.getDouble("MNGKO")*rs.getDouble("UMREZ"))/rs.getDouble("UMREN"));
//	            	    }
//
//
//
//	            	    //po.setLT(rs.getString("PLIFZ"));//x
//	            	    //po.setMOQ(rs.getDouble("BSTMI"));//x
//	            	    //po.setMPQ(rs.getDouble("BSTRF"));//x
//
//	            	    po.setMCCode(rs.getString("EKGRP"));
//	            	    //po.setMCName(rs.getString("EKNAM"));//x
//	            	    //po.setLIFNR_FIX(rs.getString("LIFNR_FIX"));//x
//	            	    //po.setBugroup(rs.getString("ZZBU"));//x
//	            	    //po.setPmcgroup(rs.getString("ZDESC"));//x
//	            	    //po.setCustcode(rs.getString("ZZCUSTOMER"));//x
//	            	    //po = sap.Z_WAP_GET_MATERIAL(po, rs.getString("WERKS"), rs.getString("IDNRK"));
//	            	    //查询其他信息 Z_WAP_GET_MATERIAL
//	            	    JCoParameterList input2 = function2.getImportParameterList();
//	            	    input2.clear();
//	        			input2.setValue("I_MATNR", rs.getString("IDNRK"));// partNumber
//	        			input2.setValue("I_WERKS", rs.getString("WERKS"));// plent
//	        			function2.execute(destination);
//	        			// 获取输出
//	        			JCoStructure E_MARA = function2.getExportParameterList().getStructure("E_MARA");
//	        			String ZBU = E_MARA.getString("ZZBU");
//	        			String BISMT = E_MARA.getString("BISMT");
//	        			String NORMT = E_MARA.getString("NORMT");
//	        			String ZZBU = E_MARA.getString("ZZBU");
//	        			String EXTWG = E_MARA.getString("EXTWG");
//
//
//	        			JCoStructure E_MARC = function2.getExportParameterList().getStructure("E_MARC");
//	        			String BESKZ = E_MARC.getString("BESKZ");
//	        			String MAABC = E_MARC.getString("MAABC");
//	        			String EISBE = E_MARC.getString("EISBE");
//	        			String WEBAZ = E_MARC.getString("WEBAZ");
//	        			String PLIFZ = E_MARC.getString("PLIFZ");
//	        			Double BSTMI = E_MARC.getDouble("BSTMI");
//	        			Double BSTRF = E_MARC.getDouble("BSTRF");
//
//	        			String DZEIT = E_MARC.getString("DZEIT");
//	        			String EKGRP = E_MARC.getString("EKGRP");
//
//
//	        			JCoStructure E_ZCPMP2 = function2.getExportParameterList().getStructure("E_ZCPMP2");
//	        			String PMCP = E_ZCPMP2.getString("PMCP");
//
//	        			JCoStructure E_T024D = function2.getExportParameterList().getStructure("E_T024D");
//	        			String DISPO = E_T024D.getString("DISPO");
//	        			String DSNAM = E_T024D.getString("DSNAM");
//
//	        			JCoStructure E_T024 = function2.getExportParameterList().getStructure("E_T024");
//	        			String EKNAM = E_T024.getString("EKNAM");
//
//	        			JCoStructure E_EORD = function2.getExportParameterList().getStructure("E_EORD");
//	        			String LIFNR = E_EORD.getString("LIFNR");
//
//
//
//	        	     	//System.out.println(ZBU);
//
//	        	     	//po.setSchedul_id(Integer.parseInt(id));
//
//	             	    //po.setPLANT(rs.getString("WERKS"));
//	             	    //po.setSO("");
//	             	   // po.setPN(sl.getPn());
//	             	    //po.setPART(rs.getString("IDNRK"));
//	             	    //po.setDESCRIPTION(rs.getString("OJTXP"));
//
//	             	    po.setOMSPN(BISMT);//x
//	             	    po.setCustomerPN(NORMT);//x
//	             	    //po.setMTLTYPE(rs.getString("MTART"));
//	             	    //po.setEFFDate(rs.getString("DATUV"));
//
//	             	    po.setPURTYPE(BESKZ);//x
//	             	   // po.setUOM(rs.getString("MMEIN"));
//
//	             	    //po.setALP_GRP(rs.getString("ALPGR"));
//	             	   // po.setPrior(rs.getString("ALPRF"));
//	             	    //po.setUsge_pro(rs.getString("EWAHR"));
//	             	    po.setABC(MAABC);//x
//	             	    po.setSafeStock(EISBE);//X
//	             	    po.setGR_PT(WEBAZ);//x
//	             	   //// po.setBLKQty(rs.getDouble("SPEME"));//xxxx
//	             	   ////po.setBlock(rs.getDouble("BLOCK"));//xxxxx
//	             	   ////po.setIn_Transit(rs.getDouble("TRANSIT"));//xxxxx
//	             	    //po.setUsage(rs.getDouble("MNGLG"));
//	             	    po.setLT(PLIFZ);//x
//	             	    po.setMOQ(BSTMI);//x
//	             	    po.setMPQ(BSTRF);//x
//
//	             	     po.setMCCode(EKGRP);
//	             	    po.setMCName(EKNAM);//x
//	             	    po.setLIFNR_FIX(LIFNR);//xxxxxx
//	             	    po.setBugroup(ZZBU);//x
//	             	    po.setPmcgroup(PMCP);//x
//	             	    po.setCustcode(EXTWG);//x
//
//
//	             	   po.setDISPO(DISPO);
//	             	   po.setDSNAM(DSNAM);
//	             	    po.setDZEIT(DZEIT);
//
//	            	    list.add(po);
//            	    }
//            	    rs.nextRow();
//            }
//
//	return list;
//}
//
//	//--------WIP拆BOM---YPPR44B------Z_JAVA_CS_BOM_02
//
//	public List<PlanOrder> Z_DC_GET_YMMR46WIP(SchedulList sl,String id,List<MG> listMG,List<MGPN> listMGPN)throws Exception{
//		    List<PlanOrder> list  =  new ArrayList();
//		    JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_JAVA_CS_BOM_02");// 接口方法名字
//			JCoFunction function2 = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//
//			if (function == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//
//
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("ZMATNR", sl.getPn());// partNumber
//			input.setValue("ZWERKS", sl.getPlant());// plent
//
//
//
//			  try {
//					function.execute(destination); //
//				} catch (AbapException e) {
//					e.printStackTrace();
//					System.out.println(e.toString());
//				}
//
//				JCoParameterList tables=function.getTableParameterList();
//				JCoTable rs = function.getTableParameterList().getTable("ITAB_ALL");
//				//System.out.println(sl.getPn()+"-----"+sl.getPlant());
//
//				//SAPService sap=new SAPService();
//				for (int j = 0; j < rs.getNumRows(); j++) {
//	            		//根据物料组+工厂进行判断
//						boolean d = false;
//
//						for(int m= 0;m<listMG.size();m++){
//							if(rs.getString("IDNRK").startsWith(listMG.get(m).getMG()) && rs.getString("WERKS").equals(listMG.get(m).getPlant())){
//								d = true;
//								break;
//							}
//						}
//
//	            	    if(d && !rs.getString("XTLNR").equals("") && !rs.getString("DUMPS").equals("X") && !rs.getString("DUMPS").equals("x")){
//
//	            	    	//System.out.println(j+"----"+rs.getString("IDNRK")+"------"+rs.getString("DUMPS"));
//
//		            	    PlanOrder po = new PlanOrder();
//		            	    po.setSchedul_id(Integer.parseInt(id));
//
//		            	    po.setPLANT(rs.getString("WERKS"));
//		            	    po.setSO("");
//		            	    po.setPN(sl.getPn());
//		            	    po.setPART(rs.getString("IDNRK"));
//		            	    po.setDESCRIPTION(rs.getString("OJTXP"));
//
//		            	    //po.setOMSPN(rs.getString("BISMT"));//x
//		            	    //po.setCustomerPN(rs.getString("NORMT"));//x
//		            	    po.setMTLTYPE(rs.getString("MTART"));
//		            	    po.setEFFDate(rs.getString("DATUV"));
//
//		            	    //po.setPURTYPE(rs.getString("BESKZ"));//x
//		            	    po.setUOM(rs.getString("MMEIN"));
//
//		            	    po.setALP_GRP(rs.getString("ALPGR"));
//		            	    po.setPrior(rs.getString("ALPRF"));
//		            	    po.setUsge_pro(rs.getString("EWAHR"));
//		            	    //po.setABC(rs.getString("MAABC"));//x
//		            	    //po.setSafeStock(rs.getString("EISBE"));//X
//		            	    //po.setGR_PT(rs.getString("WEBAZ"));//x
//		            	    //po.setBLKQty(rs.getDouble("SPEME"));//x
//		            	    //po.setBlock(rs.getDouble("BLOCK"));//x
//		            	    //po.setIn_Transit(rs.getDouble("TRANSIT"));//x
//		            	    po.setUsage(rs.getDouble("MNGLG"));
//		            	    //po.setLT(rs.getString("PLIFZ"));//x
//		            	    //po.setMOQ(rs.getDouble("BSTMI"));//x
//		            	    //po.setMPQ(rs.getDouble("BSTRF"));//x
//
//		            	    po.setMCCode(rs.getString("EKGRP"));
//		            	    //po.setMCName(rs.getString("EKNAM"));//x
//		            	    //po.setLIFNR_FIX(rs.getString("LIFNR_FIX"));//x
//		            	    //po.setBugroup(rs.getString("ZZBU"));//x
//		            	    //po.setPmcgroup(rs.getString("ZDESC"));//x
//		            	    //po.setCustcode(rs.getString("ZZCUSTOMER"));//x
//		            	    //po = sap.Z_WAP_GET_MATERIAL(po, rs.getString("WERKS"), rs.getString("IDNRK"));
//		            	    //查询其他信息 Z_WAP_GET_MATERIAL
//		            	    JCoParameterList input2 = function2.getImportParameterList();
//		            	    input2.clear();
//		        			input2.setValue("I_MATNR", rs.getString("IDNRK"));// partNumber
//		        			input2.setValue("I_WERKS", rs.getString("WERKS"));// plent
//		        			function2.execute(destination);
//		        			// 获取输出
//		        			JCoStructure E_MARA = function2.getExportParameterList().getStructure("E_MARA");
//		        			String ZBU = E_MARA.getString("ZZBU");
//		        			String BISMT = E_MARA.getString("BISMT");
//		        			String NORMT = E_MARA.getString("NORMT");
//		        			String ZZBU = E_MARA.getString("ZZBU");
//		        			String EXTWG = E_MARA.getString("EXTWG");
//
//
//		        			JCoStructure E_MARC = function2.getExportParameterList().getStructure("E_MARC");
//		        			String BESKZ = E_MARC.getString("BESKZ");
//		        			String MAABC = E_MARC.getString("MAABC");
//		        			String EISBE = E_MARC.getString("EISBE");
//		        			String WEBAZ = E_MARC.getString("WEBAZ");
//		        			String PLIFZ = E_MARC.getString("PLIFZ");
//		        			Double BSTMI = E_MARC.getDouble("BSTMI");
//		        			Double BSTRF = E_MARC.getDouble("BSTRF");
//
//		        			String DZEIT = E_MARC.getString("DZEIT");
//		        			String EKGRP = E_MARC.getString("EKGRP");
//
//
//		        			JCoStructure E_ZCPMP2 = function2.getExportParameterList().getStructure("E_ZCPMP2");
//		        			String PMCP = E_ZCPMP2.getString("PMCP");
//
//		        			JCoStructure E_T024D = function2.getExportParameterList().getStructure("E_T024D");
//		        			String DISPO = E_T024D.getString("DISPO");
//		        			String DSNAM = E_T024D.getString("DSNAM");
//
//		        			JCoStructure E_T024 = function2.getExportParameterList().getStructure("E_T024");
//		        			String EKNAM = E_T024.getString("EKNAM");
//
//		        			JCoStructure E_EORD = function2.getExportParameterList().getStructure("E_EORD");
//		        			String LIFNR = E_EORD.getString("LIFNR");
//		        	     	//System.out.println(ZBU);
//
//		        	     	//po.setSchedul_id(Integer.parseInt(id));
//
//		             	    //po.setPLANT(rs.getString("WERKS"));
//		             	    //po.setSO("");
//		             	   // po.setPN(sl.getPn());
//		             	    //po.setPART(rs.getString("IDNRK"));
//		             	    //po.setDESCRIPTION(rs.getString("OJTXP"));
//
//		             	    po.setOMSPN(BISMT);//x
//		             	    po.setCustomerPN(NORMT);//x
//		             	    //po.setMTLTYPE(rs.getString("MTART"));
//		             	    //po.setEFFDate(rs.getString("DATUV"));
//
//		             	    po.setPURTYPE(BESKZ);//x
//		             	   // po.setUOM(rs.getString("MMEIN"));
//
//		             	    //po.setALP_GRP(rs.getString("ALPGR"));
//		             	   // po.setPrior(rs.getString("ALPRF"));
//		             	    //po.setUsge_pro(rs.getString("EWAHR"));
//		             	    po.setABC(MAABC);//x
//		             	    po.setSafeStock(EISBE);//X
//		             	    po.setGR_PT(WEBAZ);//x
//		             	   //// po.setBLKQty(rs.getDouble("SPEME"));//xxxx
//		             	   ////po.setBlock(rs.getDouble("BLOCK"));//xxxxx
//		             	   ////po.setIn_Transit(rs.getDouble("TRANSIT"));//xxxxx
//		             	    //po.setUsage(rs.getDouble("MNGLG"));
//		             	    po.setLT(PLIFZ);//x
//		             	    po.setMOQ(BSTMI);//x
//		             	    po.setMPQ(BSTRF);//x
//
//		             	    po.setMCCode(EKGRP);
//		             	    po.setMCName(EKNAM);//x
//		             	    po.setLIFNR_FIX(LIFNR);//xxxxxx
//		             	    po.setBugroup(ZZBU);//x
//		             	    po.setPmcgroup(PMCP);//x
//		             	    po.setCustcode(EXTWG);//x
//
//
//		             	   po.setDISPO(DISPO);
//		             	   po.setDSNAM(DSNAM);
//		             	    po.setDZEIT(DZEIT);
//		             	   if(!BESKZ.equals("F")){
//		             		  list.add(po);
//		             	   }
//
//		             	  if(po.getPART().startsWith("660") || po.getPART().startsWith("661") || po.getPART().startsWith("662") || po.getPART().startsWith("663")){
//		      				list.add(po);
//
//			      			}else{
//			      				if(!BESKZ.equals("F")){
//			  	    				list.add(po);
//			  	        	    }
//			      			}
//
//	            	    }
//	            	    rs.nextRow();
//	            }
//
//		return list;
//	}
//
//	//查询物料信息
//	public PlanOrder Z_WAP_GET_MATERIAL(PlanOrder po,String Plant,String pn )throws Exception{
//
//	    JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//		if (function == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//
//
//		JCoParameterList input = function.getImportParameterList();
//		input.setValue("I_MATNR", pn);// partNumber
//		input.setValue("I_WERKS", Plant);// plent
//
//
//		  try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//
//			// 获取输出
//			JCoStructure E_MARA = function.getExportParameterList().getStructure("E_MARA");
//			String ZBU = E_MARA.getString("ZZBU");
//			String BISMT = E_MARA.getString("BISMT");
//			String NORMT = E_MARA.getString("NORMT");
//			String ZZBU = E_MARA.getString("ZZBU");
//			String EXTWG = E_MARA.getString("EXTWG");
//
//
//			JCoStructure E_MARC = function.getExportParameterList().getStructure("E_MARC");
//			String BESKZ = E_MARC.getString("BESKZ");
//			String MAABC = E_MARC.getString("MAABC");
//			String EISBE = E_MARC.getString("EISBE");
//			String WEBAZ = E_MARC.getString("WEBAZ");
//			String PLIFZ = E_MARC.getString("PLIFZ");
//			Double BSTMI = E_MARC.getDouble("BSTMI");
//			Double BSTRF = E_MARC.getDouble("BSTRF");
//
//			String DZEIT = E_MARC.getString("DZEIT");
//
//
//			JCoStructure E_ZCPMP2 = function.getExportParameterList().getStructure("E_ZCPMP2");
//			String PMCP = E_ZCPMP2.getString("PMCP");
//
//			JCoStructure E_T024D = function.getExportParameterList().getStructure("E_T024D");
//			String DISPO = E_T024D.getString("DISPO");
//			String DSNAM = E_T024D.getString("DSNAM");
//
//
//	     	//System.out.println(ZBU);
//
//	     	//po.setSchedul_id(Integer.parseInt(id));
//
//     	    //po.setPLANT(rs.getString("WERKS"));
//     	    //po.setSO("");
//     	   // po.setPN(sl.getPn());
//     	    //po.setPART(rs.getString("IDNRK"));
//     	    //po.setDESCRIPTION(rs.getString("OJTXP"));
//
//     	    po.setOMSPN(BISMT);//x
//     	    po.setCustomerPN(NORMT);//x
//     	    //po.setMTLTYPE(rs.getString("MTART"));
//     	    //po.setEFFDate(rs.getString("DATUV"));
//
//     	    po.setPURTYPE(BESKZ);//x
//     	   // po.setUOM(rs.getString("MMEIN"));
//
//     	    //po.setALP_GRP(rs.getString("ALPGR"));
//     	   // po.setPrior(rs.getString("ALPRF"));
//     	    //po.setUsge_pro(rs.getString("EWAHR"));
//     	    po.setABC(MAABC);//x
//     	    po.setSafeStock(EISBE);//X
//     	    po.setGR_PT(WEBAZ);//x
//     	   //// po.setBLKQty(rs.getDouble("SPEME"));//xxxx
//     	   ////po.setBlock(rs.getDouble("BLOCK"));//xxxxx
//     	   ////po.setIn_Transit(rs.getDouble("TRANSIT"));//xxxxx
//     	    //po.setUsage(rs.getDouble("MNGLG"));
//     	    po.setLT(PLIFZ);//x
//     	    po.setMOQ(BSTMI);//x
//     	    po.setMPQ(BSTRF);//x
//
//     	    //po.setMCCode(rs.getString("EKGRP"));
//     	    //po.setMCName(rs.getString("EKNAM"));//x
//     	    ////po.setLIFNR_FIX(rs.getString("LIFNR_FIX"));//xxxxxx
//     	    po.setBugroup(ZZBU);//x
//     	    po.setPmcgroup(PMCP);//x
//     	    po.setCustcode(EXTWG);//x
//
//
//     	   po.setDISPO(DISPO);
//     	   po.setDSNAM(DSNAM);
//     	    po.setDZEIT(DZEIT);
//
//
//
//	return po;
//}
//	/**
//	 * 查询物料信息 -- 接口名字：Z_SPT_GET_VENDOR_RID
//	 * @param po
//	 * @param I_MATNR 物料编码
//	 * @param I_LIFNR 供应商编号
//	 * @param I_EXTWG 客户编号
//	 * @param I_WERKS 工厂
//	 * @param I_MFRPN 制造商物料编码
//	 * @return
//	 */
//	public List<SchedulList> Z_DC_GET_YPPR23B(String id,String WERKS) throws Exception{
//		List<SchedulList> list  =  new ArrayList();
//
//
//
//		JCoDestination destination = JCoDestinationManager
//				.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction(
//				"Z_DC_GET_YPPR23B");// 接口方法名字
//
//		if (function == null)
//			throw new RuntimeException(
//					"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//		  JCoTable ZDC = function.getTableParameterList().getTable("I_WERKS");
//
//	      ZDC.appendRow();
//		  ZDC.setValue("WERKS",WERKS);//printQTY
//
//
//
//
//
//
//		try {
//			function.execute(destination); //
//		} catch (AbapException e) {
//			e.printStackTrace();
//			System.out.println(e.toString());
//		}
//
//		JCoParameterList tables=function.getTableParameterList();
//		JCoTable rs = function.getTableParameterList().getTable("E_OUT");
//
//
//		for (int j = 0; j < rs.getNumRows(); j++) {
//
//	            	    SchedulList scl = new SchedulList();
//	            	    scl.setSchedul_id(Integer.parseInt(id));
//	            	    scl.setSo(rs.getString("AUFNR"));
//	            	    scl.setPlant(rs.getString("WERKS"));
//	            	    scl.setPn(rs.getString("MATNR"));
//
//	            	    scl.setFEVOR(rs.getString("FEVOR"));
//	            	    scl.setTXT(rs.getString("TXT"));
//	            	    scl.setDISPO(rs.getString("DISPO"));
//	            	    scl.setOddqty(rs.getDouble("GAMNG"));
//	            	    scl.setDSNAM(rs.getString("DSNAM"));
//	            	    scl.setWEMNG(rs.getString("WEMNG"));
//	            	    scl.setGASMG(rs.getString("GASMG"));
//
//	            	    scl.setIAMNG(rs.getString("IAMNG"));
//	            	    scl.setFTRMI(rs.getString("FTRMI"));
//	            	    scl.setGSTRP(rs.getString("GSTRP"));
//
//	            	    scl.setGETRI(rs.getString("GETRI"));
//	            	    scl.setIDAT2(rs.getString("IDAT2"));
//	            	    scl.setUSNAM(rs.getString("USNAM"));
//
//	            	    scl.setSTATUS(rs.getString("STATUS"));
//	            	    scl.setCOMDAY(rs.getString("COMDAY"));
//	            	    scl.setFLAG(rs.getString("FLAG"));
//
//	            	    scl.setNORMAL(rs.getString("NORMAL"));
//	            	    scl.setRUNRESULT(rs.getString("RUNRESULT"));
//	            	    scl.setWorkcenter(rs.getString("ARBPL"));
//
//
//	            	    list.add(scl);
//	            	    rs.nextRow();
//	            }
//
//
//		return list;
//	}
//
//	public List<SchedulList> Z_DC_GET_YPPR23B_wip(String id,String WERKS) throws Exception{
//		List<SchedulList> list  =  new ArrayList();
//
//
//
//		JCoDestination destination = JCoDestinationManager
//				.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction(
//				"Z_DC_GET_YPPR23B");// 接口方法名字
//
//		//JCoFunction function2 = destination.getRepository().getFunction("BAPI_PRODORD_GET_DETAIL");// 接口方法名字
//		if (function == null)
//			throw new RuntimeException(
//					"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//		  JCoTable ZDC = function.getTableParameterList().getTable("I_WERKS");
//
//	      ZDC.appendRow();
//		  ZDC.setValue("WERKS",WERKS);//printQTY
//
//
//
//
//
//
//		try {
//			function.execute(destination); //
//		} catch (AbapException e) {
//			e.printStackTrace();
//			System.out.println(e.toString());
//		}
//
//		JCoParameterList tables=function.getTableParameterList();
//		JCoTable rs = function.getTableParameterList().getTable("E_OUT");
//		JCoTable rs2 = function.getTableParameterList().getTable("E_MNG");
//
//
//
//		Map allWITHDRAWN_QUANTITY = new HashMap<String,String>();
//		for (int j = 0; j < rs2.getNumRows(); j++) {
//			if((String.valueOf(allWITHDRAWN_QUANTITY.get(rs2.getString("AUFNR")))).equals("null")){
//				allWITHDRAWN_QUANTITY.put(rs2.getString("AUFNR"), rs2.getDouble("ENMNG"));
//			}
//			rs2.nextRow();
//		}
//		for (int j = 0; j < rs.getNumRows(); j++) {
//
//	            	    SchedulList scl = new SchedulList();
//	            	    scl.setSchedul_id(Integer.parseInt(id));
//	            	    scl.setSo(rs.getString("AUFNR"));
//	            	    scl.setPlant(rs.getString("WERKS"));
//	            	    scl.setPn(rs.getString("MATNR"));
//
//	            	    scl.setFEVOR(rs.getString("FEVOR"));
//	            	    scl.setTXT(rs.getString("TXT"));
//	            	    scl.setDISPO(rs.getString("DISPO"));
//	            	    scl.setOddqty(rs.getDouble("GAMNG"));
//	            	    scl.setDSNAM(rs.getString("DSNAM"));
//	            	    scl.setWEMNG(rs.getString("WEMNG"));
//	            	    scl.setGASMG(rs.getString("GASMG"));
//
//	            	    scl.setIAMNG(rs.getString("IAMNG"));
//	            	    scl.setFTRMI(rs.getString("FTRMI"));
//	            	    scl.setGSTRP(rs.getString("GSTRP"));
//
//	            	    scl.setGETRI(rs.getString("GETRI"));
//	            	    scl.setIDAT2(rs.getString("IDAT2"));
//	            	    scl.setUSNAM(rs.getString("USNAM"));
//
//	            	    scl.setSTATUS(rs.getString("STATUS"));
//	            	    scl.setCOMDAY(rs.getString("COMDAY"));
//	            	    scl.setFLAG(rs.getString("FLAG"));
//
//	            	    scl.setNORMAL(rs.getString("NORMAL"));
//	            	    scl.setRUNRESULT(rs.getString("RUNRESULT"));
//	            	    scl.setWorkcenter(rs.getString("ARBPL"));
//	            	    //查询工单下任意已发料数
//	            	    if(!rs.getString("FTRMI").equals("0000-00-00")){
//
//	            	    //double WITHDRAWN_QUANTITY =0;
//						if(!(String.valueOf(allWITHDRAWN_QUANTITY.get(rs.getString("AUFNR")))).equals("null")){
//							//WITHDRAWN_QUANTITY = Double.valueOf(String.valueOf(allWITHDRAWN_QUANTITY.get(rs.getString("AUFNR")))); //短缺;
//							scl.setWITHDRAWN_QUANTITY(1);
//
//						}else{
//							scl.setWITHDRAWN_QUANTITY(0);
//						}
//						//System.out.println(rs.getString("AUFNR")+"   "+rs.getString("FTRMI")+"  "+WITHDRAWN_QUANTITY);
//	            	    }
//
//	            	    list.add(scl);
//	            	    rs.nextRow();
//	            }
//
//
//		return list;
//	}
//
//
//	/**
//	 * 查询物料信息 -- 接口名字：Z_DC_GET_MB52
//	 * @param plant 工厂
//	 * @param pn 物料编码
//
//	 * @return
//	 */
//	public List<Mb52> Z_DC_GET_MB52(String plant,String pn)throws Exception{
//		List<Mb52> list  =  new ArrayList();
//		JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_MB52");// 接口方法名字
//if (function == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//
//JCoTable ZDC2 = function.getTableParameterList().getTable("T_MATNR");
//ZDC2.appendRow();
//ZDC2.setValue("MATNR",pn);//getPn
//
//JCoTable ZDC = function.getTableParameterList().getTable("T_WERKS");
//ZDC.appendRow();
//ZDC.setValue("WERKS",plant);//WERKS
//
//   try {
//		function.execute(destination); //
//	} catch (AbapException e) {
//		e.printStackTrace();
//		System.out.println(e.toString());
//	}
//
//	JCoParameterList tables=function.getTableParameterList();
//	JCoTable rs = function.getTableParameterList().getTable("T_OUT");
//
//					for (int i = 0; i < rs.getNumRows(); i++) {
//						Mb52 mb = new Mb52();
//
//						mb.setLGORT(rs.getString("LGORT"));
//						mb.setSOBKZ(rs.getString("SOBKZ"));
//						mb.setSSNUM(rs.getString("SSNUM"));
//						mb.setCHARG(rs.getString("CHARG"));
//						mb.setMEINS(rs.getString("MEINS"));
//
//						mb.setLABST(rs.getDouble("LABST"));
//						mb.setUMLME(rs.getDouble("UMLME"));
//						mb.setINSME(rs.getDouble("INSME"));
//						mb.setEINME(rs.getDouble("EINME"));
//						mb.setSPEME(rs.getDouble("SPEME"));
//						mb.setRETME(rs.getDouble("RETME"));
//						mb.setMCCode(rs.getString("EKGRP"));
//						mb.setMCName(rs.getString("EKNAM"));
//						list.add(mb);
//						 rs.nextRow();
//					}
//		return list;
//	}
//	/**
//	 * 查询物料信息 -- 接口名字：Z_SPT_GET_VENDOR_RID
//	 * @param po
//	 * @param I_MATNR 物料编码
//	 * @param I_LIFNR 供应商编号
//	 * @param I_EXTWG 客户编号
//	 * @param I_WERKS 工厂
//	 * @param I_MFRPN 制造商物料编码
//	 * @return
//	 */
//	public List<OsPoList> doDownYMMR08L(String plant,String pn) throws Exception {
//		List<OsPoList> list = new ArrayList();
//		JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction("Z_SPT_GET_VENDOR_RID");// 接口方法名字
//		if (function == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//		JCoParameterList input = function.getImportParameterList();
//		input.setValue("I_WERKS", plant);// po
//		input.setValue("I_MATNR", pn);// vendorcode
//
//
//		try {
//			function.execute(destination); //
//		} catch (AbapException e) {
//			e.printStackTrace();
//			System.out.println(e.toString());
//		}
//		JCoParameterList tables=function.getTableParameterList();
//		JCoTable rs = function.getTableParameterList().getTable("ET_VENDORRID");
//
//	            for (int i=0; i<rs.getNumRows(); i++){
//	            	OsPoList lt =new OsPoList();
//
//	                	lt.setCustomerCode(rs.getString("EXTWG"));
//	                	lt.setPlant(rs.getString("WERKS"));
//	                	lt.setBuyerName(rs.getString("EKNAM"));
//	                	lt.setSupplierCode(rs.getString("LIFNR"));
//	                	lt.setSupplierName(rs.getString("NAME1").replace("'", "''"));
//
//	                	lt.setSAPPartNumber(rs.getString("MATNR"));
//	                	lt.setABCIndicate(rs.getString("MAABC"));
//	                	lt.setDescription(rs.getString("MAKTX").replace("'", "''"));
//	                	lt.setLT(rs.getString("PLIFZ"));
//	                	lt.setManufPartNumber(rs.getString("MFRPN"));
//
//	                	lt.setManufactoryName(rs.getString("MFRNAME").replace("'", "''"));
//	                	lt.setCustomerPartNumber(rs.getString("NORMT"));
//	                	lt.setPONumber(rs.getString("EBELN"));
//	                	lt.setPOItem(rs.getString("EBELP"));
//	                	lt.setPOIssueDate(rs.getString("BEDAT"));
//
//	                	lt.setPOOrderedQTY(rs.getDouble("MENGE"));
//	                	lt.setOutstandingQTY(rs.getDouble("MENGE1"));
//	                	lt.setOrginalPOETA(rs.getString("LFDAT"));
//	                	lt.setLastestETA(rs.getString("EINDT"));
//	                	lt.setNewETA(rs.getString("UMDAT2"));
//
//	                 	lt.setSupplierAction(rs.getString("SUAM"));
//	                	lt.setCancellationWindow(rs.getString("MHDRZ"));
//	                	lt.setExceptional_Code1(rs.getString("AUSKT"));
//	                	lt.setExceptional_Code2(rs.getString("AUSKT1"));
//	                	lt.setReleased(rs.getString("RELEA"));
//	                	lt.setUnreleas(rs.getString("UNREL"));
//
//	                 	lt.setSendTyp(rs.getString("STYPE"));
//	                	lt.setPaymentT(rs.getString("ZTERM"));
//	                	lt.setTaxCode(rs.getString("MWSKZ"));
//	                	lt.setTaxCodeDescription(rs.getString("TEXT1"));
//	                	lt.setDate1(rs.getString("DSDAT"));
//
//	                 	lt.setTime(rs.getString("DSTMP"));
//	                	lt.setRemark(rs.getString("REMARK"));
//	                	lt.setUnit(rs.getString("MEINS"));
//	                	lt.setUpdatedUser(rs.getString("USERNAME"));
//	                	lt.setUpdatedDate(rs.getString("UDATE"));
//
//	                 	lt.setTcode(rs.getString("TCODE"));
//	                	lt.setXYZ(rs.getString("ZMAXYZ"));
//	                	lt.setEF(rs.getString("EFTXT"));
//	                	lt.setCurrency(rs.getString("WAERS"));
//	                	lt.setUnitCost(rs.getString("NETPR"));
//
//	                 	lt.setTotalamount_FX(rs.getString("NETWR"));
//	                	lt.setTotalamount_HKD(rs.getString("LOCAL_CURRENCY"));
//	                	lt.setExpectedPaydate(rs.getString("EXP_DATE"));
//
//						list.add(lt);
//						 rs.nextRow();
//	            }
//
//
//		return list;
//	}
//
//	/**
//	 * 查询物料信息 -- 接口名字：Z_DC_GET_BOM_RESB3 coois
//	 * @param po
//	 * @param I_MATNR 物料编码
//	 * @param I_LIFNR 供应商编号
//	 * @param I_EXTWG 客户编号
//	 * @param I_WERKS 工厂
//	 * @param I_MFRPN 制造商物料编码
//	 * @return
//	 */
//	public List<WorkOrder> Z_DC_GET_BOM_RESB3(SchedulList sl,String id ,List<MG> listMG,List<MGPN> listMGPN )throws Exception{
//		List<WorkOrder> list  =  new ArrayList();
//
//
//		JCoDestination destination = JCoDestinationManager
//				.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction(
//				"Z_DC_GET_BOM_RESB3");// 接口方法名字
//		if (function == null)
//			throw new RuntimeException(
//					"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//		JCoParameterList input = function.getImportParameterList();
//
//		input.setValue("I_AUFNR", sl.getSo());
//
//		try {
//			function.execute(destination); //
//		} catch (AbapException e) {
//			e.printStackTrace();
//			System.out.println(e.toString());
//		}
//		JCoParameterList tables=function.getTableParameterList();
//		JCoTable rs = function.getTableParameterList().getTable("T_ZDCBOM_RESB");
//					for (int j = 0; j < rs.getNumRows(); j++) {
//
//						//根据物料组+工厂进行判断
//						boolean d = false;
//						for(int m= 0;m<listMG.size();m++){
//							if(rs.getString("MATNR").startsWith(listMG.get(m).getMG()) && rs.getString("WERKS").equals(listMG.get(m).getPlant())){
//								d = true;
//								break;
//							}
//						}
//						//根据工厂物料进行判断
//						if(!d){
//							for(int n= 0;n<listMGPN.size();n++){
//								if(rs.getString("MATNR").equals(listMGPN.get(n).getPN()) && rs.getString("WERKS").equals(listMGPN.get(n).getPlant())){
//									d = true;
//									break;
//								}
//							}
//						}
//
//					    if(d){
//						WorkOrder wo = new WorkOrder();
//						wo.setMATNR(rs.getString("MATNR"));
//						wo.setPOSNR(rs.getString("POSNR"));
//						wo.setBDTER(rs.getString("BDTER"));
//						wo.setBDMNG(rs.getDouble("BDMNG"));
//						wo.setENMNG(rs.getDouble("ENMNG"));
//						//-------
//
//						double DFMENG = rs.getDouble("BDMNG") - rs.getDouble("ENMNG");
//						int DFMENG2 = (new   Double(DFMENG)).intValue();
//				 		if(DFMENG2<(new   Double(DFMENG))){
//				 			DFMENG2++;
//				 		}
//						wo.setDFMENG(DFMENG2);
//						wo.setMEINS(rs.getString("MEINS"));
//						wo.setMAKTX(rs.getString("MAKTX_P").replace("'", "''"));
//						wo.setCHARG(rs.getString("CHARG"));
//						wo.setLGORT(rs.getString("LGORT"));
//
//						wo.setDUMPS(rs.getString("DUMPS"));
//						wo.setRGEKZ(rs.getString("RGEKZ"));
//
//						wo.setXLOEK(rs.getString("XLOEK"));
//
//						wo.setMTLTYPE(rs.getString("MTART"));
//						wo.setGR_PT(rs.getString("WEBAZ"));
//						wo.setLT(rs.getString("PLIFZ"));
//						wo.setMOQ(rs.getDouble("BSTMI") );
//						wo.setMPQ(rs.getDouble("BSTRF"));
//						wo.setZZBU(rs.getString("ZZBU"));
//						wo.setEXTWG(rs.getString("EXTWG"));
//						wo.setPMCP(rs.getString("PMCP"));
//						wo.setMCCode(rs.getString("EKGRP"));
//						wo.setMCName(rs.getString("EKNAM"));
//						wo.setLIFNR_FIX(rs.getString("LIFNR_FIX"));
//						wo.setARBPL(rs.getString("ARBPL"));
//
//						list.add(wo);
//					    }
//					    rs.nextRow();
//					}
//
//
//
//
//		return list;
//	}
//	//coois WIP
//	public List<WorkOrder> Z_DC_GET_BOM_RESB3WIP(SchedulList sl,String id ,List<MG> listMG,List<MGPN> listMGPN )throws Exception{
//		List<WorkOrder> list  =  new ArrayList();
//
//
//		JCoDestination destination = JCoDestinationManager
//				.getDestination(ABAP_AS_POOLED);
//		JCoFunction function = destination.getRepository().getFunction(
//				"Z_DC_GET_BOM_RESB3");// 接口方法名字
//		JCoFunction function2 = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//
//		if (function == null)
//			throw new RuntimeException(
//					"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//		JCoParameterList input = function.getImportParameterList();
//
//		input.setValue("I_AUFNR", sl.getSo());
//
//		try {
//			function.execute(destination); //
//		} catch (AbapException e) {
//			e.printStackTrace();
//			System.out.println(e.toString());
//		}
//		JCoParameterList tables=function.getTableParameterList();
//		JCoTable rs = function.getTableParameterList().getTable("T_ZDCBOM_RESB");
//					for (int j = 0; j < rs.getNumRows(); j++) {
//
//						//根据物料组+工厂进行判断
//						boolean d = false;
//						for(int m= 0;m<listMG.size();m++){
//							if(rs.getString("MATNR").startsWith(listMG.get(m).getMG()) && rs.getString("WERKS").equals(listMG.get(m).getPlant())){
//								d = true;
//								break;
//							}
//						}
//						//根据工厂物料进行判断
//						if(!d){
//							for(int n= 0;n<listMGPN.size();n++){
//								if(rs.getString("MATNR").equals(listMGPN.get(n).getPN()) && rs.getString("WERKS").equals(listMGPN.get(n).getPlant())){
//									d = true;
//									break;
//								}
//							}
//						}
//
//					    if(d && !rs.getString("DUMPS").equals("X") && !rs.getString("DUMPS").equals("x")){
//						WorkOrder wo = new WorkOrder();
//						wo.setMATNR(rs.getString("MATNR"));
//						wo.setPOSNR(rs.getString("POSNR"));
//						wo.setBDTER(rs.getString("BDTER"));
//						wo.setBDMNG(rs.getDouble("BDMNG"));
//						wo.setENMNG(rs.getDouble("ENMNG"));
//						//-------
//
//						double DFMENG = rs.getDouble("BDMNG") - rs.getDouble("ENMNG");
//						int DFMENG2 = (new   Double(DFMENG)).intValue();
//				 		if(DFMENG2<(new   Double(DFMENG))){
//				 			DFMENG2++;
//				 		}
//						wo.setDFMENG(DFMENG2);
//						wo.setMEINS(rs.getString("MEINS"));
//						wo.setMAKTX(rs.getString("MAKTX_P").replace("'", "''"));
//						wo.setCHARG(rs.getString("CHARG"));
//						wo.setLGORT(rs.getString("LGORT"));
//
//						wo.setDUMPS(rs.getString("DUMPS"));
//						wo.setRGEKZ(rs.getString("RGEKZ"));
//
//						wo.setXLOEK(rs.getString("XLOEK"));
//
//						wo.setMTLTYPE(rs.getString("MTART"));
//						wo.setGR_PT(rs.getString("WEBAZ"));
//						wo.setLT(rs.getString("PLIFZ"));
//						wo.setMOQ(rs.getDouble("BSTMI") );
//						wo.setMPQ(rs.getDouble("BSTRF"));
//						wo.setZZBU(rs.getString("ZZBU"));
//						wo.setEXTWG(rs.getString("EXTWG"));
//
//						wo.setMCCode(rs.getString("EKGRP"));
//						wo.setMCName(rs.getString("EKNAM"));
//						wo.setLIFNR_FIX(rs.getString("LIFNR_FIX"));
//						wo.setARBPL(rs.getString("ARBPL"));
//
//						//查询其他信息 Z_WAP_GET_MATERIAL
//	            	    JCoParameterList input2 = function2.getImportParameterList();
//	            	    input2.clear();
//	        			input2.setValue("I_MATNR", rs.getString("MATNR"));// partNumber
//	        			input2.setValue("I_WERKS", rs.getString("WERKS"));// plent
//	        			function2.execute(destination);
//	        			// 获取输出
//	        			JCoStructure E_MARC = function2.getExportParameterList().getStructure("E_MARC");
//
//	        			String DZEIT = E_MARC.getString("DZEIT");
//	        			String BESKZ = E_MARC.getString("BESKZ");
//
//
//	        			JCoStructure E_T024D = function2.getExportParameterList().getStructure("E_T024D");
//	        			String DISPO = E_T024D.getString("DISPO");
//	        			String DSNAM = E_T024D.getString("DSNAM");
//	        			wo.setDISPO(DISPO);
//	        			wo.setDSNAM(DSNAM);
//	        			wo.setDZEIT(DZEIT);
//	        			JCoStructure E_ZCPMP2 = function2.getExportParameterList().getStructure("E_ZCPMP2");
//	        			String PMCP = E_ZCPMP2.getString("PMCP");
//	        			wo.setPMCP(PMCP);
//
//
//
//	        			 if(wo.getMATNR().startsWith("660") || wo.getMATNR().startsWith("661") || wo.getMATNR().startsWith("662") || wo.getMATNR().startsWith("663")){
//			      				list.add(wo);
//
//				      			}else{
//				      				if(!BESKZ.equals("F")){
//				  	    				list.add(wo);
//				  	        	    }
//				      			}
//
//
//					    }
//					    rs.nextRow();
//					}
//
//
//
//
//		return list;
//	}
//
//	//下载数据sap_pi.printVendorRID(po, partNumber,vendorcode, customerCode, plent, manufPartNumber);
//	public List<PoList> Z_SPT_GET_VENDOR_RID(String po,String I_MATNR,String I_LIFNR
//			,String I_EXTWG,String I_WERKS,String I_MFRPN,String decimal){
//		List<PoList> list = new ArrayList();
//		try {
//			System.out.println(I_LIFNR+" Z_SPT_GET_VENDOR_RID 1 :"+(new Date()).toString() );
//			if(decimal.equals("")){
//				Map session = ActionContext.getContext().getSession();
//				decimal=(String)session.get("decimal");
//			}
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					"Z_SPT_GET_VENDOR_RID");// 接口方法名字
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("I_EBELN", po);// po
//			input.setValue("I_LIFNR", I_LIFNR);// vendorcode
//			input.setValue("I_MATNR", I_MATNR);// partNumber
//			input.setValue("I_EXTWG", I_EXTWG);// customerCode
//			input.setValue("I_WERKS", I_WERKS);// plent
//			input.setValue("I_MFRPN", I_MFRPN);// manufPartNumber
//
//			try {
//				function.execute(destination); //
//
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ET_VENDORRID");
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//	     		boolean b = false;
//        	    if(i>0){
//        	    	if(rs.getString("EBELN").equals(list.get(list.size()-1).getPo()) &&rs.getString("EBELP").equals(list.get(list.size()-1).getPoItem())){
//        	    		b = true;
//        	    		//int PoOutQTY = Integer.parseInt(list.get(list.size()-1).getPoOutQTY());
//        	    		//PoOutQTY += Integer.parseInt(rs.getString("MENGE1").toString().substring(0,rs.getString("MENGE1").toString().indexOf(".")));
//
//        	    		if(!rs.getString("EBELN").startsWith("55")){
//        	    			String ALLnewETA = list.get(list.size()-1).getALLnewETA();
//        	    			ALLnewETA += rs.getString("UMDAT2")+" "+rs.getString("MENGE1")+",";
//        	    			list.get(list.size()-1).setALLnewETA(ALLnewETA);
//        	    		}
//        	    	}
//        	    }
//        	    if(!b){
//        	    	PoList lt =new PoList();
//                	lt.setCustomerCode(rs.getString("EXTWG"));
//                	lt.setPlent(rs.getString("WERKS"));
//                	lt.setSupplierCode(rs.getString("LIFNR"));
//                	lt.setSupplierName(rs.getString("NAME1"));
//                	lt.setPartNumber(rs.getString("MATNR"));
//
//                	lt.setPartdesc(rs.getString("MAKTX").replace("'", ""));
//                	lt.setLt(rs.getString("PLIFZ"));
//                	lt.setManufPartNumber(rs.getString("MFRPN"));
//                	lt.setManufactoryName(rs.getString("MFRNAME"));
//
//                	lt.setPo(rs.getString("EBELN"));
//                	lt.setPoItem(rs.getString("EBELP"));
//                	lt.setPoIssueDate(rs.getString("BEDAT"));
//                	//lt.setPoOutQTY(rs.getInt("MENGE1")+"");
//                	lt.setLastestETA(rs.getString("EINDT"));
//
//                	if(rs.getString("EBELN").startsWith("55")){
//                		lt.setNewETA("0000-00-00");
//                		lt.setALLnewETA("0000-00-00"+" "+rs.getString("MENGE2")+",");//ETA+X+数量
//                	}else{
//                		lt.setNewETA(rs.getString("UMDAT2"));
//                		lt.setALLnewETA(rs.getString("UMDAT2")+" "+rs.getString("MENGE1"));
//                		lt.setALLnewETA(rs.getString("UMDAT2")+" "+(int)Double.parseDouble(rs.getString("MENGE1"))+",");
//
//                		//Double.parseDouble(lt.getPoOrderedQTY())
//                	}
//
//
//                	lt.setPoETA(rs.getString("LFDAT"));
//                	lt.setMSD(rs.getString("ZMSL"));
//
//                	lt.setEKNAM(rs.getString("EKNAM"));
//                	lt.setSMTP_ADDR(rs.getString("SMTP_ADDR"));
//                	lt.setTEL_NUMBER(rs.getString("TEL_NUMBER"));
//
//                	lt.setZUIDVAL(rs.getString("ZUIDVAL"));
//
//                	lt.setPoUnit(rs.getString("MEINS"));// 採購單計量單位
//            		lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//            		int UMREN = rs.getInt("UMREN");
//            		int UMREZ = rs.getInt("UMREZ");
//
//            		lt.setUMREN(rs.getInt("UMREN"));//采购单位 与  基本单位 比例
//            		lt.setUMREZ(rs.getInt("UMREZ"));
//
//                	String XCHAR = rs.getString("XCHAR");
//                	if(XCHAR.equals("X")){
//                		XCHAR = "Y";
//                	}else{
//                		XCHAR = "N";
//                	}
//                	lt.setISbatch(XCHAR);//-----是否批次
//                	String StockName1 = rs.getString("GROES");
//                	if(StockName1!=null && !StockName1.equals("")){
//                		if(!StockName1.contains("*")){
//                			lt.setStockName1(StockName1);
//                		}else{
//                			String[] StockName1_s = StockName1.split("\\*");
//                			if(StockName1_s.length==2){
//                    			lt.setStockName1(StockName1.split("\\*")[1]);
//                			}else{
//                    			lt.setStockName1(StockName1.split("\\*")[0]);
//                			}
//                		}
//                	}else{
//                		lt.setStockName1("");
//                	}
//
//
//
//                	if(decimal.contains(rs.getString("LIFNR"))){
//                		lt.setMinbm(Double.valueOf(rs.getString("MINBM")));
//                		lt.setPoOrderedQTY(Double.valueOf(rs.getString("MENGE2"))+"");
//                	}else{
//
//                	double MINBM1 = Double.valueOf(rs.getString("MINBM")); //最小包装
//                	if(MINBM1==0){
//                		MINBM1=1;
//                	}
//            		System.out.println(MINBM1);
//            		//Double MINBM2 = MINBM1;
//            		int MINBM2 = (int)MINBM1;
//                	if(MINBM1==MINBM2){//如果相等  最小包装不包含小数
//                		//判断采购总数包不包含小数。
//                		double MENGE2Temp = Double.valueOf(rs.getString("MENGE2")); //采购数量
//                		int MENGE2Temp2 = (int)MENGE2Temp;
//                		if(MENGE2Temp2==MENGE2Temp){
//                			lt.setMinbm(MINBM1);
//                    		lt.setPoOrderedQTY(MENGE2Temp2+"");
//                		}else{
//                			//MINBM2 = (MINBM1*(UMREZ/UMREN));
//                			lt.setMinbm(MINBM1*(UMREZ/UMREN));
//
//                			lt.setPoOrderedQTY((int)(rs.getDouble("MENGE2")*(UMREZ/UMREN))+"");
//
//                			lt.setUMREN(1);//已经转换的后续不用再转换
//                    		lt.setUMREZ(1);
//                    		lt.setPoUnit(lt.getUnit());// 採購單計量單位 与 基本单位统一
//                    		//lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//                    		//lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//                		}
//                	}else{
//                		lt.setMinbm(MINBM1*(UMREZ/UMREN));
//                		lt.setPoOrderedQTY((int)(rs.getDouble("MENGE2")*(UMREZ/UMREN))+"");
//                		lt.setUMREN(1);//已经转换的后续不用再转换
//                		lt.setUMREZ(1);
//                		lt.setPoUnit(lt.getUnit());// 採購單計量單位 与 基本单位统一
//
//                	}
//                	}
//					list.add(lt);
//            	   }
//	     	    rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//	public  List<VendorRid>  Z_SPT_GET_VENDOR_RID3(String po,String I_EBELP){
//
//		List<VendorRid> list = new ArrayList();
//		try {
//			//Map session = ActionContext.getContext().getSession();
//			//String decimal=(String)session.get("decimal");
//			String decimal="";
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					"Z_SPT_GET_VENDOR_RID");// 接口方法名字
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("I_EBELN", po);// po
//			input.setValue("I_EBELP", I_EBELP);// poitem
//
//			try {
//				function.execute(destination); //
//
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ET_VENDORRID");
//			Double PoOutQTY = 0.0;
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//
//	     		    VendorRid lt = new VendorRid();
//                	lt.setCustomerCode(rs.getString("EXTWG"));
//                	lt.setPlent(rs.getString("WERKS"));
//                	lt.setSupplierCode(rs.getString("LIFNR"));
//                	lt.setSupplierName(rs.getString("NAME1"));
//                	lt.setPartNumber(rs.getString("MATNR"));
//
//                	lt.setPartdesc(rs.getString("MAKTX").replace("'", ""));
//                	lt.setLt(rs.getString("PLIFZ"));
//                	lt.setManufPartNumber(rs.getString("MFRPN"));
//                	lt.setManufactoryName(rs.getString("MFRNAME"));
//
//                	lt.setPo(rs.getString("EBELN"));
//                	lt.setPoItem(rs.getString("EBELP"));
//                	lt.setPoIssueDate(rs.getString("BEDAT"));
//
//                	//lt.setPoOutQTY(rs.getInt("MENGE1")+"");
//
//                	PoOutQTY +=  Double.valueOf(rs.getString("MENGE1"));
//                	lt.setLastestETA(rs.getString("EINDT"));
//                	lt.setNewETA(rs.getString("UMDAT2"));
//                	lt.setPoETA(rs.getString("LFDAT"));
//                	lt.setMSD(rs.getString("ZMSL"));
//
//                	lt.setEKNAM(rs.getString("EKNAM"));
//                	lt.setSMTP_ADDR(rs.getString("SMTP_ADDR"));
//                	lt.setTEL_NUMBER(rs.getString("TEL_NUMBER"));
//                	lt.setZUIDVAL(rs.getString("ZUIDVAL"));
//
//                	lt.setPoUnit(rs.getString("MEINS"));
//                	String XCHAR = rs.getString("XCHAR");
//
//                	lt.setPoUnit(rs.getString("MEINS"));// 採購單計量單位
//            		lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//            		int UMREN = rs.getInt("UMREN");
//            		int UMREZ = rs.getInt("UMREZ");
//
//            		lt.setUMREN(rs.getInt("UMREN"));//采购单位 与  基本单位 比例
//            		lt.setUMREZ(rs.getInt("UMREZ"));
//            		lt.setPonewETATemp("");
//                	if(XCHAR.equals("X")){
//                		XCHAR = "Y";
//                	}else{
//                		XCHAR = "N";
//                	}
//                	lt.setISbatch(XCHAR);//-----是否批次
//
//
//                	String StockName1 = rs.getString("GROES");
//                	if(StockName1!=null && !StockName1.equals("")){
//                		if(!StockName1.contains("*")){
//                			lt.setStockName1(StockName1);
//                		}else{
//                			String[] StockName1_s = StockName1.split("\\*");
//
//
//                			if(StockName1_s.length==2){
//
//                    			lt.setStockName1(StockName1.split("\\*")[1]);
//                			}else{
//
//                    			lt.setStockName1(StockName1.split("\\*")[0]);
//                			}
//                		}
//                	}else{
//                		lt.setStockName1("");
//                	}
//					if(decimal.contains(rs.getString("LIFNR"))){
//                		lt.setMinbm(Double.valueOf(rs.getString("MINBM")));
//                		lt.setPoOrderedQTY(Double.valueOf(rs.getString("MENGE1"))+"");
//                	}else{
//
//                	double MINBM1 = Double.valueOf(rs.getString("MINBM")); //最小包装
//
//
//            		//Double MINBM2 = MINBM1;
//            		int MINBM2 = (int)MINBM1;
//                	if(MINBM1==MINBM2){//如果相等  最小包装不包含小数
//                		//判断采购总数包不包含小数。
//                		double MENGE2Temp = Double.valueOf(rs.getString("MENGE1")); //采购数量
//                		int MENGE2Temp2 = (int)MENGE2Temp;
//                		if(MENGE2Temp2==MENGE2Temp){
//                			lt.setMinbm(MINBM1);
//                    		lt.setPoOrderedQTY(MENGE2Temp2+"");
//                		}else{
//                			//MINBM2 = (MINBM1*(UMREZ/UMREN));
//                			lt.setMinbm(MINBM1*(UMREZ/UMREN));
//                			lt.setPoOrderedQTY((int)(rs.getDouble("MENGE1")*(UMREZ/UMREN))+"");
//                			PoOutQTY =PoOutQTY*(UMREZ/UMREN);
//                			lt.setUMREN(1);//已经转换的后续不用再转换
//                    		lt.setUMREZ(1);
//                    		lt.setPoUnit(lt.getUnit());// 採購單計量單位 与 基本单位统一
//                    		lt.setPonewETATemp((UMREZ/UMREN)+"");
//                    		//lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//                		}
//                	}else{
//                		lt.setMinbm(MINBM1*(UMREZ/UMREN));
//                		lt.setPoOrderedQTY((int)(rs.getDouble("MENGE1")*(UMREZ/UMREN))+"");
//                		PoOutQTY =PoOutQTY*(UMREZ/UMREN);
//                		lt.setPonewETATemp((UMREZ/UMREN)+"");
//                		lt.setUMREN(1);//已经转换的后续不用再转换
//                		lt.setUMREZ(1);
//                		lt.setPoUnit(lt.getUnit());// 採購單計量單位 与 基本单位统一
//
//                	}
//                	}
//                	list.add(lt);
//                	rs.nextRow();
//
//	     	}
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//	/**
//     *  Z_AEGIS_GOODSMVT_GR GRN收货 101  105
//     *  String MVT_type//过账类型
//     *  String MFILE//物料文件
//     *  String DeliveryOrder//送货单号
//     *  String TableFile//表头内文
//     *  String STGE_LOC//SAP目标仓位
//     *  String plent//工厂
//     *  String partNumber//物料
//     *  String PO
//     *  String POITEM
//     *  String printQTY//数量
//     *  StringBuffer sb
//     *  StringBuffer E_CHARG//批号
//     *  StringBuffer E_PRUEFLOS//检验批次
//     *  StringBuffer E_MJAHR //过账年份
//     *  StringBuffer SPEC_STOCK//特殊库存
//     *  GR_RCPT //收货人
//     *  ITEM_TEXT// 项目内文
//     *  UNLOAD_PT//SAP备注
//     *  E_MJAHRtemp 过账年份
//     * @return
//     */
//    public String Z_AEGIS_GOODSMVT_GR(String MVT_type,String MFILE,String DeliveryOrder,String TableFile,
//    		String STGE_LOC,String plent,String partNumber,String PO,String POITEM,
//    		String printQTY,String GR_RCPT,String ITEM_TEXT,String UNLOAD_PT,String E_MJAHRtemp,
//    		StringBuffer sb,StringBuffer E_CHARG,StringBuffer E_PRUEFLOS,
//    		StringBuffer E_MJAHR ,StringBuffer SPEC_STOCK,StringBuffer GROES,
//    		StringBuffer EFNO,StringBuffer PATH,String COO,String unit,String tempE_CHARG,List<Grn_543> listGrn_543, StringBuffer SAP_STGE_LOC)
//    {
//
//    	String GRN ="";
//
//    	String E_MJAHR1 ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_GR");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_XBLNR", DeliveryOrder.toUpperCase());//送货单号
//
//	         input.setValue("I_BKTXT", TableFile.toUpperCase());//表头内文
//	         input.setValue("I_BWART", MVT_type);//异动类型
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISGR");
//	         if(PO.startsWith("42")){
//	        	 ZDC.appendRow();
//		         ZDC.setValue("ENTRY_QNT",printQTY);//printQTY   ENTRY_UOM单位
//		         ZDC.setValue("ENTRY_UOM",unit);
//		         ZDC.setValue("MATERIAL",partNumber);//物料
//		         ZDC.setValue("PLANT",plent);//plent
//		         ZDC.setValue("PO_ITEM",POITEM);//PO_ITEM
//		         ZDC.setValue("PO_NUMBER",PO);//PO
//		         ZDC.setValue("REF_DOC",MFILE);//文件号码103 101 ""
//		         ZDC.setValue("REF_DOC_YR",E_MJAHRtemp);//參考文件的會計年度
//		         ZDC.setValue("STGE_LOC",STGE_LOC);//SAP目标仓位
//
//		         ZDC.setValue("GR_RCPT",GR_RCPT);//收貨人/收貨方
//		         ZDC.setValue("UNLOAD_PT",UNLOAD_PT);//备注
//		         ZDC.setValue("ITEM_TEXT",ITEM_TEXT);//項目內文
//		         ZDC.setValue("UNLOAD_PT",COO);//备注
//		         ZDC.setValue("LINE_ID","00001");//备注
//
//		         ZDC.setValue("BATCH",tempE_CHARG);//批次号码
//
//
//		         for(int i=0;i<listGrn_543.size();i++){
//		        	 Grn_543 grm543 = listGrn_543.get(i);
//		        	 String[] grnQTY = grm543.getGrnQTY().split(",");
//		        	 String[] batch = grm543.getBatch().split(",");
//
//		        	 for(int j = 0;j<grnQTY.length;j++){
//		        		 String tempbatch = batch[j];
//		        		 if(tempbatch.equals("0000000000")){
//		        			 tempbatch = "";
//		        		 }
//		        		 ZDC.appendRow();
//		        		 ZDC.setValue("BATCH",batch[j]);
//				         ZDC.setValue("ENTRY_QNT",grnQTY[j]);//printQTY
//				         ZDC.setValue("MATERIAL",grm543.getPn());//物料
//				         ZDC.setValue("MOVE_TYPE","543");//543
//				         ZDC.setValue("PLANT",grm543.getPlent());//plent
//				         ZDC.setValue("PO_ITEM",POITEM);//PO_ITEM
//				         ZDC.setValue("PO_NUMBER",PO);//PO
//				         ZDC.setValue("PARENT_ID","00001");//备注
//		        	 }
//		         }
//	         }else{
//	        	 ZDC.appendRow();
//	        	 int tempprintQTY3 = (int)Double.parseDouble(printQTY);
//	        	 if(tempprintQTY3!= Double.parseDouble(printQTY)){
//	        		 ZDC.setValue("ENTRY_QNT",Double.parseDouble(printQTY));//printQTY
//	        	 }else{
//	        		 ZDC.setValue("ENTRY_QNT",tempprintQTY3);//printQTY
//	        	 }
//
//
//
//		         ZDC.setValue("ENTRY_UOM",unit);
//		         ZDC.setValue("MATERIAL",partNumber);//物料
//		         ZDC.setValue("PLANT",plent);//plent
//		         ZDC.setValue("PO_ITEM",POITEM);//PO_ITEM
//		         ZDC.setValue("PO_NUMBER",PO);//PO
//		         ZDC.setValue("REF_DOC",MFILE);//文件号码103 101 ""
//		         ZDC.setValue("REF_DOC_YR",E_MJAHRtemp);//參考文件的會計年度
//		         ZDC.setValue("STGE_LOC",STGE_LOC);//SAP目标仓位
//
//		         ZDC.setValue("GR_RCPT",GR_RCPT);//收貨人/收貨方
//		         ZDC.setValue("UNLOAD_PT",UNLOAD_PT);//备注
//		         ZDC.setValue("ITEM_TEXT",ITEM_TEXT);//項目內文
//		         ZDC.setValue("UNLOAD_PT",COO);//备注
//
//		         ZDC.setValue("BATCH",tempE_CHARG);//批次号码
//	         }
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//
//	     	    if(GRN==null){
//	     		  GRN = "";
//	     	     }
//	     	     E_MJAHR1 = exports.getString("E_MJAHR");
//	     	     if(E_MJAHR1==null){
//	     		   E_MJAHR1 = "";
//		     	 }
//	     	    sb.setLength(0);
//	     	    E_MJAHR.append(E_MJAHR1);
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE")+"\r\n");
//		     	        rs.nextRow();
//		     	    }
//	     	   }else{
//	     		  JCoTable rs = function.getTableParameterList().getTable("IT_AEGISGR");
//		     	    for (int j = 0; j < rs.getNumRows() &&j<1; j++) {
//		     	        E_CHARG.append(rs.getString("E_CHARG"));
//		     	        E_PRUEFLOS.append(rs.getString("E_PRUEFLOS"));
//		     	        SPEC_STOCK.append(rs.getString("SPEC_STOCK"));
//		     	        SAP_STGE_LOC.append(rs.getString("STGE_LOC"));
//		     	        String StockName1 = rs.getString("GROES");
//	                	if(StockName1!=null && !StockName1.equals("")){
//	                		if(!StockName1.contains("*")){
//	                			GROES.append(StockName1);
//	                		}else{
//
//	                			String[] StockName1_s = StockName1.split("\\*");
//	                			if(StockName1_s.length==2){
//
//	                				GROES.append(StockName1.split("\\*")[1]);
//	                			}else{
//
//	                				GROES.append(StockName1.split("\\*")[0]);
//	                			}
//	                		}
//	                	}
//
//		     	        EFNO.append(rs.getString("EFNO"));
//		     	        PATH.append(rs.getString("PATH1")+"\\"+rs.getString("PATH2")+"\\"+rs.getString("PATH3"));
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    System.out.println(GRN);
//	    return GRN;
//    }
//
//
//    /**
//     *  Z_AEGIS_GOODSMVT_GR GRN收货
//     * @return
//     */
//    public String Z_AEGIS_GOODSMVT_GR103(String MVT_type,String MFILE,String DeliveryOrder,String TableFile,String STGE_LOC,
//    		List cooList,StringBuffer sb,StringBuffer E_CHARG,StringBuffer E_PRUEFLOS,StringBuffer E_MJAHR,StringBuffer SPEC_STOCK)
//    {
//
//    	String GRN ="";
//    	String E_MJAHR1 ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_GR");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_XBLNR", DeliveryOrder.toUpperCase());//送货单号
//	         input.setValue("I_BKTXT", TableFile.toUpperCase());//表头内文
//	         input.setValue("I_BWART", MVT_type);//异动类型
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISGR");
//
//
//	         for(int c = 0;c<cooList.size();c++){
//	        		Map maprowSO = (Map)cooList.get(c);
//	        	   	String plent = String.valueOf(maprowSO.get("plent"));
//	        	   	String partNumber = String.valueOf(maprowSO.get("partNumber"));
//	        		String PO = String.valueOf(maprowSO.get("PO"));
//	        	   	String POITEM = String.valueOf(maprowSO.get("POITEM"));
//	        	    String printQTY = String.valueOf(maprowSO.get("printQTY"));
//	        	    String coo = String.valueOf(maprowSO.get("coo"));
//	        	    String WEIGHT = String.valueOf(maprowSO.get("WEIGHT"));
//	        	    String BOXNO = String.valueOf(maprowSO.get("BOXNO"));
//	        		String UMREN = String.valueOf(maprowSO.get("UMREN"));
//	        	   	String UMREZ = String.valueOf(maprowSO.get("UMREZ"));
//	        	   	String unit = String.valueOf(maprowSO.get("unit"));
//	        	   	if(UMREN.equals("null")){
//						UMREN="0";
//					}
//					if(UMREZ.equals("null")){
//						UMREZ="0";
//					}
//					double printQTY2 = Double.parseDouble(printQTY);
//
//					double tempprintQTY2 = 0.000;
//
//					if(!UMREN.equals("0")&&!UMREN.equals("0")){
//						double UMREN2 = Double.parseDouble(UMREN);
//						double UMREZ2 = Double.parseDouble(UMREZ);
//						tempprintQTY2 = printQTY2*(UMREZ2/UMREN2);
//
//					}else{
//						tempprintQTY2 = printQTY2;
//						unit = "";
//					}
//					printQTY = tempprintQTY2+"";
//
//					ZDC.appendRow();
//
//
//	        	 int tempprintQTY3 = (int)Double.parseDouble(printQTY);
//
//	        	 if(tempprintQTY3!= Double.parseDouble(printQTY)){
//	        		 ZDC.setValue("ENTRY_QNT",Double.parseDouble(printQTY));//printQTY
//	        	 }else{
//	        		 ZDC.setValue("ENTRY_QNT",tempprintQTY3);//printQTY
//	        	 }
//
//	        	 ZDC.setValue("ENTRY_UOM",unit);
//		         ZDC.setValue("MATERIAL",partNumber);//物料
//		         ZDC.setValue("PLANT",plent);//plent
//		         ZDC.setValue("PO_ITEM",POITEM);//PO_ITEM
//		         ZDC.setValue("PO_NUMBER",PO);//PO
//		         ZDC.setValue("REF_DOC",MFILE);//文件号码
//		         ZDC.setValue("STGE_LOC",STGE_LOC);//
//		         ZDC.setValue("GR_RCPT",WEIGHT);//收貨人/收貨方
//		         ZDC.setValue("UNLOAD_PT",coo);//备注
//		         ZDC.setValue("ITEM_TEXT",BOXNO);//項目內文
//	         }
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//
//	     	    E_MJAHR1 = exports.getString("E_MJAHR");
//	     	    E_MJAHR.append(E_MJAHR1);
//	     	   if(GRN==null){
//	     		  GRN = "";
//	     	     }
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE")+"\r\n");
//		     	        rs.nextRow();
//		     	    }
//	     	   }else{
//	     		  JCoTable rs = function.getTableParameterList().getTable("IT_AEGISGR");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	    	if(j==0){
//		     	    		E_CHARG.append(rs.getString("E_CHARG"));
//		     	    		E_PRUEFLOS.append(rs.getString("E_PRUEFLOS"));
//		     	    		SPEC_STOCK.append(rs.getString("SPEC_STOCK"));
//		     	    	}else{
//		     	    		E_CHARG.append(","+rs.getString("E_CHARG"));
//		     	    		E_PRUEFLOS.append(","+rs.getString("E_PRUEFLOS"));
//		     	    		SPEC_STOCK.append(","+rs.getString("SPEC_STOCK"));
//		     	    	}
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return GRN;
//    }
//
//
//    public List<PonewETATemp> Z_SPT_GET_VENDOR_RID2(String YMD,String week){
//		List<PonewETATemp> list = new ArrayList();
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_SPT_GET_VENDOR_RID2");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_UDATE",YMD);//更改日期
//	         //input.setValue("I_UDATE", "2017-2-16");//更改日期
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ET_VENDORRID");
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//
//	     		if(week.equals("星期一")){//('PG00','PD03','PH00','PU01','RM01','SM00')
//	     			if(rs.getString("EXTWG").endsWith("PG00") ||
//	     					rs.getString("EXTWG").endsWith("PD03") ||
//	     					rs.getString("EXTWG").endsWith("PH00") ||
//	     					rs.getString("EXTWG").endsWith("PU01") ||
//	     					rs.getString("EXTWG").endsWith("RM01") ||
//	     					rs.getString("EXTWG").endsWith("SM00") ){
//	     				PonewETATemp lt =new PonewETATemp();
//	                	lt.setPo(rs.getString("EBELN"));
//	                	lt.setPoItem(rs.getString("EBELP"));
//	                	lt.setNewETA(rs.getString("UMDAT2"));
//	                	lt.setSupplierCode(rs.getString("LIFNR"));
//	                	lt.setMENGE1(rs.getDouble("MENGE1"));
//	                	lt.setLFDAT(rs.getString("LFDAT"));
//	                	lt.setEINDT(rs.getString("EINDT"));
//	                	lt.setAUSKT(rs.getString("AUSKT"));
//	                	lt.setAUSKT1(rs.getString("AUSKT1"));
//	                	lt.setMATNR(rs.getString("MATNR"));
//	                	lt.setSupplierName(rs.getString("NAME1"));
//	                	lt.setEKGRP(rs.getString("EKGRP"));
//	                	lt.setWERKS(rs.getString("WERKS"));
//	                	lt.setEXTWG(rs.getString("EXTWG"));
//						list.add(lt);
//	     			}
//	     		}else{
//	     			if(!rs.getString("EXTWG").endsWith("PG00") &&
//	     					!rs.getString("EXTWG").endsWith("PD03") &&
//	     					!rs.getString("EXTWG").endsWith("PH00") &&
//	     					!rs.getString("EXTWG").endsWith("PU01") &&
//	     					!rs.getString("EXTWG").endsWith("RM01") &&
//	     					!rs.getString("EXTWG").endsWith("SM00") ){
//	     				PonewETATemp lt =new PonewETATemp();
//	                	lt.setPo(rs.getString("EBELN"));
//	                	lt.setPoItem(rs.getString("EBELP"));
//	                	lt.setNewETA(rs.getString("UMDAT2"));
//	                	lt.setSupplierCode(rs.getString("LIFNR"));
//	                	lt.setMENGE1(rs.getDouble("MENGE1"));
//	                	lt.setLFDAT(rs.getString("LFDAT"));
//	                	lt.setEINDT(rs.getString("EINDT"));
//	                	lt.setAUSKT(rs.getString("AUSKT"));
//	                	lt.setAUSKT1(rs.getString("AUSKT1"));
//	                	lt.setMATNR(rs.getString("MATNR"));
//	                	lt.setSupplierName(rs.getString("NAME1"));
//	                	lt.setEKGRP(rs.getString("EKGRP"));
//	                	lt.setWERKS(rs.getString("WERKS"));
//	                	lt.setEXTWG(rs.getString("EXTWG"));
//						list.add(lt);
//	     			}
//	     		}
//
//
//	     	        rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//
//    public VendorRid Z_SPT_GET_VENDOR_RID(String po,String I_EBELP){
//		VendorRid lt = null;
//		try {
//			//Map session = ActionContext.getContext().getSession();
//			//String decimal=(String)session.get("decimal");
//			String decimal="";
//			JCoDestination destination = JCoDestinationManager
//					.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction(
//					"Z_SPT_GET_VENDOR_RID");// 接口方法名字
//			if (function == null)
//				throw new RuntimeException(
//						"BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			JCoParameterList input = function.getImportParameterList();
//			input.setValue("I_EBELN", po);// po
//			input.setValue("I_EBELP", I_EBELP);// poitem
//
//			try {
//				function.execute(destination); //
//
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ET_VENDORRID");
//			Double PoOutQTY = 0.0;
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//
//        	    	lt = new VendorRid();
//                	lt.setCustomerCode(rs.getString("EXTWG"));
//                	lt.setPlent(rs.getString("WERKS"));
//                	lt.setSupplierCode(rs.getString("LIFNR"));
//                	lt.setSupplierName(rs.getString("NAME1"));
//                	lt.setPartNumber(rs.getString("MATNR"));
//
//                	lt.setPartdesc(rs.getString("MAKTX").replace("'", ""));
//                	lt.setLt(rs.getString("PLIFZ"));
//                	lt.setManufPartNumber(rs.getString("MFRPN"));
//                	lt.setManufactoryName(rs.getString("MFRNAME"));
//
//                	lt.setPo(rs.getString("EBELN"));
//                	lt.setPoItem(rs.getString("EBELP"));
//                	lt.setPoIssueDate(rs.getString("BEDAT"));
//
//                	//lt.setPoOutQTY(rs.getInt("MENGE1")+"");
//                	PoOutQTY +=  Double.valueOf(rs.getString("MENGE1"));
//                	lt.setLastestETA(rs.getString("EINDT"));
//                	lt.setNewETA(rs.getString("UMDAT2"));
//                	lt.setPoETA(rs.getString("LFDAT"));
//                	lt.setMSD(rs.getString("ZMSL"));
//
//                	lt.setEKNAM(rs.getString("EKNAM"));
//                	lt.setSMTP_ADDR(rs.getString("SMTP_ADDR"));
//                	lt.setTEL_NUMBER(rs.getString("TEL_NUMBER"));
//                	lt.setZUIDVAL(rs.getString("ZUIDVAL"));
//
//                	lt.setPoUnit(rs.getString("MEINS"));
//                	String XCHAR = rs.getString("XCHAR");
//
//                	lt.setPoUnit(rs.getString("MEINS"));// 採購單計量單位
//            		lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//            		int UMREN = rs.getInt("UMREN");
//            		int UMREZ = rs.getInt("UMREZ");
//
//            		lt.setUMREN(rs.getInt("UMREN"));//采购单位 与  基本单位 比例
//            		lt.setUMREZ(rs.getInt("UMREZ"));
//            		lt.setPonewETATemp("");
//                	if(XCHAR.equals("X")){
//                		XCHAR = "Y";
//                	}else{
//                		XCHAR = "N";
//                	}
//                	lt.setISbatch(XCHAR);//-----是否批次
//
//
//                	String StockName1 = rs.getString("GROES");
//                	if(StockName1!=null && !StockName1.equals("")){
//                		if(!StockName1.contains("*")){
//                			lt.setStockName1(StockName1);
//                		}else{
//                			String[] StockName1_s = StockName1.split("\\*");
//
//
//                			if(StockName1_s.length==2){
//
//                    			lt.setStockName1(StockName1.split("\\*")[1]);
//                			}else{
//
//                    			lt.setStockName1(StockName1.split("\\*")[0]);
//                			}
//                		}
//                	}else{
//                		lt.setStockName1("");
//                	}
//                	if(decimal.contains(rs.getString("LIFNR"))){
//                		lt.setMinbm(Double.valueOf(rs.getString("MINBM")));
//                		lt.setPoOrderedQTY(Double.valueOf(rs.getString("MENGE2"))+"");
//                	}else{
//
//                	double MINBM1 = Double.valueOf(rs.getString("MINBM")); //最小包装
//
//
//            		//Double MINBM2 = MINBM1;
//            		int MINBM2 = (int)MINBM1;
//                	if(MINBM1==MINBM2){//如果相等  最小包装不包含小数
//                		//判断采购总数包不包含小数。
//                		double MENGE2Temp = Double.valueOf(rs.getString("MENGE2")); //采购数量
//                		int MENGE2Temp2 = (int)MENGE2Temp;
//                		if(MENGE2Temp2==MENGE2Temp){
//                			lt.setMinbm(MINBM1);
//                    		lt.setPoOrderedQTY(MENGE2Temp2+"");
//                		}else{
//                			//MINBM2 = (MINBM1*(UMREZ/UMREN));
//                			lt.setMinbm(MINBM1*(UMREZ/UMREN));
//                			lt.setPoOrderedQTY((int)(rs.getDouble("MENGE2")*(UMREZ/UMREN))+"");
//                			PoOutQTY =PoOutQTY*(UMREZ/UMREN);
//                			lt.setUMREN(1);//已经转换的后续不用再转换
//                    		lt.setUMREZ(1);
//                    		lt.setPoUnit(lt.getUnit());// 採購單計量單位 与 基本单位统一
//                    		lt.setPonewETATemp((UMREZ/UMREN)+"");
//                    		//lt.setUnit(rs.getString("LMEIN"));//单位 用基本单位
//                		}
//                	}else{
//                		lt.setMinbm(MINBM1*(UMREZ/UMREN));
//                		lt.setPoOrderedQTY((int)(rs.getDouble("MENGE2")*(UMREZ/UMREN))+"");
//                		PoOutQTY =PoOutQTY*(UMREZ/UMREN);
//                		lt.setPonewETATemp((UMREZ/UMREN)+"");
//                		lt.setUMREN(1);//已经转换的后续不用再转换
//                		lt.setUMREZ(1);
//                		lt.setPoUnit(lt.getUnit());// 採購單計量單位 与 基本单位统一
//
//                	}
//                	}
//                	rs.nextRow();
//	     	}
//	     	if(lt != null){
//	     		lt.setPoOutQTY(PoOutQTY+"");
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return lt;
//	}
//
//
//
//    /**
//     *  Z_AEGIS_GOODSMVT_RE 102
//     * @return
//     */
//    public String Z_AEGIS_GOODSMVT_RE(String MVT_type,String I_XBLNR,String I_BKTXT,String I_BWART,
//    		String ENTRY_QNT,
//    		String MATERIAL,
//    		String PLANT,
//    		String PO_ITEM,
//    		String PO_NUMBER,
//    		String STGE_LOC,StringBuffer sb)
//    {
//
//    	String GRN ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_RE");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         if(I_BWART.equals("102")){
//	        	 input.setValue("I_XBLNR", I_XBLNR);//參考文件號碼
//	        	 input.setValue("I_BKTXT", I_BKTXT);//文件表頭內文
//	        	 input.setValue("I_BWART", I_BWART);//异动类型
//	         }
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISGR");
//
//	         ZDC.appendRow();
//
//	         ZDC.setValue("ENTRY_QNT",ENTRY_QNT);//printQTY
//	         ZDC.setValue("MATERIAL",MATERIAL);//物料
//	         ZDC.setValue("MOVE_TYPE",MVT_type);//异动类型
//	         ZDC.setValue("PLANT",PLANT);//plent
//	         ZDC.setValue("PO_ITEM",PO_ITEM);//PO_ITEM
//	         ZDC.setValue("PO_NUMBER",PO_NUMBER);//PO
//	         ZDC.setValue("STGE_LOC",STGE_LOC);//
//
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//
//	     	   if(GRN==null){
//	     		  GRN = "";
//	     	   }
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE"));
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return GRN;
//    }
//
//
//    /**
//     *  Z_AEGIS_GOODSMVT_TRANSFER 311
//     * @return
//     */
//    public String Z_AEGIS_GOODSMVT_TRANSFER311(String MVT_type,String I_XBLNR,String I_BKTXT,String I_BWART,
//    		String ENTRY_QNT,
//    		String MATERIAL,
//    		String MOVE_REAS,
//    		String MOVE_STLOC,
//    		String PLANT,
//    		String STGE_LOC,
//    		StringBuffer sb)
//    {
//
//    	String GRN ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_TRANSFER");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//
//	        	 input.setValue("I_XBLNR", I_XBLNR);//參考文件號碼
//	        	 input.setValue("I_BKTXT", I_BKTXT);//文件表頭內文
//	        	 input.setValue("I_BWART", I_BWART);//异动类型
//
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISTR");
//
//	         ZDC.appendRow();
//
//	         ZDC.setValue("ENTRY_QNT",ENTRY_QNT);//printQTY
//	         ZDC.setValue("MATERIAL",MATERIAL);//物料
//	         ZDC.setValue("MOVE_REAS",MOVE_REAS);//異動原因
//	         ZDC.setValue("MOVE_STLOC",MOVE_STLOC);//接收儲存位置
//	         ZDC.setValue("PLANT",PLANT);//plent
//	         ZDC.setValue("STGE_LOC",STGE_LOC);//
//
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//	     	   if(GRN==null){
//	     		  GRN = "";
//	     	   }
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE"));
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return GRN;
//    }
//
//    /**
//     *  Z_AEGIS_GOODSMVT_TRANSFER 541
//     * @return
//     */
//    public String Z_AEGIS_GOODSMVT_TRANSFER541(String MVT_type,String I_XBLNR,String I_BKTXT,String I_BWART,
//    		String ENTRY_QNT,
//    		String MATERIAL,
//    		String MOVE_STLOC,
//    		String PLANT,
//    		String PO_ITEM,
//    		String PO_NUMBER,
//    		String STGE_LOC,
//    		String VENDOR,
//
//    		StringBuffer sb)
//    {
//
//    	String GRN ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_TRANSFER");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//
//	        	 input.setValue("I_XBLNR", I_XBLNR);//參考文件號碼
//	        	 input.setValue("I_BKTXT", I_BKTXT);//文件表頭內文
//	        	 input.setValue("I_BWART", I_BWART);//异动类型
//
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISTR");
//
//	         ZDC.appendRow();
//
//	         ZDC.setValue("ENTRY_QNT",ENTRY_QNT);//printQTY
//	         ZDC.setValue("MATERIAL",MATERIAL);//物料
//	         ZDC.setValue("MOVE_STLOC",MOVE_STLOC);//接收儲存位置
//	         ZDC.setValue("PLANT",PLANT);//plent
//	         ZDC.setValue("PO_ITEM",PO_ITEM);//PO_ITEM
//	         ZDC.setValue("PO_NUMBER",PO_NUMBER);//PO
//	         ZDC.setValue("STGE_LOC",STGE_LOC);//
//	         ZDC.setValue("VENDOR",VENDOR);//
//
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//	     	   if(GRN==null){
//	     		  GRN = "";
//	     	   }
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE"));
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return GRN;
//    }
//
//    /**
//     *  Z_AEGIS_GOODSMVT_DELIVER 261
//     * @return
//     */
//    public String Z_AEGIS_GOODSMVT_DELIVER(String MVT_type,String I_XBLNR,String I_BKTXT,String I_BWART,
//    		String ENTRY_QNT,
//    		String MATERIAL,
//    		String MOVE_TYPE,
//    		String ORDERID,
//    		String ORDER_ITNO,
//    		String PLANT,
//    		String STGE_LOC,
//    		StringBuffer sb)
//    {
//
//    	String GRN ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_DELIVER");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//
//	        	 input.setValue("I_XBLNR", I_XBLNR);//參考文件號碼
//	        	 input.setValue("I_BKTXT", I_BKTXT);//文件表頭內文
//	        	 input.setValue("I_BWART", I_BWART);//异动类型
//
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISDELIVER");
//
//	         ZDC.appendRow();
//
//	         ZDC.setValue("ENTRY_QNT",ENTRY_QNT);//printQTY
//	         ZDC.setValue("MATERIAL",MATERIAL);//物料
//	         ZDC.setValue("MOVE_TYPE",MOVE_TYPE);//接收儲存位置
//	         ZDC.setValue("ORDERID",ORDERID);//PO_ITEM
//	         ZDC.setValue("ORDER_ITNO",ORDER_ITNO);//PO_ITEM
//	         ZDC.setValue("PLANT",PLANT);//plent
//	         ZDC.setValue("STGE_LOC",STGE_LOC);//
//
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//	     	   if(GRN==null){
//	     		  GRN = "";
//	     	   }
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE"));
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return GRN;
//    }
//
//
//    /**
//     *  Z_AEGIS_GOODSMVT_DELIVER 261
//     * @return
//     */
//    public String WEEK_GET_FIRST_DAY(String yyyywk)
//    {
//
//    	String  DATE ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("WEEK_GET_FIRST_DAY");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //yyyywk
//	         input.setValue("WEEK", yyyywk);//yyyywk
//
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    DATE = exports.getString("DATE");
//
//	         }
//	         catch(AbapException e)
//	         {
//	             //System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return  DATE;
//    }
//
//
//    /**
//     *  模組     Z_AEGIS_GETPOSTATUS  判断PO POITEM状态是否可以收货
//     * @return
//     */
//    public String Z_AEGIS_GETPOSTATUS(String po,String poitem,StringBuffer sb)
//    {
//
//    	String  E_TYPE ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GETPOSTATUS");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //yyyywk
//	         input.setValue("I_EBELN", po);//po
//	         input.setValue("I_EBELP", poitem);//poitem
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    E_TYPE = exports.getString("E_TYPE");
//	     	    if(!E_TYPE.equals("X")){
//	     	    	sb.append(exports.getString("E_MESSAGE"));
//	     	    }
//	         }
//	         catch(AbapException e)
//	         {
//	             //System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return  E_TYPE;
//    }
//
//    //拆BOM
//    public List<Grn_543> Z_AEGIS_GET_PUR_BOM(String po,String poitem){
//		List<Grn_543> list = new ArrayList();
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_PUR_BOM");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_EBELN",po);
//	         input.setValue("I_EBELP",poitem);
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//			 JCoStructure js =  exportsE_EKPO.getStructure("E_EKPO");
//
//			 Double  MENGE =  js.getDouble("MENGE");
//
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("T_RESB");
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//	     		String RSNUM = rs.getString("RSNUM");
//	     		if(RSNUM.equals("9999999999")){
//	     		    Grn_543 lt =new Grn_543();
//                	lt.setPn(rs.getString("MATNR"));
//                	lt.setPlent(rs.getString("WERKS"));
//
//                	lt.setUnit(rs.getString("ERFME"));
//                	lt.setSupplierCode(rs.getString("LIFNR"));
//                	lt.setERFMG(Double.valueOf(rs.getString("ERFMG")));
//                	lt.setMENGE(MENGE);
//
//
//					list.add(lt);
//	     		}
//	     	    rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//    //拆BOM
//    public String  Z_AEGIS_GET_PUR_BOM(String po){
//    	String vendorcode= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_PUR_BOM");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_EBELN",po);
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//			 JCoStructure js =  exportsE_EKPO.getStructure("E_EKKO");
//
//			 vendorcode =  js.getString("LIFNR");
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return vendorcode;
//	}
//
//  //查询库存批次
//    public List<Grn_543> Z_AEGIS_GET_INVENTORY(String pn,String I_WERKS,String supplierCode){
//		List<Grn_543> list = new ArrayList();
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_INVENTORY");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_MATNR",pn);
//	         input.setValue("I_WERKS",I_WERKS);
//	         input.setValue("I_LIFNR",supplierCode);
//
//
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("T_INVENTORY");
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//	     		    Grn_543 lt =new Grn_543();
//                	lt.setPn(rs.getString("MATNR"));
//                	lt.setPlent(rs.getString("WERKS"));
//
//                	lt.setSuggestQTY(Double.valueOf(rs.getString("LABST")));
//                	lt.setUnit(rs.getString("MEINS"));
//                	String CHARG = rs.getString("CHARG");
//                	if(CHARG.equals("")){
//                		CHARG = "0000000000";
//                	}
//
//                	lt.setBatch(CHARG);
//
//					list.add(lt);
//	     	    rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//    //根据工厂 PN查询MSD
//    public String Z_DC_GET_MATERIAL(String plant,String pn)
//    {
//
//    	String  ZMSL ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_DC_GET_MATERIAL");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //yyyywk
//	         input.setValue("I_MATNR", pn);//po
//	         input.setValue("I_WERKS", plant);//poitem
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//
//	     	    JCoStructure js =  exports.getStructure("E_MARC");
//
//	     	   ZMSL =  js.getString("ZMSL");
//	         }
//	         catch(AbapException e)
//	         {
//	             //System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return  ZMSL;
//    }
//    public static List<File> getFileList(File fileDir,String fileType){
//        List<File> lfile = new ArrayList<File>();
//        File[] fs = fileDir.listFiles();
//        for(File f:fs){
//            if(f.isFile()){
//                if (fileType.equals(f.getName().substring(f.getName().lastIndexOf(".")+1, f.getName().length())))
//                    lfile.add(f);
//            }else{
//                List<File> ftemps = getFileList(f, fileType);
//                lfile.addAll(ftemps);
//            }
//        }
//        return lfile;
//    }
//
//  //根据物料+工厂查询物料描述
//    public String  ZEPR_GET_SIMPLE_MATERIAL(String plent,String pn){
//    	String desc= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("ZEPR_GET_SIMPLE_MATERIAL");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_WERKS",plent);
//	         input.setValue("I_MATNR",pn);
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//
//			 JCoParameterList exports = function.getExportParameterList();
//			 desc = exports.getString("E_MAKTX");
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return desc;
//	}
//
//
//	//提交对账单申请
//	public String[] commitAccApply(List<YFIR112018> unPayList,  AccApply accapp,
//			String billCompany, String tradeType, String totalAmt, String taxAmt, String invoiceDate, String postingDate, String assign) {
//		String sapmsg[] = new String[3];
//		NumberFormat nf = NumberFormat.getNumberInstance();  //去除千分位
//		nf.setGroupingUsed(false);
//		DecimalFormat df = new DecimalFormat();
//		try {
//			//
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_INVOICE_DATA_CREATE");//接口方法名字
//			if (function == null)
//				throw new RuntimeException("Z_INVOICE_DATA_CREATE not found in SAP.");
//			//给功能函数输入参数
//			JCoParameterList input = function.getImportParameterList();
//			JCoStructure header = input.getStructure("HEADERDATA");
//			header.setValue("DOC_DATE",invoiceDate);
//			header.setValue("PSTNG_DATE",postingDate);
//			header.setValue("COMP_CODE",billCompany);
//			header.setValue("CURRENCY",accapp.getCcy());
//			header.setValue("GROSS_AMOUNT",df.parse(totalAmt));
//			if("".equals(taxAmt)) {
//				header.setValue("CALC_TAX_IND","");
//			}else {
//				header.setValue("CALC_TAX_IND","X");
//			}
//			header.setValue("ALLOC_NMBR", assign);
//			header.setValue("RETURN_POSTING", "H");
//			//detail
//			JCoTable detail = function.getTableParameterList().getTable("ITEMDATA");
//			for (int i=0; i < unPayList.size(); i++) {
//				detail.appendRow();
//				detail.setValue("INVOICE_DOC_ITEM", i+1);
//				detail.setValue("PO_NUMBER", unPayList.get(i).getPofile());
//				detail.setValue("PO_ITEM", unPayList.get(i).getItem());
//				detail.setValue("REF_DOC", unPayList.get(i).getStoreno());
//				detail.setValue("REF_DOC_YEAR", unPayList.get(i).getLfbja());
//				detail.setValue("REF_DOC_IT", unPayList.get(i).getLfpos());
//				detail.setValue("TAX_CODE", unPayList.get(i).getTaxno());
//				//System.out.println(df.parse(unPayList.get(i).getTotalprice()));
//				String tempTotalPrice = String.valueOf(df.parse(unPayList.get(i).getTotalprice()));
//				if(Double.valueOf(tempTotalPrice) < 0.0) {
//					tempTotalPrice = String.valueOf(Double.valueOf(tempTotalPrice) * -1);
//				}
//				if(tempTotalPrice.indexOf(".") == -1) {
//					tempTotalPrice = tempTotalPrice+".00";
//				}else {
//					String[] last = tempTotalPrice.split("\\.");
//					if(last[1].length() == 1) {
//						tempTotalPrice = tempTotalPrice+"0";
//					}
//				}
//				detail.setValue("ITEM_AMOUNT", tempTotalPrice);
//				double tempQty = Double.valueOf(unPayList.get(i).getQty());
//				if(tempQty < 0.0) {
//					tempQty = tempQty * -1;
//				}
//				detail.setValue("QUANTITY", tempQty);
//				detail.setValue("PO_UNIT", unPayList.get(i).getUnit());
//				if(unPayList.get(i).getUnit().equals(unPayList.get(i).getUnit2())){
//					detail.setValue("PO_PR_UOM", "");
//				}else{
//					detail.setValue("PO_PR_UOM", unPayList.get(i).getUnit2());
//				}
//
//			}
//			//函数执行
//			try {
//				function.execute(destination);
//			} catch (AbapException e) {
//				e.printStackTrace();
//			}
//			JCoParameterList exports = function.getExportParameterList();
//			sapmsg[0] = exports.getString("RETURNSTR");
//			sapmsg[1] = exports.getString("INVOICEDOCNUMBER");
//			sapmsg[2] = exports.getString("FISCALYEAR");
//			System.out.println("commitAccApply:"+sapmsg[0]+","+sapmsg[1]+","+sapmsg[2]);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sapmsg;
//	}
//
//
//    //SMT261  HTP261   MC261
////    String PartNumber = String.valueOf(maprowSO.get("PartNumber"));
////	String Batch = String.valueOf(maprowSO.get("Batch"));
////	String PLENT = String.valueOf(maprowSO.get("PLENT"));
////	String WorkStation = String.valueOf(maprowSO.get("WorkStation"));
////	String ORDERID = String.valueOf(maprowSO.get("ORDERID"));
////	String QTY = String.valueOf(maprowSO.get("QTY"));
////
//    public String Z_AEGIS_GOODSMVT_DELIVER261(String UNLOAD_PT,String PartNumber,String Batch,String PLENT,String WorkStation,String ORDERID,String QTY,
//    		StringBuffer sb,StringBuffer E_MJAHR)
//    {
//
//    	String GRN ="";
//    	String E_MJAHR1 ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_DELIVER_VPS");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//
//
//	         //销售文件
//	         input.setValue("I_BWART", "261");//异动类型
//	         //抬头项
//	         JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISDELIVER");
//	        	 ZDC.appendRow();
//	        	 System.out.println(QTY+"  "+PartNumber+"  "+PLENT+"  "+ORDERID+"  "+Batch+"  "+WorkStation);
//	        	 ZDC.setValue("ENTRY_QNT",QTY);//--------------数量
//		         ZDC.setValue("MATERIAL",PartNumber);//--------物料
//		         ZDC.setValue("PLANT",PLENT);//----------------工厂
//		         ZDC.setValue("ORDERID",ORDERID);//------------工单   261专用
//		         ZDC.setValue("BATCH",Batch);//----------------批次
//		         ZDC.setValue("STGE_LOC",WorkStation);//-------仓位
//		         ZDC.setValue("UNLOAD_PT",UNLOAD_PT);//--------备注 HTP261 SMT261
//
//
//
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    GRN = exports.getString("E_MBLNR");
//
//	     	    E_MJAHR1 = exports.getString("E_MJAHR");
//	     	    E_MJAHR.append(E_MJAHR1);
//	     	   if(GRN==null){
//	     		  GRN = "";
//	     	     }
//	     	   if(GRN.equals("")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE")+"\r\n");
//		     	        rs.nextRow();
//		     	    }
//	     	   }
////	     	   else{
////	     		  JCoTable rs = function.getTableParameterList().getTable("IT_AEGISGR");
////		     	    for (int j = 0; j < rs.getNumRows(); j++) {
////		     	    	if(j==0){
////		     	    		E_CHARG.append(rs.getString("E_CHARG"));
////		     	    		E_PRUEFLOS.append(rs.getString("E_PRUEFLOS"));
////		     	    		SPEC_STOCK.append(rs.getString("SPEC_STOCK"));
////		     	    	}else{
////		     	    		E_CHARG.append(","+rs.getString("E_CHARG"));
////		     	    		E_PRUEFLOS.append(","+rs.getString("E_PRUEFLOS"));
////		     	    		SPEC_STOCK.append(","+rs.getString("SPEC_STOCK"));
////		     	    	}
////		     	        rs.nextRow();
////		     	    }
////	     	   }
//	         }
//	         catch(AbapException e)
//	         {
//	             System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return GRN;
//    }
//
//  //707PN工单增加Line Item
//    public String  Z_AEGIS_COMPONENT_ADD(String ORDERID,String MATNR,String QTY,StringBuffer sb){
//    	String desc= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_COMPONENT_ADD");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("AUFNR",ORDERID);
//	         input.setValue("MATNR",MATNR);
//	         input.setValue("QTY",QTY);
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//
//			 JCoParameterList exports = function.getExportParameterList();
//			 desc = exports.getString("SUCC");
//			 if(desc.equals("N")){
//		     	    JCoTable rs = function.getTableParameterList().getTable("MESSAGE");
//		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//		     	        sb.append(rs.getString("MESSAGE_TYPE")+","+rs.getString("MESSAGE_TEXT"));
//		     	        rs.nextRow();
//		     	    }
//	     	   }
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return desc;
//	}
//   //根据PO POITEM 时间范围查询已过账数
//    public String  Z_AEGIS_GET_PO_GRQTY(String po,String poitem,String timeStart,String timeEnd){
//    	String desc= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_PO_GRQTY");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_EBELN",po);
//	         input.setValue("I_EBELP",poitem);
//	         input.setValue("I_BUDATFR",timeStart);
//	         input.setValue("I_BUDATTO",timeEnd);
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//
//			 JCoParameterList exports = function.getExportParameterList();
//			 desc = exports.getString("E_MENGE");
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return desc;
//	}
//
//    //查询价格及海关编码
//    public String  Z_JAVA_GET_PO_PRICE(String GRN,String po,String poitem,String printQTY,
//    		StringBuffer E_NETPR,StringBuffer E_HGMAT,StringBuffer E_NTGEW,StringBuffer E_BRGEW){
//    	String desc= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_JAVA_GET_PO_PRICE");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_EBELN",po);//PO
//	         input.setValue("I_EBELP",poitem);//PO ITEM
//	         input.setValue("I_MENGE",printQTY);//数量
//	         input.setValue("I_MBLNR",GRN);//grn
//
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//
//			 JCoParameterList exports = function.getExportParameterList();
//			 String tempE_NETPR = exports.getString("E_NETPR");//价格
//			 String tempE_TOTAL = exports.getString("E_TOTAL");//总金额
//			 String tempE_NTGEW = exports.getString("E_NTGEW");//净重
//			 String tempE_BRGEW = exports.getString("E_BRGEW");//毛重
//			 E_NETPR.append(tempE_NETPR);
//			 E_HGMAT.append(tempE_TOTAL);
//			 E_NTGEW.append(tempE_NTGEW);
//			 E_BRGEW.append(tempE_BRGEW);
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return desc;
//	}
//
//    //查询PO价格 特审单用
//    public String  Z_JAVA_GET_PO_PRICE2(String po,String poitem,String printQTY,
//    		StringBuffer E_NETPR,StringBuffer E_TOTAL){
//    	String desc= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_JAVA_GET_PO_PRICE2");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_EBELN",po);
//	         input.setValue("I_EBELP",poitem);
//	         input.setValue("I_MENGE",printQTY);
//
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_EKPO = function.getExportParameterList();
//
//			 JCoParameterList exports = function.getExportParameterList();
//			 String tempE_NETPR = exports.getString("E_NETPR");
//			 String tempE_TOTAL = exports.getString("E_TOTAL");
//			 E_NETPR.append(tempE_NETPR);
//			 E_TOTAL.append(tempE_TOTAL);
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return desc;
//	}
//
//  //根据客户编码查询BU
//    public String  Z_JAVA_GET_CUSTOMER(String CUSTOMER){
//    	String ZZBU= "";
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_JAVA_GET_CUSTOMER");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_KUNNR",CUSTOMER);
//
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			 JCoParameterList exportsE_CUSTOMER = function.getExportParameterList();
//
//			 JCoStructure js =  exportsE_CUSTOMER.getStructure("E_CUSTOMER");
//
//			 ZZBU =  js.getString("ZZBU");
//
//
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return ZZBU;
//	}
////获取新建的PO POITEM
//    public List<PonewETATemp> Z_AEGIS_GET_PO_NEWLY(String TIME1,String TIME2){
//		List<PonewETATemp> list = new ArrayList();
//		try {
//			SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_PO_NEWLY");// 接口方法名字
//			 //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //销售文件
//	         input.setValue("I_AEDATFR",TIME1);//其实日期
//	         input.setValue("I_AEDATTO",TIME2);//结束日期
//			if (function == null)
//				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//			try {
//				function.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			JCoParameterList tables=function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ET_PONEWLY");
//	     	for (int i = 0; i < rs.getNumRows(); i++) {
//	     		PonewETATemp lt =new PonewETATemp();
//                	lt.setPo(rs.getString("EBELN"));
//                	lt.setPoItem(rs.getString("EBELP"));
//
//					list.add(lt);
//	     	    rs.nextRow();
//	     	}
//		}catch(Exception e){
//			  e.printStackTrace();
//		}
//		return list;
//	}
//
//  //根据AUFNR 工单 MATNR 查询查询工单状态 并返回 领料数据 SMT261
//    // gamng(N,13,3)	工单排产数量
//    // bdmng(N,13,3)	物料需求数量
//    // enmng(N,13,3)	物料领料数量
//    // status(C,5)	工单状态:CNF为最后确认
//
//    public String Z_AEGIS_GET_WO_STATUS(String AUFNR,String MATNR,StringBuffer GAMNG,StringBuffer BDMNG,StringBuffer ENMNG)
//    {
//
//    	String  status ="";
//    	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//    	try
//    	{
//      		 //获取连接池
//	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//	    	 //获取功能函数
//	         JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_WO_STATUS");
//	         if(function == null)
//	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//	         //给功能函数输入参数
//	         JCoParameterList input = function.getImportParameterList();
//	         //yyyywk
//	         input.setValue("AUFNR", AUFNR);//po
//	         input.setValue("MATNR", MATNR);//poitem
//	         try
//	         {
//	            function.execute(destination); //函数执行
//	     	    JCoParameterList exports = function.getExportParameterList();
//	     	    status =  exports.getString("STATUS");
//	     	    GAMNG.append(exports.getString("GAMNG"));
//	     	    BDMNG.append(exports.getString("BDMNG")); //物料需求数量
//	     	    ENMNG.append(exports.getString("ENMNG")); //物料领料数量
//
//	         }
//	         catch(AbapException e)
//	         {
//	             //System.out.println(e.toString());
//	         }
//	    }catch(Exception ex)
//	    {
//	    	ex.printStackTrace();
//	    	return ex.toString();
//	    }
//	    return  status;
//    }
//
//
//    //查库存
//    public String[] Z_AEGIS_GET_Stock_Num(String plant ,String pn, String stock){
//    	String[] rtArray= new String[8];
//		try {
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_GOODSSTOCK2");// 接口方法名字
//			if (function == null) {
//				throw new RuntimeException("Z_AEGIS_GET_GOODSSTOCK2 not found in SAP.");
//			}
//			//给功能函数输入参数
//	        JCoParameterList input = function.getImportParameterList();
//	        input.setValue("I_WERKS",plant);
//	        input.setValue("I_MATNR",pn);
//	        input.setValue("I_LGORT",stock);
//			try {
//				function.execute(destination);
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//			//JCoParameterList exports = function.getExportParameterList();
//			JCoTable rs1 = function.getTableParameterList().getTable("RETURN");
//			System.out.println("Z_AEGIS_GET_Stock_Num:"+rs1.getString("TYPE"));
//			//if("S".equals(rs1.getString("TYPE"))){
//				JCoTable rs2 = function.getTableParameterList().getTable("IT_MARD");
//				if(rs2.getNumRows()>0) {
//					rtArray[0] = rs2.getString("LABST"); //未限制库存
//					rtArray[3] = rs2.getString("UMLME"); //转移中的库存
//					rtArray[4] = rs2.getString("INSME"); //品检中库存
//					rtArray[5] = rs2.getString("EINME"); //限制使用库存
//					rtArray[6] = rs2.getString("SPEME"); //冻结库存
//					rtArray[7] = rs2.getString("RETME"); //退货
//				}
//				JCoTable rs3 = function.getTableParameterList().getTable("IT_MKOL");
//				if(rs3.getNumRows()>0) {
//					rtArray[1] = rs3.getString("SOBKZ"); //特殊库存
//					rtArray[2] = rs3.getString("LIFNR"); //特殊库存号码
//				}
//
//			//}
//		}catch(Exception e){
//			 e.printStackTrace();
//		}
//		return rtArray;
//	}
//
//
//    //根据工单、物料查询领料数量
//       public String Z_AEGIS_GET_ENMNG(String so ,String pn){
//       	String qty= "";
//   		try {
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_WO_STATUS");// 接口方法名字
//   			if (function == null) {
//   				throw new RuntimeException("Z_AEGIS_GET_WO_STATUS not found in SAP.");
//   			}
//   			//给功能函数输入参数
//   	        JCoParameterList input = function.getImportParameterList();
//   	        input.setValue("AUFNR",so);
//   	        input.setValue("MATNR",pn);
//   			try {
//   				function.execute(destination);
//   			} catch (AbapException e) {
//   				e.printStackTrace();
//   				System.out.println(e.toString());
//   			}
//   			JCoParameterList exports = function.getExportParameterList();
//   			qty = exports.getString("ENMNG");
//   		}catch(Exception e){
//   			 e.printStackTrace();
//   		}
//   		return qty;
//   	}
//
//     //找出日期范围内已完成321异动的工单
//   	public List<SAP_321> getFinish321SoList(String startDate, String endDate) throws Exception {
//   		List<SAP_321> list = new ArrayList<SAP_321>();
//   		try {
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GET_WO_LIST");//接口方法名字
//   			if (function == null)
//   				throw new RuntimeException("Z_AEGIS_GET_WO_LIST not found in SAP.");
//   			//给功能函数输入参数
//   			JCoParameterList input = function.getImportParameterList();
//   			//System.out.println(startDate);
//   			//System.out.println(startDate.substring(8, 10)+"."+startDate.substring(5, 7)+"."+startDate.substring(0, 4));
//   			//input.setValue("DATUV",startDate.substring(8, 10)+"."+startDate.substring(5, 7)+"."+startDate.substring(0, 4));
//   			//input.setValue("DATUB",endDate.substring(8, 10)+"."+endDate.substring(5, 7)+"."+endDate.substring(0, 4));
//   			input.setValue("DATUV",startDate);
//   			input.setValue("DATUB",endDate);
//   			//函数执行
//   			try {
//   				function.execute(destination);
//   			} catch (AbapException e) {
//   				e.printStackTrace();
//   			}
//   			//JCoTable retn = function.getTableParameterList().getTable("T_RETURN");
//   			//if(retn != null) {
//   				//System.out.println(retn.getString("TYPE"));
//   				//
//   				JCoTable rs = function.getTableParameterList().getTable("WORKORDER");
//   				System.out.println(rs.getNumRows());
//   				for (int i = 0; i < rs.getNumRows(); i++) {
//   					SAP_321 item = new SAP_321();
//   					item.setPn(rs.getString("MATNR"));
//   					item.setSo(rs.getString("AUFNR"));
//   					item.setDate321(rs.getString("BUDAT"));
//   					item.setFinishdate(rs.getString("GLTRP"));
//   					list.add(item);
//   					rs.nextRow();
//   				}
//
//   			//}
//   			//String remark = function.getExportParameterList().getString("REMARK");
//   		} catch (Exception e) {
//   			e.printStackTrace();
//   		}
//   		return list;
//   	}
//
//
//   //SAP903
//       public String[] Aegis903(UIDSplit usitem, StringBuffer sb) {
//       	String []StrArray = new String[2];
//       	StrArray[0] = "";
//       	StrArray[1] = "";
//       	String GRN = "";
//   		try {
//   			//获取连接池
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			//获取功能函数
//   			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_OTHERGR");
//   			if (function == null) {
//   				throw new RuntimeException("Z_AEGIS_GOODSMVT_OTHERGR not found in SAP.");
//   			}
//   			//给功能函数输入参数
//   			JCoParameterList input = function.getImportParameterList();
//   			//input.setValue("I_XBLNR", usitem.getPnmsg());//參考文件號碼，非必须
//   			//input.setValue("I_BKTXT", usitem.getHeadermsg());//文件表頭內文，非必须
//   			input.setValue("I_BWART", "903");//异动类型
//
//   			//抬头项
//   			JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISTR");
//   			//for(int i=0; i<list.size(); i++) {
//   				ZDC.appendRow();
//   				ZDC.setValue("PLANT", usitem.getPlant());//plant
//   				ZDC.setValue("MATERIAL", usitem.getPartNumber());//
//   				ZDC.setValue("ENTRY_QNT", usitem.getQuantity());//
//   				if(usitem.getStockLocation().length()>8) {
//   					System.out.println(usitem.getStockLocation().substring(5, 9));
//   					ZDC.setValue("STGE_LOC", usitem.getStockLocation().substring(5, 9));//
//   				}
//   				ZDC.setValue("MOVE_TYPE", "903");//
//   				ZDC.setValue("MOVE_REAS", "0004"); //
//   			//}
//
//   			try {
//   				function.execute(destination); //函数执行
//
//
//   				JCoParameterList exports = function.getExportParameterList();
//   				GRN = exports.getString("E_MBLNR");
//   				if(GRN == null) {
//   					GRN = "";
//   				}
//   				if(GRN.equals("")) {
//   					JCoTable rs = function.getTableParameterList().getTable("T_RETURN");
//   					for (int j = 0; j < rs.getNumRows(); j++) {
//   						System.out.println("111111:"+rs.getString("MESSAGE"));
//   						sb.append(rs.getString("MESSAGE"));
//   						rs.nextRow();
//   					}
//   				}
//   				StrArray[0] = GRN;
//   				StrArray[1] = exports.getString("E_MJAHR");
//   			} catch (AbapException e) {
//   				System.out.println("222222:"+e.toString());
//   				return StrArray;
//   			}
//   		} catch (Exception ex) {
//   			ex.printStackTrace();
//   			System.out.println("333333:"+ex.toString());
//   			return StrArray;
//   		}
//   		return StrArray;
//   	}
//
//
//       //下载过账信息 101  MVT_type
//
//       public List<BitchPrint> BAPI_GOODSMVT_GETITEMS(String plant,String pn,String startDate, String endDate,StringBuffer sb)
//       {
//
//    	List<BitchPrint> list = new ArrayList();
//       	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//       	try
//       	{
//         		 //获取连接池
//   	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   	    	 //获取功能函数
//   	         JCoFunction function = destination.getRepository().getFunction("BAPI_GOODSMVT_GETITEMS");
//   	         if(function == null)
//   	             throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//   	         //给功能函数输入参数
//
//   	         //物料
//   	         JCoTable ZDC = function.getTableParameterList().getTable("MATERIAL_RA");
//   	         ZDC.appendRow();
//   	         ZDC.setValue("SIGN","I");
//   	         ZDC.setValue("OPTION","CP");
//   	         ZDC.setValue("LOW",pn+"*");
//
//   	         //工厂
//   	         //JCoTable ZDC2 = function.getTableParameterList().getTable("PLANT_RA");
//   	         //ZDC2.appendRow();
//   	         //ZDC2.setValue("SIGN","I");
//   	         //ZDC2.setValue("OPTION","EQ");
//   	         //ZDC2.setValue("LOW",plant);
//
//   	         //移动类型
//   	         JCoTable ZDC3 = function.getTableParameterList().getTable("MOVE_TYPE_RA");
//   	         ZDC3.appendRow();
//   	         ZDC3.setValue("SIGN","I");
//   	         ZDC3.setValue("OPTION","EQ");
//   	         ZDC3.setValue("LOW","101");
//
//   	      //时间
//   	         JCoTable ZDC4 = function.getTableParameterList().getTable("PSTNG_DATE_RA");
//   	      ZDC4.appendRow();
//   	   ZDC4.setValue("SIGN","I");
//   	ZDC4.setValue("OPTION","BT");
//   	ZDC4.setValue("LOW",startDate);
//   	ZDC4.setValue("HIGH",endDate);
//
//   	         try
//   	         {
//   	            function.execute(destination); //函数执行
//   		     	JCoTable rs = function.getTableParameterList().getTable("GOODSMVT_ITEMS");
//
//
//   		     	JCoTable rs2 = function.getTableParameterList().getTable("GOODSMVT_HEADER");
//   		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//
//   		     	        BitchPrint b = new BitchPrint();
//   		     	        b.setPlant(rs.getString("PLANT"));
//   		     	        b.setPN(rs.getString("MATERIAL"));
//   		     	    b.setSloc(rs.getString("STGE_LOC"));
//   		     	    b.setMvt("101");
//   		     	    b.setGRN(rs.getString("MAT_DOC"));
//   		     	    b.setGRNDate(rs2.getString("PSTNG_DATE"));
//   		     	    b.setBATCH(rs.getString("BATCH"));
//
//   		     	    b.setQTY(rs.getDouble("ENTRY_QNT"));
//   		     	    b.setUNIT(rs.getString("PO_PR_QNT"));
//   		     	    b.setOrders(rs.getString("ORDERID"));
//   		     	    b.setState("未打印");
//
//   		     	list.add(b);
//   		     	        //System.out.println(rs.getString("MAT_DOC"));
//   		     	        rs.nextRow();
//   		     	        rs2.nextRow();
//   		     	    }
//
//   	         }
//   	         catch(AbapException e)
//   	         {
//   	             System.out.println(e.toString());
//   	         }
//   	    }catch(Exception ex)
//   	    {
//   	    	ex.printStackTrace();
//
//   	    }
//   	    return list;
//       }
//
//       //获取邮件
//       public String Z_SPT_GET_VENDOR_EMAIL(String vendorCode){
//    	   String email="";
//
//       	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//       	try
//       	{
//         	//获取连接池
//   	    	 JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   	    	 //获取功能函数
//   	         JCoFunction function = destination.getRepository().getFunction("Z_SPT_GET_VENDOR_EMAIL");
//   	         if(function == null){
//   	             throw new RuntimeException("Z_SPT_GET_VENDOR_email not found in SAP");
//   	         }
//   	         //传参
//   	         JCoParameterList input = function.getImportParameterList();
//   	         input.setValue("I_LIFNR", vendorCode);
//   	         try
//   	         {
//   	            function.execute(destination); //函数执行
//   		     	JCoTable rs = function.getTableParameterList().getTable("ET_ADR6");
//
//   		     	    for (int j = 0; j < rs.getNumRows(); j++) {
//   		     	    	email=email+rs.getString("SMTP_ADDR")+";";
//
//   		     	        rs.nextRow();
//   		     	    }
//   	         }
//   	         catch(AbapException e)
//   	         {
//   	             System.out.println(e.toString());
//   	         }
//   	    }catch(Exception ex)
//   	    {
//   	    	ex.printStackTrace();
//   	    }
//   	    System.out.println(email);
//   	    return email;
//       }
//
//       public String Z_AEGIS_GOODSMVT_DELIVER261_OverQty(String UNLOAD_PT,
//   			String PartNumber, String Batch, String PLENT, String WorkStation,
//   			String ORDERID, String QTY, StringBuffer sb, StringBuffer E_MJAHR) {
//       	String GRN = "";
//   		String E_MJAHR1 = "";
//   		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//   		try {
//   			// 获取连接池
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			// 获取功能函数
//   			JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSMVT_DELIVER_VPS");
//   			if (function == null)
//   				throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//   			// 给功能函数输入参数
//   			JCoParameterList input = function.getImportParameterList();
//
//   			// 销售文件
//   			input.setValue("I_BWART", "261");// 异动类型
//   			input.setValue("I_OVER_FLAG", "X");// 无限超发
//   			// 抬头项
//   			JCoTable ZDC = function.getTableParameterList().getTable("IT_AEGISDELIVER");
//   			ZDC.appendRow();
//   			System.out.println(QTY + "," + PartNumber + "," + PLENT + "," + ORDERID + "," + Batch + "," + WorkStation+","+UNLOAD_PT);
//   			ZDC.setValue("ENTRY_QNT", QTY);// --------------数量
//   			ZDC.setValue("MATERIAL", PartNumber);// --------物料
//   			ZDC.setValue("PLANT", PLENT);// ----------------工厂
//   			ZDC.setValue("ORDERID", ORDERID);// ------------工单 261专用
//   			ZDC.setValue("BATCH", Batch);// ----------------批次
//   			ZDC.setValue("STGE_LOC", WorkStation);// -------仓位
//   			ZDC.setValue("UNLOAD_PT", UNLOAD_PT);// --------备注 HTP261 SMT261
//
//   			try {
//   				function.execute(destination); // 函数执行
//   				JCoParameterList exports = function.getExportParameterList();
//   				GRN = exports.getString("E_MBLNR");
//
//   				E_MJAHR1 = exports.getString("E_MJAHR");
//   				E_MJAHR.append(E_MJAHR1);
//   				if (GRN == null) {
//   					GRN = "";
//   				}
//   				if (GRN.equals("")) {
//   					JCoTable rs = function.getTableParameterList().getTable(
//   							"T_RETURN");
//   					for (int j = 0; j < rs.getNumRows(); j++) {
//   						sb.append(rs.getString("MESSAGE") + "\r\n");
//   						rs.nextRow();
//   					}
//   				}
//   			} catch (AbapException e) {
//   				System.out.println(e.toString());
//   			}
//   		} catch (Exception ex) {
//   			ex.printStackTrace();
//   			return ex.toString();
//   		}
//   		return GRN;
//   	}
//
//       // 101
//   	public String BAPI_GOODSMVT_CREATE(String mvt_type, String plant,
//   			String so, String pn, String qty, String unit, String location, StringBuffer sb) {
//
//   		String GRN = "";
//   		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//   		String today = sf.format(new Date());
//   		try {
//   			// 获取连接池
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			// 获取功能函数
//   			JCoFunction function = destination.getRepository().getFunction("BAPI_GOODSMVT_CREATE");
//   			if (function == null)
//   				throw new RuntimeException("BAPI_GOODSMVT_CREATE not found in SAP.");
//   			// 给功能函数输入参数
//   			JCoParameterList input = function.getImportParameterList();
//   			//
//   			JCoTable ZDC1 = function.getTableParameterList().getTable("GOODSMVT_HEADER");
//   			ZDC1.appendRow();
//   			ZDC1.setValue("PSTNG_DATE", today);
//   			ZDC1.setValue("DOC_DATE", today);
//   			//
//   			JCoTable ZDC2 = function.getTableParameterList().getTable("GOODSMVT_CODE");
//   			ZDC2.appendRow();
//   			ZDC2.setValue("GM_CODE", "02");
//   			// 抬头项
//   			JCoTable ZDC3 = function.getTableParameterList().getTable("GOODSMVT_ITEM");
//   			ZDC3.appendRow();
//   			ZDC3.setValue("MATERIAL", pn);
//   			ZDC3.setValue("PLANT", plant);
//   			ZDC3.setValue("STGE_LOC", location);
//   			ZDC3.setValue("MOVE_TYPE", mvt_type);
//   			ZDC3.setValue("MVT_IND", "F");
//   			ZDC3.setValue("ENTRY_QNT", qty);
//   			ZDC3.setValue("ENTRY_UOM", unit);
//   			ZDC3.setValue("ORDERID", so);
//
//   			try {
//   				function.execute(destination); // 函数执行
//   				JCoParameterList exports = function.getExportParameterList();
//   				GRN = exports.getString("MATERIALDOCUMENT");
//   				if (GRN == null) {
//   					GRN = "";
//   				}
//   				if (GRN.equals("")) {
//   					JCoTable rs = function.getTableParameterList().getTable("RETURN");
//   					for (int j = 0; j < rs.getNumRows(); j++) {
//   						sb.append(rs.getString("MESSAGE") + "\r\n");
//   						rs.nextRow();
//   					}
//   				}
//   			} catch (AbapException e) {
//   				System.out.println(e.toString());
//   			}
//   		} catch (Exception ex) {
//   			ex.printStackTrace();
//   			return ex.toString();
//   		}
//   		System.out.println("BAPI_GOODSMVT_CREATE:"+GRN);
//   		return GRN;
//
//   	}
//
//
//  //获取固定供应商
//	public String getFixVendorByPNPlent(String pn,String plent)throws Exception{
//
//		String fixvendor = "";
//	    JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//		JCoFunction function2 = destination.getRepository().getFunction("Z_WAP_GET_MATERIAL");// 接口方法名字
//		if (function2 == null)throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
//		JCoParameterList input2 = function2.getImportParameterList();
//	    input2.clear();
//		input2.setValue("I_MATNR", pn);// partNumber
//		input2.setValue("I_WERKS", plent);// plent
//		  try {
//			  function2.execute(destination); //
//			} catch (AbapException e) {
//				e.printStackTrace();
//				System.out.println(e.toString());
//			}
//	        JCoStructure E_EORD = function2.getExportParameterList().getStructure("E_EORD");
//	        fixvendor = E_EORD.getString("LIFNR");
//	        if(fixvendor==null){
//	        	fixvendor = "";
//	        }
//	return fixvendor;
//}
//
//	 // 313315
//   	public String BAPI_GOODSMVT_CREATE313315(String mvt_type, String material,
//   			String plant, String stge_loc, String entry_qnt, String BATCH,
//   			String MOVE_PLANT,String MOVE_STLOC, StringBuffer sb) {
//
//   		String GRN = "";
//   		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//   		String today = sf.format(new Date());
//   		try {
//   			// 获取连接池
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			// 获取功能函数
//   			JCoFunction function = destination.getRepository().getFunction("BAPI_GOODSMVT_CREATE");
//   			if (function == null)
//   				throw new RuntimeException("BAPI_GOODSMVT_CREATE not found in SAP.");
//   			// 给功能函数输入参数
//   			JCoParameterList input = function.getImportParameterList();
//   			//
//
//   		    JCoStructure ZDC1 = input.getStructure("GOODSMVT_HEADER");
//
//   			ZDC1.setValue("PSTNG_DATE", today);
//   			ZDC1.setValue("DOC_DATE", today);
//   			//
//   			JCoStructure ZDC2 = input.getStructure("GOODSMVT_CODE");
//   			ZDC2.setValue("GM_CODE", "04");
//   			// 抬头项
//   			JCoTable ZDC3 = function.getTableParameterList().getTable("GOODSMVT_ITEM");
//   			ZDC3.appendRow();
//   			ZDC3.setValue("MATERIAL", material);
//   			ZDC3.setValue("PLANT", plant);
//   			ZDC3.setValue("STGE_LOC", stge_loc);
//   			ZDC3.setValue("MOVE_TYPE", mvt_type);
//   			ZDC3.setValue("ENTRY_QNT", entry_qnt);
//   			ZDC3.setValue("BATCH", BATCH);
//
//   			ZDC3.setValue("MOVE_PLANT", MOVE_PLANT);
//   			ZDC3.setValue("MOVE_STLOC", MOVE_STLOC);
//
//   			try {
//
//   				JCoContext.begin(destination);
//   				function.execute(destination); // 函数执行
//   				JCoParameterList exports = function.getExportParameterList();
//   				GRN = exports.getString("MATERIALDOCUMENT");
//   				if (GRN == null) {
//   					GRN = "";
//   				}
//   				if (GRN.equals("")) {
//   					JCoTable rs = function.getTableParameterList().getTable("RETURN");
//   					for (int j = 0; j < rs.getNumRows(); j++) {
//   						sb.append(rs.getString("MESSAGE") + "\r\n");
//   						System.out.println(rs.getString("MESSAGE"));
//   						rs.nextRow();
//   					}
//   					function = destination.getRepository().getFunction("BAPI_TRANSACTION_ROLLBACK");
// 		     	    function.execute(destination);
// 		     	    System.out.println("ROLLBACK:"+sb.toString());
//   				}else{
//   					JCoFunction function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT");
//  	     		    JCoParameterList input2 = function2.getImportParameterList();
//  	     		    input2.setValue("WAIT", "X");
//  	     		    function2.execute(destination);
//
//  		            System.out.println("COMMIT:"+GRN);
//   				}
//   				JCoContext.end(destination);
//   			} catch (AbapException e) {
//   				System.out.println(e.toString());
//   			}
//   		} catch (Exception ex) {
//   			ex.printStackTrace();
//   			return ex.toString();
//   		}
//   		System.out.println("BAPI_GOODSMVT_CREATE:"+GRN);
//   		return GRN;
//
//   	}
//
//
//	public String Z_VN_CUSTOM_GET_QTY_321(String GRN, StringBuffer sb) {
//
//   		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//   		String today = sf.format(new Date());
//   		try {
//   			// 获取连接池
//   			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//   			// 获取功能函数
//   			JCoFunction function = destination.getRepository().getFunction("Z_VN_CUSTOM_GET_QTY_321");
//   			if (function == null)
//   				throw new RuntimeException("BAPI_GOODSMVT_CREATE not found in SAP.");
//   			// 给功能函数输入参数
//   			JCoParameterList input = function.getImportParameterList();
//   			input.setValue("I_MBLNR",GRN);
//   			try {
//   				function.execute(destination);
//   					JCoTable rs = function.getTableParameterList().getTable("ET_MSEG");
//   					if(rs.getNumRows() > 0){
//   						sb.append("已过321");
//   					}else{
//   						sb.append("未过321");
//   					}
//
//   			} catch (AbapException e) {
//   				System.out.println(e.toString());
//   			}
//   		} catch (Exception ex) {
//   			ex.printStackTrace();
//   			return ex.toString();
//   		}
//   		return sb.toString();
//   	}
//	/**
//	 *
//	* @Description: TODO
//	* @param @param prueflos 检验批次
//	* @param @param bwart	 sap类型
//	* @param @param vmenge01 数量
//	* @param @return
//	* @return String[]
//	 */
//
//	public static  String[] Z_AEGIS_GOODSQM (String I_PRUEFLOS,String I_BWART,String I_VMENGE01){
//		  String [] str = new String[3];
//		  JCoDestination destination;
//		  try {
//		   destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//		   JCoFunction function = destination.getRepository().getFunction("Z_AEGIS_GOODSQM");
//		      if (function == null)
//		       throw new RuntimeException("Z_AEGIS_GOODSQM not found in SAP.");
//		      // 给功能函数输入参数
//		      JCoParameterList input = function.getImportParameterList();
//		      input.setValue("I_PRUEFLOS", I_PRUEFLOS);
//		      input.setValue("I_BWART", I_BWART);
//		      if(I_BWART.contains("321")){
//		       input.setValue("I_VMENGE01",I_VMENGE01);
//		       input.setValue("I_VCODE","0001");
//		      }else if(I_BWART.contains("123")){
//		       input.setValue("I_VMENGE06",I_VMENGE01);
//		      }else if(I_BWART.contains("331")){
//		       input.setValue("I_VMENGE03",I_VMENGE01);
//		      }else if(I_BWART.contains("553")){
//		       input.setValue("I_VMENGE02",I_VMENGE01);
//		      }else if(I_BWART.contains("350")){
//		       input.setValue("I_VMENGE04",I_VMENGE01);
//		      }else if(I_BWART.contains("122")){
//		       input.setValue("I_VMENGE07",I_VMENGE01);
//		       input.setValue("I_VCODE","0005");
//		      }else{
//		       input.setValue("I_VMENGE01",I_VMENGE01);
//		      }
//
//		      function.execute(destination);
//		      // 直接返回值取值
//		        JCoParameterList exports = function.getExportParameterList();
//		        str[0] = exports.getString("REMARK");
//		        str[1] = exports.getString("E_MBLNR");
//		        str[2] = exports.getString("E_MJAHR");
//		        return str;
//		  } catch (JCoException e) {
//		   // TODO Auto-generated catch block
//		   e.printStackTrace();
//		   return str;
//		  }
//		 }
//
//
//
//
//	//下载YFIR11_HANA接口，刷新未付款对账单
//	public List<YFIR112018> getYFIR11HanaList(String vendorCode) {
//		List<YFIR112018> list = new ArrayList();
//		NumberFormat nf = NumberFormat.getNumberInstance();
//		DecimalFormat decDf = new DecimalFormat("#.00");
//		try {
//			//
//			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//			Calendar c = Calendar.getInstance();
//			String endDate = df.format(new Date());
//			c.setTime(df.parse(endDate));
//			c.add(Calendar.MONTH, -24);//计算前6个月日期
//			String startDate = df.format(c.getTime());
//			//System.out.println(startDate+","+endDate);
//			String firstDay = endDate.substring(0,4)+"0101";
//			/*if(firstDay.compareTo(startDate) > 0) {
//				startDate = firstDay;
//			}*/
//			//System.out.println(startDate+","+endDate);
//			//
//			JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//			JCoFunction function = destination.getRepository().getFunction("Z_INVOICE_DATA_GET");//接口方法名字
//			if (function == null)
//				throw new RuntimeException("Z_INVOICE_DATA_GET not found in SAP.");
//			//给功能函数输入参数
//			JCoParameterList input = function.getImportParameterList();
//			//input.setValue("I_LIFNR","0090AAS429");
//			//input.setValue("I_WERKS",werks);
//			input.setValue("I_LIFNR",vendorCode);
///*			JCoStructure I_BUDAT = input.getStructure("I_BUDAT");
//			I_BUDAT.setValue("LOW",startDate);//开始日期
//			I_BUDAT.setValue("HIGH",endDate);//结束日期
//*/
//			//函数执行
//			try {
//				function.execute(destination);
//			} catch (AbapException e) {
//				e.printStackTrace();
//			}
//			//JCoParameterList tables = function.getTableParameterList();
//			JCoTable rs = function.getTableParameterList().getTable("ET_GRIR");
//			System.out.println("getYFIR11HanaList:"+rs.getNumRows());
//			String plant="";
//			String ccy="";
//			String pofile = "";
//			for (int i = 0; i < rs.getNumRows(); i++) {
//				plant =rs.getString("WERKS");
//				ccy = rs.getString("WAERS");
//				pofile = rs.getString("EBELN");
//				//if(!"9500".equals(plant)) {
//					if(!pofile.startsWith("55") && !pofile.startsWith("70") && !pofile.startsWith("56")) {
//						YFIR112018 yf = new YFIR112018();
//						yf.setPlant(rs.getString("WERKS"));
//						yf.setVendorcode(rs.getString("LIFNR"));
//						yf.setVendorname(rs.getString("NAME1")+" "+rs.getString("NAME2"));
//						yf.setPofile(rs.getString("EBELN"));
//						yf.setItem(rs.getString("EBELP"));
//						yf.setPn(rs.getString("MATNR"));
//						yf.setStatus("");
//						yf.setDn(rs.getString("XBLNR"));
//						yf.setStoreno(rs.getString("LFBNR"));
//						yf.setDnid("");
//						yf.setPostingdate(rs.getString("BUDAT"));
//						yf.setUnit(rs.getString("MEINS"));
//						yf.setUnit2(rs.getString("BPRME"));
//						yf.setQty(rs.getString("MENGEB"));
//						yf.setCcy(rs.getString("WAERS"));
//						double unitPrice = Double.parseDouble(rs.getString("WRBTRP"))/Double.parseDouble(rs.getString("MENGE"));
//						BigDecimal up = new BigDecimal(unitPrice);
//						//System.out.println(up.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
//						//System.out.println(up.setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()); //去除多余的0
//						yf.setUnitprice(up.setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()); //不要科学计数法显示
//						//yf.setTotalprice(rs.getString("WRBTRA"));
//						double totalPrice = ( Double.parseDouble(rs.getString("WRBTRP"))/Double.parseDouble(rs.getString("MENGE")) ) * Double.parseDouble(rs.getString("MENGEB"));
//						BigDecimal bd = new BigDecimal(totalPrice);
//						String tempTotalPrice = nf.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//						//System.out.println("before:"+tempTotalPrice);
//						if(tempTotalPrice.indexOf(".") == -1) {
//							tempTotalPrice = tempTotalPrice+".00";
//						}else {
//							String[] last = tempTotalPrice.split("\\.");
//							if(last[1].length() == 1) {
//								tempTotalPrice = tempTotalPrice+"0";
//							}
//						}
//						//System.out.println("after:"+tempTotalPrice);
//						yf.setTotalprice(tempTotalPrice);
//						//
//						yf.setPaycondition(rs.getString("ZTERM"));
//						if(rs.getString("MWSKZ") != null && !"".equals(rs.getString("MWSKZ"))){
//							yf.setTaxno(rs.getString("MWSKZ"));
//						}else{
//							if(("3000".equals(plant) || "5000".equals(plant) || "7000".equals(plant)) && "RMB".equals(ccy) ){
//								yf.setTaxno("J2");
//							}else{
//								yf.setTaxno("J0");
//							}
//						}
//						//
//						yf.setLfbja(rs.getString("LFBJA"));
//						yf.setLfpos(rs.getString("LFPOS"));
//						//
//						yf.setAddr(rs.getString("STREET")+","+rs.getString("CITY1"));
//						yf.setPhone(rs.getString("TEL_NUMBER"));
//						yf.setFax(rs.getString("FAX_NUMBER"));
//						yf.setXblnr(rs.getString("XBLNR"));
//						list.add(yf);
//					}else {
//						if(("3000".equals(plant) || "5000".equals(plant) || "7000".equals(plant)) && "RMB".equals(ccy) ) {
//							YFIR112018 yf = new YFIR112018();
//							yf.setPlant(rs.getString("WERKS"));
//							yf.setVendorcode(rs.getString("LIFNR"));
//							yf.setVendorname(rs.getString("NAME1")+" "+rs.getString("NAME2"));
//							yf.setPofile(rs.getString("EBELN"));
//							yf.setItem(rs.getString("EBELP"));
//							yf.setPn(rs.getString("MATNR"));
//							yf.setStatus("");
//							yf.setDn(rs.getString("XBLNR"));
//							yf.setStoreno(rs.getString("LFBNR"));
//							yf.setDnid("");
//							yf.setPostingdate(rs.getString("BUDAT"));
//							yf.setUnit(rs.getString("MEINS"));
//							yf.setUnit2(rs.getString("BPRME"));
//							yf.setQty(rs.getString("MENGEB"));
//							yf.setCcy(rs.getString("WAERS"));
//							double unitPrice = Double.parseDouble(rs.getString("WRBTRP"))/Double.parseDouble(rs.getString("MENGE"));
//							BigDecimal up = new BigDecimal(unitPrice);
//							//System.out.println(up.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
//							//System.out.println(up.setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()); //去除多余的0
//							yf.setUnitprice(up.setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString()); //不要科学计数法显示
//							//yf.setTotalprice(rs.getString("WRBTRA"));
//							double totalPrice = ( Double.parseDouble(rs.getString("WRBTRP"))/Double.parseDouble(rs.getString("MENGE")) ) * Double.parseDouble(rs.getString("MENGEB"));
//							BigDecimal bd = new BigDecimal(totalPrice);
//							String tempTotalPrice = nf.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//							//System.out.println("before:"+tempTotalPrice);
//							if(tempTotalPrice.indexOf(".") == -1) {
//								tempTotalPrice = tempTotalPrice+".00";
//							}else {
//								String[] last = tempTotalPrice.split("\\.");
//								if(last[1].length() == 1) {
//									tempTotalPrice = tempTotalPrice+"0";
//								}
//							}
//							//System.out.println("after:"+tempTotalPrice);
//							yf.setTotalprice(tempTotalPrice);
//							//
//							yf.setPaycondition(rs.getString("ZTERM"));
//							if(rs.getString("MWSKZ") != null && !"".equals(rs.getString("MWSKZ"))){
//								yf.setTaxno(rs.getString("MWSKZ"));
//							}else{
//								yf.setTaxno("J2");
//							}
//							//
//							yf.setLfbja(rs.getString("LFBJA"));
//							yf.setLfpos(rs.getString("LFPOS"));
//							//
//							yf.setAddr(rs.getString("STREET")+","+rs.getString("CITY1"));
//							yf.setPhone(rs.getString("TEL_NUMBER"));
//							yf.setFax(rs.getString("FAX_NUMBER"));
//							yf.setXblnr(rs.getString("XBLNR"));
//							list.add(yf);
//						}
//					}
//				//}
//				rs.nextRow();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
//
//	//true 正式 false 测试
//		public static  String[] ZVENDOR_MASTERDATA (NewSupplier uewSupplier,boolean flag){
//			JCoDestination destination;
//			String [] str = new String[5];
//			try {
//
//				destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//				JCoFunction function = destination.getRepository().getFunction("ZVENDOR_MASTERDATA06");
//	   			if (function == null)
//	   				throw new RuntimeException("ZVENDOR_MASTERDATA not found in SAP.");
//	   			// 给功能函数输入参数
//	   			JCoParameterList input = function.getImportParameterList();
//	   			input.setValue("LIFNR_001",uewSupplier.getLIFNR_001()); //0090ddd444
//	   			input.setValue("BUKRS_002", uewSupplier.getBUKRS_002()); //HT02
//	   			input.setValue("EKORG_003", uewSupplier.getEKORG_003()); //1000
//	   			input.setValue("KTOKK_004", uewSupplier.getKTOKK_004()); //HT01
//	   			input.setValue("USE_ZAV_005",uewSupplier.getUSE_ZAV_005()); //X
//	   			input.setValue("TITLE_MEDI_006",uewSupplier.getTITLE_MEDI_006()); //公司
//	   			//根据营业执照语言选择
//	   			String RegisteredAddress = "";
//	   			if("CN".equals(uewSupplier.getBusinessLicenseLanguage())){
//	   				//供应商名称
//	   				input.setValue("NAME1_007",uewSupplier.getCompanyNameZ());
//	   				RegisteredAddress = uewSupplier.getRegisteredAddressZ();
//	   			}else if("VN".equals(uewSupplier.getBusinessLicenseLanguage())){
//	   				input.setValue("NAME1_007",uewSupplier.getCompanyNameV());
//	   				RegisteredAddress = uewSupplier.getRegisteredAddressV();
//	   			}else{
//	   				input.setValue("NAME1_007",uewSupplier.getCompanyNameE());//公司名称2
//	   				RegisteredAddress = uewSupplier.getRegisteredAddressE();
//	   			}
//	   			//注册地址
//	   			if(RegisteredAddress.length()>25&&RegisteredAddress.length()<50){
//	   				input.setValue("STREET_011", RegisteredAddress.substring(0, 25));
//	   				input.setValue("STR_SUPPL1_009", RegisteredAddress.substring(25, RegisteredAddress.length()-1));
//	   			}else if(RegisteredAddress.length()>50&&RegisteredAddress.length()<75){
//	   				input.setValue("STREET_011", RegisteredAddress.substring(0, 25));
//	   				input.setValue("STR_SUPPL1_009", RegisteredAddress.substring(25, 50));
//	   				input.setValue("STR_SUPPL2_010", RegisteredAddress.substring(50,RegisteredAddress.length()-1));
//	   			}else if(RegisteredAddress.length()>75&&RegisteredAddress.length()<95){
//	   				input.setValue("STREET_011", RegisteredAddress.substring(0, 25));
//	   				input.setValue("STR_SUPPL1_009", RegisteredAddress.substring(25, 50));
//	   				input.setValue("STR_SUPPL2_010", RegisteredAddress.substring(50,75));
//	   				input.setValue("STR_SUPPL3_013", RegisteredAddress.substring(75,RegisteredAddress.length()-1));
//	   			}else if(RegisteredAddress.length()>95&&RegisteredAddress.length()<115){
//	   				input.setValue("STREET_011", RegisteredAddress.substring(0, 25));
//	   				input.setValue("STR_SUPPL1_009", RegisteredAddress.substring(25, 50));
//	   				input.setValue("STR_SUPPL2_010", RegisteredAddress.substring(50,75));
//	   				input.setValue("STR_SUPPL3_013", RegisteredAddress.substring(75,95));
//	   				input.setValue("LOCATION_014", RegisteredAddress.substring(95,RegisteredAddress.length()-1));
//	   			}else{
//	   				input.setValue("STREET_011", RegisteredAddress);//注册地址2
//	   			}
//	   			//国家邮编
//	   			input.setValue("POST_CODE1_015",uewSupplier.getPOST_CODE1_015());
//	   			//国家代码
//	   			input.setValue("COUNTRY_016",uewSupplier.getCOUNTRY_011()); //CN
//	   			//通讯语言
//	   			input.setValue("LANGU_017",uewSupplier.getLANGU_012()); //ZF
//	   			//电话号码
//	   			input.setValue("TEL_NUMBER_018",uewSupplier.getOfficePhone()); //ZF
//	   			//传真
//	   			input.setValue("FAX_NUMBER_019",uewSupplier.getFaxNo()); //ZF
//
//	   			//备注
//	   			if(uewSupplier.getTermsDelivery().contains("A1")){
//	   				input.setValue("REMARK_020","HK");
//	   			}else if(uewSupplier.getTermsDelivery().contains("1B")){
//	   				input.setValue("REMARK_020","DYB");
//	   			}else if(uewSupplier.getTermsDelivery().contains("2B")){
//
//	   				if(uewSupplier.getDeliveryPoints().contains("外仓")){
//	   					input.setValue("REMARK_020","SE(I)");
//	   				}else{
//	   					input.setValue("REMARK_020","SE(E)");
//	   				}
//	   			}else{
//	   				input.setValue("REMARK_020",uewSupplier.getTermsDelivery());
//	   			}
//
//	   			input.setValue("KONZS_021",uewSupplier.getKONZS_013());//NOT0000000 NOT0000000
//	   			//银行
//					//银行所在国家
//					input.setValue("BANKS_01_022", uewSupplier.getBANKS_01_022());//CN
//					//银行码，银行汇款国际码,swift代码
//					input.setValue("BANKL_01_023", uewSupplier.getSWIFTCode());//839
//					//银行账户，账户号码,银行账户
//					input.setValue("BANKN_01_024", uewSupplier.getAccountNumber());//8695654212354
//					//账户人,收款人,银行户名
//					input.setValue("KOINH_01_025",uewSupplier.getBeneficiaryName());//wh
//
//	   			//统制科目
//	   			input.setValue("AKONT_033",uewSupplier.getAKONT_014());//20000001
//	   			//付款条件(财务)
//	   			input.setValue("ZTERM_034", uewSupplier.getZTERM_037());//LC30
//	   			//付款方式（财务）
//	   			input.setValue("PAYCON_042", uewSupplier.getZWELS_038());//TT
//	   			//订单货币(采购)
//	   			input.setValue("WAERS_036", uewSupplier.getTradeCurrency());//GBP
//	   			//付款条件(采购)
//	   			input.setValue("ZTERM_037", uewSupplier.getZTERM_037());//LC30
//	   			//国贸条件(采购)
//	   			input.setValue("INCO1_038", uewSupplier.getIncoterms());//DDU
//	   			//国贸条件描述(采购)
//	   			input.setValue("INCO2_039", uewSupplier.getINCO2_042());//DDU
//	   			//收货为基础的发票检验(采购)
//	   			input.setValue("WEBRE_040", uewSupplier.getWEBRE_020());//X
//	   			//银行名称
//	   			input.setValue("BANKN_041",uewSupplier.getBankName());
//	   			input.setValue("ZWELS_035","");
//	   			//交货地点
//	   			input.setValue("EXTENSION1_043", uewSupplier.getDeliveryPoints());
//	   			//是否打印标签
//	   			input.setValue("REGIOGROUP_044", uewSupplier.getIsTag());
//	   			//销售人员email
//	   			input.setValue("SMTP_ADDR_045", uewSupplier.getSalesEmail());
//	   			//销售人员名字
//	   			input.setValue("VERKF_046", uewSupplier.getSalesName());
//	   			//销售员电话
//	   			input.setValue("TELF1_047", uewSupplier.getSalesTel());
//	   			//是否测试模式 X为测试 空为正式
//
//	   			if(flag){
//	   				input.setValue("I_TESTFLAG","N");
//	   			}else{
//	   				input.setValue("I_TESTFLAG","X");
//	   			}
//
//	   			function.execute(destination);
//
//	   			JCoParameterList exportParameterList = function.getExportParameterList();
//	   			System.out.println(exportParameterList.getString("SUBRC"));
//
//	   			if(!exportParameterList.getString("SUBRC").equals("0")){
//	   				JCoTable rs = function.getTableParameterList().getTable("MESSTAB");
//	   				System.out.println("MSGID:"+rs.getString("MSGID")+"|"+"MSGV1:"+rs.getString("MSGV1")+"|"+"MSGV2:"+rs.getString("MSGV2")+"|"+"MSGV3:"+rs.getString("MSGV3")+"|"+"FLDNAME:"+rs.getString("FLDNAME"));
//	   				str[0]= exportParameterList.getString("SUBRC");
//	   				str[1]= "接口名:ZVENDOR_MASTERDATA06:MSGID:"+rs.getString("MSGID")+"|"+"MSGV1:"+rs.getString("MSGV1")+" |"+"MSGV2:"+rs.getString("MSGV2")+" |"+"MSGV3:"+rs.getString("MSGV3")+" |"+"FLDNAME:"+rs.getString("FLDNAME");
//	   				return str;
//	   			}
//	   			str[0]= exportParameterList.getString("SUBRC");
//	   			return str;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return str;
//			}
//		}
//
//		//上传有效排期
//				public String updateZTDCPRODSCH(List list) throws Exception {
//					String flag = "";
//					try {
//						JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
//						JCoFunction function = destination.getRepository().getFunction("Z_DC_MAINTAIN_ZTDCPRODSCH");//接口方法名字
//						if (function == null)
//							throw new RuntimeException("Z_DC_MAINTAIN_ZTDCPRODSCH not found in SAP.");
//
//						JCoTable ZDC = function.getTableParameterList().getTable("IT_ZTDCPRODSCH");
//						for(int c = 0;c<list.size();c++){
//							  Map map = (Map)list.get(c);
//							  ZDC.appendRow();
//							  ZDC.setValue("AUFNR",String.valueOf(map.get("so")).trim());//工单
//							  ZDC.setValue("GSTRS",String.valueOf(map.get("day")).trim());//日期
//							  ZDC.setValue("MATNR",String.valueOf(map.get("pn")));//物料
//							  ZDC.setValue("ZZBU",String.valueOf(map.get("bugroup")));//BU
//							  ZDC.setValue("PMCGROUP",String.valueOf(map.get("pmcgroup")));//PMC Group
//							  ZDC.setValue("WERKS",String.valueOf(map.get("plant")));//Plant
//							  ZDC.setValue("DISPO",String.valueOf(map.get("mrpcontroller")));//MRP Controller
//							  ZDC.setValue("PRODSCHE",String.valueOf(map.get("prodscheduler")));//Production Scheduler
//							  ZDC.setValue("ARBPL",String.valueOf(map.get("workcenter")));//Work Center
//							  ZDC.setValue("KUNNR",String.valueOf(map.get("custcode")));//Customer Code 客户号码
//							  ZDC.setValue("ZABLAD",String.valueOf(map.get("prodno")));//序列号
//							  ZDC.setValue("PMCLEADER",String.valueOf(map.get("pmcleader")));//PMC Leader
//							  //ZDC.setValue("PRULINE",String.valueOf(map.get("lno")));//生产线
//							  ZDC.setValue("PGMNG",new BigDecimal(String.valueOf(map.get("oddqty"))));//订单数量
//							  ZDC.setValue("GLTRS",String.valueOf(map.get("basedate")));//基本完成日期 getGltrs  basedate  。
//							  ZDC.setValue("SCHQTY",new BigDecimal(String.valueOf(map.get("upplanqty"))));//排期数量
//						}
//
//
//						//函数执行
//						try {
//							function.execute(destination);
//							JCoParameterList exports = function.getExportParameterList();
//							flag = exports.getString("REMARK");
//						} catch (AbapException e) {
//							e.printStackTrace();
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					return flag;
//				}
//
//	public static void main(String[] args) {
//		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
//		String ymd = sf.format(new Date());
//
//
//		try {
//			Calendar c = Calendar.getInstance();
//			c.setTime(sf.parse("20221201"));
//			String week = getWeek2(c.get(Calendar.DAY_OF_WEEK));
//
//			System.out.println(week);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//	private static String getWeek2(int week){
//		if(Calendar.MONDAY == week){
//		    return "星期一";
//		}
//		if(Calendar.TUESDAY == week){
//		    return "星期二";
//		}
//		if(Calendar.WEDNESDAY == week){
//			return "星期三";
//		}
//		if(Calendar.THURSDAY == week){
//			return "星期四";
//		}
//		if(Calendar.FRIDAY == week){
//			return "星期五";
//		}
//		if(Calendar.SATURDAY == week){
//			return "星期六";
//		}
//		if(Calendar.SUNDAY == week){
//			return "星期日";
//		}
//		return "";
//	}
//
}
