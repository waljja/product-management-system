package com.ktg.mes.fg.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.ktg.common.annotation.DataSource;
import com.ktg.common.constant.UserConstants;
import com.ktg.common.enums.DataSourceType;
import com.ktg.common.utils.StringUtils;
import com.ktg.common.utils.barcode.BarcodeUtil;
import com.ktg.common.utils.file.FileUploadUtils;
import com.ktg.common.utils.file.FileUtils;
import com.ktg.mes.fg.domain.*;
import com.ktg.mes.fg.domain.Dto.FgTosAndTosListDto;
import com.ktg.mes.fg.domain.Dto.ReturnResult;
import com.ktg.mes.fg.domain.Dto.ShipmentPart;
import com.ktg.mes.fg.mapper.FgInventoryMapper;
import com.ktg.mes.fg.mapper.FgTosMapper;
import com.ktg.mes.fg.utils.SAPUtil;
import com.ktg.mes.wm.domain.WmBarcode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import com.ktg.mes.fg.mapper.FgChecklistMapper;
import com.ktg.mes.fg.service.IFgChecklistService;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * 成品送检单Service业务层处理
 * 
 * @author JiangTingming
 * @date 2023-03-18
 */
@Service
public class FgChecklistServiceImpl implements IFgChecklistService
{
    @Autowired
    private FgChecklistMapper fgChecklistMapper;

    @Autowired
    private FgInventoryMapper fgInventoryMapper;

    @Autowired
    private FgTosMapper fgTosMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 查询成品送检单
     * 
     * @param id 成品送检单主键
     * @return 成品送检单
     */
    @Override
    public FgChecklist selectFgChecklistById(Long id)
    {
        return fgChecklistMapper.selectFgChecklistById(id);
    }

    /**
     * 查询成品送检单列表
     * 
     * @param fgChecklist 成品送检单
     * @return 成品送检单
     */
    @Override
    public List<FgChecklist> selectFgChecklistList(FgChecklist fgChecklist)
    {
        return fgChecklistMapper.selectFgChecklistList(fgChecklist);
    }

    @Override
    public int checkInfonChcklist(FgChecklist fgChecklist) {

        return  fgChecklistMapper.checkInfonChcklist(fgChecklist);
    }

    /**
     * 新增成品送检单
     * 
     * @param fgChecklist 成品送检单
     * @return 结果
     */
    @Override
    public int insertFgChecklist(FgChecklist fgChecklist)
    {
        return fgChecklistMapper.insertFgChecklist(fgChecklist);
    }

    /**
     * 修改成品送检单
     * 
     * @param fgChecklist 成品送检单
     * @return 结果
     */
    @Override
    public int updateFgChecklist(FgChecklist fgChecklist)
    {
        return fgChecklistMapper.updateFgChecklist(fgChecklist);
    }

    /**
     * 批量删除成品送检单
     * 
     * @param ids 需要删除的成品送检单主键
     * @return 结果
     */
    @Override
    public int deleteFgChecklistByIds(Long[] ids)
    {
        return fgChecklistMapper.deleteFgChecklistByIds(ids);
    }

    /**
     * 删除成品送检单信息
     * 
     * @param id 成品送检单主键
     * @return 结果
     */
    @Override
    public int deleteFgChecklistById(Long id)
    {
        return fgChecklistMapper.deleteFgChecklistById(id);
    }

    /**
     * 根据条码类型和业务内容ID判断条码是否已存在
     * @param fgChecklist
     * @return
     */
    @Override
    public String checkBarcodeUnique(FgChecklist fgChecklist) {
        /*FgChecklist barcode = fgChecklistMapper.checkBarcodeUnique(fgChecklist);
        Long barcodeId = fgChecklist.getId()==null?-1L:fgChecklist.getId();
        if(StringUtils.isNotNull(barcode) && barcode.getId().longValue() != barcodeId.longValue()){
            return UserConstants.NOT_UNIQUE;
        }*/
        String returnMessage = "";
        FgChecklist barcode = fgChecklistMapper.checkBarcodeUnique(fgChecklist);
        if(barcode.getId().equals("") || barcode.getId() == null){
            returnMessage = "不存在";
        }else {
            returnMessage = "存在";
        }

        return returnMessage;
    }

    /**
     * 根据条码记录生成实际的条码，返回对应的url地址
     * 所用到的所有工具类均在ktg-common子项目中
     * @param fgChecklist
     * @return
     */
    @Override
    public String generateBarcode(FgChecklist fgChecklist) {
        // uid即二维码内容
        File buf = BarcodeUtil.generateBarCode(fgChecklist.getUid(), fgChecklist.getBarcodeFormart(),
                "./tmp/barcode/" + fgChecklist.getUid() + ".png");

        // 写入临时文件
        MultipartFile file = FileUtils.getMultipartFile(buf);
        String fileName = null;
        try {
            // 获取上传后的文件名
            fileName = FileUploadUtils.uploadMinio(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            // 删除掉临时文件
            if (buf != null && buf.exists()) {
                FileUtils.deleteFile(buf.getAbsolutePath());
            }
        }

        return fileName;
    }

    /**
     * 根据UID查询结果生成打印单
     * @param uid
     * @return
     */
    @Override
    public List<FgChecklist> selectFgChecklistByUid(String[] uid) {
        return fgChecklistMapper.selectFgChecklistByUid(uid);
    }

    /**
     * 模糊查询PN
     * @param pnn
     * @return
     */
    @Override
    public List<FgChecklist> selectPn(String pnn) {
        List<FgChecklist> list = fgChecklistMapper.selectPn(pnn);
        return list;
    }

    /**
     * 根据具体PN查询是否有多个批次数据
     * @param pn
     * @return
     */
    @Override
    public List<FgChecklist> selectOnePn(String pn) {
        List<FgChecklist> fgChecklist = fgChecklistMapper.selectOnePn(pn);
        return fgChecklist;
    }

    /**
     * 根据具体PN和批次查询数据
     * @param pn，batch
     * @return
     */
    @Override
    public FgChecklist selectOnePn1(String pn, String batch) {
        FgChecklist fgChecklist = fgChecklistMapper.selectOnePn1(pn,batch);
        return fgChecklist;
    }

    @Override
    public FgChecklist getQAresult(String uid) {
        FgChecklist fgChecklist = fgChecklistMapper.getQAresult(uid);
        return fgChecklist;
    }

    @Override
    public int destroyUid(String uid) {
        int n = fgChecklistMapper.destroyUid(uid);
        return n;
    }

    @Override
    public Long getUidNo_Sum(String pn,String sap101) {

        if (fgChecklistMapper.getUidNo_Sum(pn,sap101) == null){
            return 0l;
        }else {
            return fgChecklistMapper.getUidNo_Sum(pn,sap101);
        }
    }


    @Override
    public int updateBeforeUidNo(String uid,Long uidNo) {
        int n = fgChecklistMapper.updateBeforeUidNo(uid,uidNo);
        return n;
    }

    @Override
    public int updateQAresult(String pn,int qaresult,String plant, String batch) {
        int n = fgChecklistMapper.updateQAresult(pn,qaresult,plant, batch);
        return n;
    }

    @Override
    public FgChecklist getPrintInfo(String uid) {
        FgChecklist fgChecklist = fgChecklistMapper.getPrintInfo(uid);
        return fgChecklist;
    }

    /**
     * SAP导入PMC确认的走货信息并判断是否拆箱
     *
     * @return String
     */
    @Override
    public String devanning_PMC() throws Exception {
        String isok = "";
        try {
            System.out.println("starting..  download start....." + new Date());

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // 当天日期的7天内数据判断
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String startDate = simpleDateFormat.format(date);
            String endDate = simpleDateFormat.format(calendar.getTime());

            SAPUtil sapUtil = new SAPUtil();
            List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS(startDate, endDate);
            if (list.size() == 0)
                throw new RuntimeException("not find info...");

            // 从SAP导入走货资料
            for (int i = 0; i < list.size(); i++) {
                FgShipmentInfo fgShipmentInfo = list.get(i);
                if (fgShipmentInfo.getSapPn() != null && fgShipmentInfo.getLastComfirm() != null && fgShipmentInfo.getLastComfirm().toString().equals("PMC") &&
                        fgTosMapper.checkInfoFs2(fgShipmentInfo) == 0) {

                    int n = fgTosMapper.insertFgShipmentInfo(fgShipmentInfo);
                }
            }

            // 邮件拆箱内容
            List<FgShipmentInfo> objectList = new ArrayList<>();
            for (int i = 0;i < list.size();i++) {
                double num = (double)list.get(i).getBoxQty();
                System.out.println(num);
                if (Math.abs(num % 1) > 0.000001) {
                    System.out.println("字符串中包含小数点");
                    System.out.println(list.get(i).getShipmentDate());
                    objectList.add(list.get(i));
                }
            }

            System.out.println(objectList.size());
            if (objectList.size() != 0) {
                System.out.println("发送邮件");
                sendMail(objectList);
            }
            isok = "OK";
            System.out.println("The end...." + new Date());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isok;
    }
//    @Override
//    public String devanning_PMC() throws Exception {
//        String isok = "";
//        try {
//            System.out.println("starting..  download start....." + new Date());
//
//            // Date date = new Date();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            // PMC和船务确认的信息都是当天日期的下一天
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//            String startDate = simpleDateFormat.format(calendar.getTime());
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
//                        fgTosMapper.checkInfoFs2(fgShipmentInfo) == 0) {
//
//                    int n = fgTosMapper.insertFgShipmentInfo(fgShipmentInfo);
//                }
//            }
//
//            // 邮件拆箱内容
//            List<Object> objectList = new ArrayList<>();
//            Object[] s = new Object[3];
//            // 根据 PN PO关联库存表查询（是否拆箱操作）
//            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPart("PMC");
//            for (int i = 0; i < list1.size(); i++) {
//
//                List<FgTosAndTosListDto> fgTosAndTosListDtos = fgTosMapper.getTosAndTOListInfo(list1.get(i).getShipmentNO().toString(), list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "PMC");
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
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "PMC", list1.get(i).getShipmentNO().toString());
//                                // 邮件提醒拆箱
//                                System.out.println("邮件提醒拆箱！");
//                                // 多出数量
//                                long qty2 = sum_uidno - list1.get(i).getBatchsum();
//                                // 需要数量
//                                long qty3 = sum_uidno - qty2;
//                                if (qty2 != 0) {
//                                    System.out.println(qty2);
//                                    s[0] = fgTosAndTosListDtos.get(j).getUid().toString();
//                                    s[1] = fgTosAndTosListDtos.get(j).getQuantity();
//                                    s[2] = qty3;
//                                    objectList.add(s);
//                                }
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
//            if (objectList.size() != 0) {
//                sendMail(objectList);
//            }
//            isok = "OK";
//            System.out.println("The end...." + new Date());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isok;
//    }

    /**
     * SAP导入船务确认的走货信息并产生备货单/走货单 后期可优化多重if嵌套
     *
     * @return String
     */
//    @Override
//    public String generateTO_NO() throws Exception {
//        String isok = "";
//        try {
//            System.out.println("starting..  download start....." + new Date());
//
//            Date date = new Date();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            // PMC和船务确认的信息都是当天日期的下一周/3天
//            calendar.add(Calendar.DAY_OF_MONTH, 3);
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//            String startDate = simpleDateFormat.format(date);
//            String endDate = simpleDateFormat.format(calendar.getTime());
//
//            SAPUtil sapUtil = new SAPUtil();
//            List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS("20231013", "20231014");
//            if (list.size() == 0)
//                throw new RuntimeException("not find info...");
//
//            // 从SAP导入走货资料（判空、查重、船务/货仓）
//            for (int i = 0; i < list.size(); i++) {
//                FgShipmentInfo fgShipmentInfo = list.get(i);
//                if (fgShipmentInfo.getSapPn() != null && fgShipmentInfo.getLastComfirm() != null && (fgShipmentInfo.getLastComfirm().toString().equals("船务") || fgShipmentInfo.getLastComfirm().toString().equals("货仓")) &&
//                        fgTosMapper.checkInfoFs(fgShipmentInfo) == 0) {
//
//                    int n = fgTosMapper.insertFgShipmentInfo(fgShipmentInfo);
//
//                } else if (fgTosMapper.checkInfoFs3(fgShipmentInfo) > 0) {
//                    fgTosMapper.updateShipmentQuantity(fgShipmentInfo);
//                }
//            }
//
//            List<String[]> strings = new ArrayList<>();
//            // 统计相同走货信息（PN、PO相同则数量累加【相当于去重】） sql语句直接判定船务/货仓
//            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPart("船务");
//            for (int i = 0; i < list1.size(); i++) {
//
//                FgTos fgTos = new FgTos();
//                FgToList fgToList = new FgToList();
//                // 统计同一个走货单数量总数
//                long shipment_qty = fgTosMapper.getQuantityByshipmentno(list1.get(i).getShipmentNO().toString());
//                System.out.println("测试2" + list1.get(i).getPn().toString() + list1.get(i).getPo().toString() + "ss" + list1.get(i).getBatchsum());
//                // 走货信息逐条 根据 PN PO 船务关联库存表查询
//                List<FgTosAndTosListDto> fgTosAndTosListDtos = fgTosMapper.getTosAndTOListInfo(list1.get(i).getShipmentNO().toString(), list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                // 查询是否已存在备货单/欠货单
//                String BH = fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString());
//                String QH = fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString());
//                // 当走货单的某个PN  PO在库存表中无数据，产生欠货单（欠货数量为改PN的批量），并邮件提醒
//                if (fgTosAndTosListDtos.size() == 0) {
//                    System.out.println("为什么产生欠货单" + list1.get(i).getShipmentNO() + list1.get(i).getPn() + list1.get(i).getPo());
//                    if (!QH.contains("QH")) {
//                        QH = generateTo_No("欠货单");
//                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
//                        fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), 0l, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
//                        fgTos.setCreateTime(date);
//                        fgTosMapper.insertFgTos(fgTos);
//                    }
//                    // 没有批次、库位、UID  状态（0备货单，1已拣货，2欠货单，3欠货单已备货）
//                    fgToList = setFgToList(fgToList, QH, list1.get(i).getBatchsum(), 2);
//                    fgToList.setPn(list1.get(i).getPn().toString());
//                    fgToList.setPo(list1.get(i).getPo().toString());
//                    fgToList.setSap_qty(list1.get(i).getBatchsum());
//                    fgToList.setCreateTime(date);
//                    fgTosMapper.insertFgTolist(fgToList);
//                    // 在TO明细表查询欠货总数赋值到TO管理表
//                    long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
//                    fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
//                    fgTosMapper.updateQuantity2(sumqty, QH);
//                    // 用于邮件提醒欠货 欠货TO单，走货流水号，走货日期，PN，走货数量，欠货数量，
//                    String[] s = new String[6];
//                    s[0] = QH;
//                    s[1] = list1.get(i).getShipmentNO().toString();
//                    s[2] = list1.get(i).getShipmentDate() + "";
//                    s[3] = list1.get(i).getPn().toString();
//                    s[4] = shipment_qty + "";
//                    s[5] = list1.get(i).getBatchsum() + "";
//                    strings.add(s);
//
//                } else {
//                    if (!BH.contains("BH")) {
//                        BH = generateTo_No("备货单");
//                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
//                        fgTos = setFgTos(BH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 0);
//                        fgTos.setCreateTime(date);
//                        // TO管理
//                        fgTosMapper.insertFgTos(fgTos);
//                    }
//                    long sum_uidno = 0;
//                    // 一条走货信息关联到多条库存数据，该数据根据条件逐条产生备货单/欠货单
//                    for (int j = 0; j < fgTosAndTosListDtos.size(); j++) {
//                        // 走货信息的批量 == 关联库存数据的总数量（即uid_no之和），直接插入TO明细表生成备货单，备货单号使用TO管理对应走货单的单号
//                        if (fgTosAndTosListDtos.get(j).getSum_uidno() == list1.get(i).getBatchsum()) {
//                            System.out.println("产生备货单1");
//                            // TO明细
//                            BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                            // 使用TO管理的备货单（即产生关联）
//                            fgToList.setTo_No(BH);
//                            fgToList.setCreateTime(date);
//                            // 状态（0备货单，1已拣货，2欠货单，3欠货单已备货）
//                            fgToList.setStatus(0);
//                            fgTosMapper.insertFgTolist(fgToList);
//                            fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                            long toNoSum = fgTosMapper.gettoNoSum(BH);
//                            fgTosMapper.updateTosQuantity(toNoSum, BH);
//                            fgTosMapper.updateTolistQuantity(toNoSum, BH);
//
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() > list1.get(i).getBatchsum()) {
//                            System.out.println("产生备货单2");
//                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
//                            System.out.println("sa" + sum_uidno);
//                            long qty = 0l;
//                            if (sum_uidno > list1.get(i).getBatchsum()) {
//                                // 累加到大于临界值情况 提醒拆分该成品单后 减去多出的部分 生成欠货单（后续判断库存表是否有拆分的数据 再生成新备货单）
//                                // 生成欠货单前判断TO管理是否已存在该走货单的欠货单，存在则直接在TO明细表生成欠货单，不存在则现在TO管理生成欠货单，再存到TO明细表
////                                if (!QH.contains("QH")) {
////                                    QH = generateTo_No("欠货单");
////                                    // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
////                                    fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
////                                    fgTosMapper.insertFgTos(fgTos);
////                                }
////                                // 欠货数量（即所需要备货的数量）
////                                qty = sum_uidno - list1.get(i).getBatchsum();
////                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
////                                fgToList.setBatch("");
////                                fgToList.setStock("");
////                                fgToList.setUid("");
////                                // 统一赋值
////                                fgToList = setFgToList(fgToList, QH, qty, 0);
////                                fgTosMapper.insertFgTolist(fgToList);
////                                long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
////                                fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
////                                String[] s = new String[2];
////                                s[0] = fgTosAndTosListDtos.get(j).getUid().toString();
////                                s[1] = qty + "";
////                                strings.add(s);
//                                // 累加到当前UID后数量比批量大，则按到批量的数量进行备货，多出的数量在PDA拣货时提醒拆箱，若没拆箱不给拣货，（不需要产生备货单）
//                                // 该UID需备货的数量(在PDA显示的数量，实际可能还未拆分)
//                                long qtyy = sum_uidno - list1.get(i).getBatchsum();
//                                qty = fgTosAndTosListDtos.get(j).getQuantity() - qtyy;
//                                System.out.println("jtjtjtjt" + qty);
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, qty, 0);
//                                fgToList.setCreateTime(date);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                long toNoSum = fgTosMapper.gettoNoSum(BH);
//                                System.out.println("jjjjtttt" + toNoSum);
//                                fgTosMapper.updateTosQuantity(toNoSum, BH);
//                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
//                                break;
//
//                            } else if (sum_uidno == list1.get(i).getBatchsum()) {
//                                // 累加到刚好等于的情况 则备货后直接break退出循环，后面的关联数据就不用进行备货了
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgToList.setCreateTime(date);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                long toNoSum = fgTosMapper.gettoNoSum(BH);
//                                fgTosMapper.updateTosQuantity(toNoSum, BH);
//                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
//                                break;
//                            } else {
//                                // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
//                                System.out.println("11111");
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgToList.setCreateTime(date);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                long toNoSum = fgTosMapper.gettoNoSum(BH);
//                                fgTosMapper.updateTosQuantity(toNoSum, BH);
//                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
//                            }
//                            System.out.println("邮件提醒还未拆箱");
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() < list1.get(i).getBatchsum()) {
//                            System.out.println("产生备货单3");
//                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
//                            // 临界值为库存总数
//                            if (sum_uidno == fgTosAndTosListDtos.get(j).getSum_uidno()) {
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgToList.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
//                                fgToList.setCreateTime(date);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                long toNoSum = fgTosMapper.gettoNoSum(BH);
//                                fgTosMapper.updateTosQuantity(toNoSum, BH);
//                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
//                                // 总数小于 批次总数则产生备货单（即剩下的数量为欠货数量）
//                                if (!QH.contains("QH")) {
//                                    QH = generateTo_No("欠货单");
//                                    fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
//                                    fgTos.setCreateTime(date);
//                                    fgTosMapper.insertFgTos(fgTos);
//                                }
//                                long qty = list1.get(i).getBatchsum() - fgTosAndTosListDtos.get(j).getSum_uidno();
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList.setBatch("");
//                                fgToList.setStock("");
//                                fgToList.setUid("");
//                                fgToList.setQuantity(qty);
//                                // 将欠料数存到TO明细表
//                                fgToList = setFgToList(fgToList, QH, qty, 2);
//                                fgToList.setCreateTime(date);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
//                                fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
//                                fgTosMapper.updateQuantity2(sumqty, QH);
//                                // 用于邮件提醒欠货 欠货TO单，走货流水号，走货日期，PN，走货数量，欠货数量
//                                String[] s = new String[6];
//                                s[0] = QH;
//                                s[1] = fgTosAndTosListDtos.get(j).getShipmentNO().toString();
//                                s[2] = list1.get(i).getShipmentDate() + "";
//                                s[3] = fgTosAndTosListDtos.get(j).getPn().toString();
//                                s[4] = list1.get(i).getBatchsum() + "";
//                                s[5] = sumqty + "";
//                                strings.add(s);
//                                break;
//                            } else {
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgToList.setCreateTime(date);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                long toNoSum = fgTosMapper.gettoNoSum(BH);
//                                fgTosMapper.updateTosQuantity(toNoSum, BH);
//                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
//                            }
//                        }
//                    }
//                }
//            }
//            // 更新这批走货信息状态（避免重复备货）、不在以上循环更新是因为上面需要获取走货总数，状态更新了会获取出错
//            for (int i = 0; i < list1.size(); i++) {
//                fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务", list1.get(i).getShipmentNO().toString());
//            }
//            // 邮件提醒欠货/拆分成品单
//            if (strings.size() > 0) {
//                System.out.println("邮件提醒欠货***");
//                sendMail_QH(strings);
//            }
//
//
//            isok = "OK";
//            System.out.println("The end...." + new Date());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isok;
//    }

    @Override
    public String generateTO_NO(Date shipmentDate) throws Exception {
        String isok = "";
        try {
            System.out.println("starting..  download start....." + new Date());

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // PMC和船务确认的信息都是当天日期的下一周/3天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String startDate = simpleDateFormat.format(date);
            String endDate = simpleDateFormat.format(calendar.getTime());
            // 手动选择日期产生TO
            String ShipmentDate = simpleDateFormat.format(shipmentDate == null ? new Date() : shipmentDate);
            SAPUtil sapUtil = new SAPUtil();
            List<FgShipmentInfo> list = new ArrayList<>();
            System.out.println(ShipmentDate + endDate + startDate);
            System.out.println(ShipmentDate.compareTo(endDate));
            // 日期为20230101表示没有手动选择（即自动产生）
            if (ShipmentDate.compareTo(endDate) > 0) {
                System.out.println("选择日期大于自动日期（按选择日期产生）");
                list = sapUtil.Z_HTMES_ZSDSHIPLS(ShipmentDate, ShipmentDate);
            } else if (ShipmentDate.compareTo(startDate) < 0) {
                System.out.println("选择日期小于自动日期（按自动日期产生）");
                list = sapUtil.Z_HTMES_ZSDSHIPLS(startDate, endDate);
            } else {
                System.out.println("选择日期介于最大和最小日期之间（按自动日期产生）");
                list = sapUtil.Z_HTMES_ZSDSHIPLS(startDate, endDate);
            }

            // list = sapUtil.Z_HTMES_ZSDSHIPLS("20231024", "20231024");
            if (list.size() == 0)
                throw new RuntimeException("not find info...");

            List<FgShipmentInfo> ck00_list = new ArrayList<>();
            List<FgShipmentInfo> llist = new ArrayList<>();
            for (int i = 0;i < list.size();i++){
                if (list.get(i).getClientCode() != null && list.get(i).getClientCode().equals("CK00")) {
                    ck00_list.add(list.get(i));
                } else {
                    llist.add(list.get(i));
                }
            }

            // 从SAP导入走货资料（判空、查重、船务/货仓）
            for (int i = 0; i < list.size(); i++) {
                FgShipmentInfo fgShipmentInfo = list.get(i);
                if (fgShipmentInfo.getSapPn() != null && fgShipmentInfo.getLastComfirm() != null && (fgShipmentInfo.getLastComfirm().toString().equals("船务") || fgShipmentInfo.getLastComfirm().toString().equals("货仓")) &&
                        fgTosMapper.checkInfoFs(fgShipmentInfo) == 0) {

                    int n = fgTosMapper.insertFgShipmentInfo(fgShipmentInfo);

                } else if (fgTosMapper.checkInfoFs3(fgShipmentInfo) > 0) {
                    fgTosMapper.updateShipmentQuantity(fgShipmentInfo);
                }
            }
            // CK00 单独产生TO
            // GenerateToNo_CK00(ck00_list);

            // 存放欠货信息 -----------   （以下产生TO与CK00无关）
            List<String[]> strings = new ArrayList<>();
            // 统计相同走货信息（PN、PO相同则数量累加【相当于去重】） sql语句直接判定船务/货仓
            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPart("船务");
            for (int i = 0; i < list1.size(); i++) {

                FgTos fgTos = new FgTos();
                FgToList fgToList = new FgToList();
                // 统计同一个走货单数量总数
                long shipment_qty = fgTosMapper.getQuantityByshipmentno(list1.get(i).getShipmentNO().toString());
                System.out.println("测试2" + list1.get(i).getPn().toString() + list1.get(i).getPo().toString() + "ss" + list1.get(i).getBatchsum());
                // 走货信息逐条 根据 PN PO 船务关联库存表查询
                List<FgTosAndTosListDto> fgTosAndTosListDtos = fgTosMapper.getTosAndTOListInfo(list1.get(i).getShipmentNO().toString(), list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
                // 查询是否已存在备货单/欠货单
                String BH = fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString());
                String QH = fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString());
                // 当走货单的某个PN  PO在库存表中无数据，产生欠货单（欠货数量为改PN的批量），并邮件提醒
                if (fgTosAndTosListDtos.size() == 0) {
                    System.out.println("为什么产生欠货单" + list1.get(i).getShipmentNO() + list1.get(i).getPn() + list1.get(i).getPo());
                    if (!QH.contains("QH")) {
                        QH = generateTo_No("欠货单");
                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
                        fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), 0l, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
                        fgTos.setCreateTime(date);
                        fgTosMapper.insertFgTos(fgTos);
                    }
                    // 没有批次、库位、UID  状态（0备货单，1已拣货，2欠货单，3欠货单已备货）
                    fgToList = setFgToList(fgToList, QH, list1.get(i).getBatchsum(), 2);
                    fgToList.setPn(list1.get(i).getPn().toString());
                    fgToList.setPo(list1.get(i).getPo().toString());
                    fgToList.setSap_qty(list1.get(i).getBatchsum());
                    fgToList.setCreateTime(date);
                    fgTosMapper.insertFgTolist(fgToList);
                    // 在TO明细表查询欠货总数赋值到TO管理表
                    long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
                    fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
                    fgTosMapper.updateQuantity2(sumqty, QH);
                    // 用于邮件提醒欠货 欠货TO单，走货流水号，走货日期，PN，走货数量，欠货数量，
                    String[] s = new String[6];
                    s[0] = QH;
                    s[1] = list1.get(i).getShipmentNO().toString();
                    s[2] = list1.get(i).getShipmentDate() + "";
                    s[3] = list1.get(i).getPn().toString();
                    s[4] = shipment_qty + "";
                    s[5] = list1.get(i).getBatchsum() + "";
                    strings.add(s);

                } else {
                    if (!BH.contains("BH")) {
                        BH = generateTo_No("备货单");
                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
                        fgTos = setFgTos(BH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 0);
                        fgTos.setCreateTime(date);
                        // TO管理
                        fgTosMapper.insertFgTos(fgTos);
                    }
                    long sum_uidno = 0;
                    // 一条走货信息关联到多条库存数据，该数据根据条件逐条产生备货单/欠货单
                    for (int j = 0; j < fgTosAndTosListDtos.size(); j++) {
                        // 走货信息的批量 == 关联库存数据的总数量（即uid_no之和），直接插入TO明细表生成备货单，备货单号使用TO管理对应走货单的单号
                        if (fgTosAndTosListDtos.get(j).getSum_uidno() == list1.get(i).getBatchsum()) {
                            System.out.println("产生备货单1");
                            // TO明细
                            BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                            // 使用TO管理的备货单（即产生关联）
                            fgToList.setTo_No(BH);
                            fgToList.setCreateTime(date);
                            // 状态（0备货单，1已拣货，2欠货单，3欠货单已备货）
                            fgToList.setStatus(0);
                            fgTosMapper.insertFgTolist(fgToList);
                            fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                            long toNoSum = fgTosMapper.gettoNoSum(BH);
                            fgTosMapper.updateTosQuantity(toNoSum, BH);
                            fgTosMapper.updateTolistQuantity(toNoSum, BH);

                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() > list1.get(i).getBatchsum()) {
                            System.out.println("产生备货单2");
                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
                            System.out.println("sa" + sum_uidno);
                            long qty = 0l;
                            if (sum_uidno > list1.get(i).getBatchsum()) {
                                // 累加到大于临界值情况 提醒拆分该成品单后 减去多出的部分 生成欠货单（后续判断库存表是否有拆分的数据 再生成新备货单）
                                // 生成欠货单前判断TO管理是否已存在该走货单的欠货单，存在则直接在TO明细表生成欠货单，不存在则现在TO管理生成欠货单，再存到TO明细表
//                                if (!QH.contains("QH")) {
//                                    QH = generateTo_No("欠货单");
//                                    // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
//                                    fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
//                                    fgTosMapper.insertFgTos(fgTos);
//                                }
//                                // 欠货数量（即所需要备货的数量）
//                                qty = sum_uidno - list1.get(i).getBatchsum();
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList.setBatch("");
//                                fgToList.setStock("");
//                                fgToList.setUid("");
//                                // 统一赋值
//                                fgToList = setFgToList(fgToList, QH, qty, 0);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
//                                fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
//                                String[] s = new String[2];
//                                s[0] = fgTosAndTosListDtos.get(j).getUid().toString();
//                                s[1] = qty + "";
//                                strings.add(s);
                                // 累加到当前UID后数量比批量大，则按到批量的数量进行备货，多出的数量在PDA拣货时提醒拆箱，若没拆箱不给拣货，（不需要产生备货单）
                                // 该UID需备货的数量(在PDA显示的数量，实际可能还未拆分)
                                long qtyy = sum_uidno - list1.get(i).getBatchsum();
                                qty = fgTosAndTosListDtos.get(j).getQuantity() - qtyy;
                                System.out.println("jtjtjtjt" + qty);
                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                                fgToList = setFgToList(fgToList, BH, qty, 0);
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                long toNoSum = fgTosMapper.gettoNoSum(BH);
                                System.out.println("jjjjtttt" + toNoSum);
                                fgTosMapper.updateTosQuantity(toNoSum, BH);
                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
                                break;

                            } else if (sum_uidno == list1.get(i).getBatchsum()) {
                                // 累加到刚好等于的情况 则备货后直接break退出循环，后面的关联数据就不用进行备货了
                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                                fgToList = setFgToList(fgToList, BH, 0l, 0);
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                long toNoSum = fgTosMapper.gettoNoSum(BH);
                                fgTosMapper.updateTosQuantity(toNoSum, BH);
                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
                                break;
                            } else {
                                // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
                                System.out.println("11111");
                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                                fgToList = setFgToList(fgToList, BH, 0l, 0);
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                long toNoSum = fgTosMapper.gettoNoSum(BH);
                                fgTosMapper.updateTosQuantity(toNoSum, BH);
                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
                            }
                            System.out.println("邮件提醒还未拆箱");
                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() < list1.get(i).getBatchsum()) {
                            System.out.println("产生备货单3");
                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
                            // 临界值为库存总数
                            if (sum_uidno == fgTosAndTosListDtos.get(j).getSum_uidno()) {
                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                                fgToList = setFgToList(fgToList, BH, 0l, 0);
                                fgToList.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                long toNoSum = fgTosMapper.gettoNoSum(BH);
                                fgTosMapper.updateTosQuantity(toNoSum, BH);
                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
                                // 总数小于 批次总数则产生备货单（即剩下的数量为欠货数量）
                                if (!QH.contains("QH")) {
                                    QH = generateTo_No("欠货单");
                                    fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
                                    fgTos.setCreateTime(date);
                                    fgTosMapper.insertFgTos(fgTos);
                                }
                                long qty = list1.get(i).getBatchsum() - fgTosAndTosListDtos.get(j).getSum_uidno();
                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                                fgToList.setBatch("");
                                fgToList.setStock("");
                                fgToList.setUid("");
                                fgToList.setQuantity(qty);
                                // 将欠料数存到TO明细表
                                fgToList = setFgToList(fgToList, QH, qty, 2);
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
                                fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
                                fgTosMapper.updateQuantity2(sumqty, QH);
                                // 用于邮件提醒欠货 欠货TO单，走货流水号，走货日期，PN，走货数量，欠货数量
                                String[] s = new String[6];
                                s[0] = QH;
                                s[1] = fgTosAndTosListDtos.get(j).getShipmentNO().toString();
                                s[2] = list1.get(i).getShipmentDate() + "";
                                s[3] = fgTosAndTosListDtos.get(j).getPn().toString();
                                s[4] = list1.get(i).getBatchsum() + "";
                                s[5] = sumqty + "";
                                strings.add(s);
                                break;
                            } else {
                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
                                fgToList = setFgToList(fgToList, BH, 0l, 0);
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                long toNoSum = fgTosMapper.gettoNoSum(BH);
                                fgTosMapper.updateTosQuantity(toNoSum, BH);
                                fgTosMapper.updateTolistQuantity(toNoSum, BH);
                            }
                        }
                    }
                }
            }
            // 更新这批走货信息状态（避免重复备货）、不在以上循环更新是因为上面需要获取走货总数，状态更新了会获取出错
            for (int i = 0; i < list1.size(); i++) {
                fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务", list1.get(i).getShipmentNO().toString());
            }
            // 邮件提醒欠货/拆分成品单
            if (strings.size() > 0) {
                System.out.println("邮件提醒欠货***");
                sendMail_QH(strings);
            }


            isok = "OK";
            System.out.println("The end...." + new Date());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isok;
    }

    /**
     * CK00 客户产生TO单
     * */
    public void GenerateToNo_CK00(List<FgShipmentInfo> list) {

        Date date = new Date();
        try {

            List<String[]> strings = new ArrayList<>();
            // 获取650对应的660 sql语句直接判定船务/货仓
            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPartCK00("船务");
            for (int i = 0; i < list1.size(); i++) {
                // 查询是否已存在备货单/欠货单
                String BH = fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString());
                String QH = fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString());
                // 根据660PN、PO查询660对应是否都对应4个650
                List<String> stringList = fgTosMapper.checkCount(list1.get(i));
                if (stringList.size() != 4) {
                    System.out.println("660产生欠货单" + list1.get(i).getPn());
                    // （不等）则660产生欠货单
                    FgTos fgTos = new FgTos();
                    FgToList fgToList = new FgToList();
                    if ("".equals(QH)) {
                        QH = generateTo_No("欠货单");
                        fgTos.setTo_No(QH);
                        fgTos.setShipmentNO(list1.get(i).getShipmentNO().toString());
                        fgTos.setSap_qty(list1.get(i).getBatchsum());
                        fgTos.setCarNo(list1.get(i).getCarno());
                        fgTos.setPlant(list1.get(i).getPlant());
                        fgTos.setStatus(3);
                        fgTos.setCreateTime(date);
                        // 插入TO管理
                        fgTosMapper.insertFgTos(fgTos);
                    }
                    fgToList.setTo_No(QH);
                    fgToList.setPn(list1.get(i).getPn());
                    fgToList.setPo(list1.get(i).getPo());
                    fgToList.setQuantity(list1.get(i).getBatchsum());
                    fgToList.setSap_qty(list1.get(i).getBatchsum());
                    fgToList.setStatus(2);
                    fgToList.setCreateTime(date);
                    fgTosMapper.insertFgTolist(fgToList);
                    long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
                    fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
                    fgTosMapper.updateQuantity2(sumqty, QH);
                    // 用于邮件提醒欠货 欠货TO单，走货流水号，走货日期，PN，走货数量，欠货数量，
                    String[] s = new String[6];
                    s[0] = QH;
                    s[1] = list1.get(i).getShipmentNO().toString();
                    s[2] = list1.get(i).getShipmentDate() + "";
                    s[3] = list1.get(i).getPn().toString();
                    s[4] = list1.get(i).getBatchsum() + "";
                    s[5] = list1.get(i).getBatchsum() + "";
                    strings.add(s);

                } else {
                    FgTos fgTos = new FgTos();
                    FgToList fgToList = new FgToList();

                    // 查询每个650对应工单数量累加是否都等于走货数量 （650可能绑多个660，因此需加上660PN）
                    System.out.println("650Pn" + stringList.get(0).toString() + stringList.get(1).toString() +
                            stringList.get(2).toString() + stringList.get(3).toString());
                    ReturnResult returnResult = fgTosMapper.checkSumByWO(stringList.get(0).toString(), stringList.get(1).toString(),
                            stringList.get(2).toString(), stringList.get(3).toString(), list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), list1.get(i).getBatchsum());
                    if (returnResult.getFlag1().equals("false") || returnResult.getFlag2().equals("false") || returnResult.getFlag3().equals("false") || returnResult.getFlag4().equals("false")) {
                        System.out.println("660产生欠货单-650层" + list1.get(i).getPn());
                        // 产生欠货单
                        if ("".equals(QH)) {
                            QH = generateTo_No("欠货单");
                            fgTos.setTo_No(QH);
                            fgTos.setShipmentNO(list1.get(i).getShipmentNO().toString());
                            fgTos.setSap_qty(list1.get(i).getBatchsum());
                            fgTos.setCarNo(list1.get(i).getCarno());
                            fgTos.setPlant(list1.get(i).getPlant());
                            fgTos.setStatus(3);
                            fgTos.setCreateTime(date);
                            // 插入TO管理
                            fgTosMapper.insertFgTos(fgTos);
                        }
                        fgToList.setTo_No(QH);
                        fgToList.setPn(list1.get(i).getPn());
                        fgToList.setPo(list1.get(i).getPo());
                        fgToList.setQuantity(list1.get(i).getBatchsum());
                        fgToList.setSap_qty(list1.get(i).getBatchsum());
                        fgToList.setStatus(2);
                        fgToList.setCreateTime(date);
                        fgTosMapper.insertFgTolist(fgToList);
                        // 在TO明细表查询欠货总数赋值到TO管理表
                        long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
                        fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
                        fgTosMapper.updateQuantity2(sumqty, QH);
                        String[] s = new String[6];
                        s[0] = QH;
                        s[1] = list1.get(i).getShipmentNO().toString();
                        s[2] = list1.get(i).getShipmentDate() + "";
                        s[3] = list1.get(i).getPn().toString();
                        s[4] = list1.get(i).getBatchsum() + "";
                        s[5] = list1.get(i).getBatchsum() + "";
                        strings.add(s);

                    } else {
                        System.out.println("660产生备货单" + list1.get(i).getPn());
                        // 产生备货单
                        if ("".equals(BH)) {
                            BH = generateTo_No("备货单");
                            fgTos.setTo_No(BH);
                            fgTos.setShipmentNO(list1.get(i).getShipmentNO().toString());
                            fgTos.setSap_qty(list1.get(i).getBatchsum());
                            fgTos.setCarNo(list1.get(i).getCarno());
                            fgTos.setPlant(list1.get(i).getPlant());
                            fgTos.setStatus(0);
                            fgTos.setCreateTime(date);
                            // 插入TO管理
                            fgTosMapper.insertFgTos(fgTos);
                        }

                        // 扫650下架需预留每个650
                        List<String> stringList1 = new ArrayList<>();
                        stringList1.add(returnResult.getFlag1());
                        stringList1.add(returnResult.getFlag2());
                        stringList1.add(returnResult.getFlag3());
                        stringList1.add(returnResult.getFlag4());
                        for (int j = 0;j < stringList.size();j++) {
                            List<ReturnResult> returnResult1 = fgTosMapper.getInfo650(stringList.get(j).toString(), list1.get(i).getPn(), list1.get(i).getPo(), list1.get(i).getBatchsum(), stringList1.get(j));
                            System.out.println("获取650信息" + returnResult1.toString());

                            for (int z = 0;z < returnResult1.size();z++){
                                fgToList.setTo_No(BH);
                                fgToList.setUid(returnResult1.get(z).getUid());
                                fgToList.setPn(returnResult1.get(z).getPn());
                                fgToList.setPo(list1.get(i).getPo());
                                fgToList.setQuantity(returnResult1.get(z).getQuantity());
                                fgToList.setSap_qty(list1.get(i).getBatchsum());
                                fgToList.setStock(returnResult1.get(z).getStock());
                                fgToList.setStatus(0);
                                fgToList.setCreateTime(date);
                                fgTosMapper.insertFgTolist(fgToList);
                                // 库存状态为 已预留
                                fgInventoryMapper.updateStatusByUid(returnResult1.get(z).getUid());
                            }
                        }
                        // 更新TOs总数
                        long toNoSum = fgTosMapper.gettoNoSum(BH);
                        fgTosMapper.updateTosQuantity(toNoSum, BH);
                        fgTosMapper.updateTolistQuantity(toNoSum, BH);

                    }
                }

            }
            // 更新这批走货信息状态（避免重复备货）、不在以上循环更新是因为上面需要获取走货总数，状态更新了会获取出错
            for (int i = 0; i < list1.size(); i++) {
                fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务", list1.get(i).getShipmentNO().toString());
            }
            // 邮件提醒欠货/拆分成品单
            if (strings.size() > 0) {
                System.out.println("邮件提醒欠货***");
                sendMail_QH(strings);
            }

            System.out.println("The end...." + new Date());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *是否绑定有贴纸，有绑定则按贴纸预留
     * */
//    public String checkTags(String BH, FgToList fgToList, long quantity) {
//
//        // 查询是否存在贴纸（存在则预留贴纸代替UID）
//        List<FgTagsInventory> fgTagsInventoryList = fgInventoryMapper.getTagsInventoryInfo(fgToList.getUid().toString());
//        if (fgTagsInventoryList.size() > 0) {
//            // stream流统计总数
//            long sum_tags = fgTagsInventoryList.stream().mapToLong(FgTagsInventory::getQuantity).sum();
//            if (quantity == sum_tags) {
//                // 预留贴纸
//                List<FgToList> fgToListList = new ArrayList<>();
//
//            } else {
//                // 产生欠货单
//            }
//        }
//
//        return null;
//    }

    /**
     * 根据 BH或QH + 年、月、日、时、分、秒、毫秒（三位数） 生成备货单/备货单
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

    /***
     * 生成备货单/欠货单TO管理表同一赋值
     */
    public FgTos setFgTos(String toNo, String shipmentNo, long sapQty, String carNo, String plant, int status) {

        // 直接创建对象赋值，因为没有原值需要返回（对比setFgToList()区别）
        FgTos fgTos = new FgTos();
        fgTos.setTo_No(toNo);
        fgTos.setShipmentNO(shipmentNo);
        fgTos.setSap_qty(sapQty);
        fgTos.setCarNo(carNo);
        fgTos.setPlant(plant);
        fgTos.setStatus(status);

        return fgTos;
    }

    /***
     * 生成备货单/欠货单TO明细表同一赋值
     *
     */
    public FgToList setFgToList(FgToList fgToList, String toNo, long quantity, int status) {

        // 不在方法内创建FgToList对象是因为只需要设置其它字段的值，原有的值需要保留一并返回（对比setFgTos()区别）
        // 数量为0则不需要设置数量，原值已有
        if (quantity == 0l) {
            fgToList.setTo_No(toNo);
            fgToList.setStatus(status);
        } else {
            fgToList.setTo_No(toNo);
            fgToList.setQuantity(quantity);
            fgToList.setStatus(status);
        }
        return fgToList;
    }

    // 拆箱邮件
    public void sendMail(List<FgShipmentInfo> objectList) throws MessagingException, javax.mail.MessagingException, IOException {

        try {
            System.out.println("进入");

            //1. 创建一个工作簿对象
            XSSFWorkbook workbook2 = new XSSFWorkbook();
            //2. 创建一个工作表对象
            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
            // 设置表头
            String[] headers = {"走货日期", "走货单", "型号", "客户PN", "客户名", "走货数量", "PCS/箱", "箱数", "备注"};
            XSSFRow headerRow = sheet2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Cell cell = null;
            // 在表格中写入数据
            int rowCount = 1;

            for (int i = 0;i < objectList.size();i++) {
                XSSFRow row = sheet2.createRow(rowCount++);
                int cellCount = 0;

                cell = row.createCell(cellCount++);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String shipmentDate = sdf.format(objectList.get(i).getShipmentDate());
                cell.setCellValue(shipmentDate);

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) objectList.get(i).getShipmentNO() == null ? "" : objectList.get(i).getShipmentNO().toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) objectList.get(i).getSapPn() == null ? "" : objectList.get(i).getSapPn().toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) objectList.get(i).getClientPN() == null ? "" : objectList.get(i).getClientPN().toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) objectList.get(i).getClientCode() == null ? "" : objectList.get(i).getClientCode().toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue(objectList.get(i).getQuantity() == null ? 0 : objectList.get(i).getQuantity());

                cell = row.createCell(cellCount++);
                cell.setCellValue(objectList.get(i).getPcsQty() == null ? 0 : objectList.get(i).getPcsQty());

                cell = row.createCell(cellCount++);
                cell.setCellValue(objectList.get(i).getBoxQty());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) objectList.get(i).getRemark() == null ? "" : objectList.get(i).getRemark().toString());

            }
            // 自适应宽度
            for (int i = 0; i < headers.length; i++) {
                sheet2.autoSizeColumn(i);
            }
            //4. 创建文件并保存
            File file = new File("拆箱邮件.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook2.write(outputStream);
            outputStream.close();
            workbook2.close();
            String[] to = {"mei.zhu@honortone.com", "ni.cai@honortone.com", "zhongping.luo@honortone.com", "kaitong.ye@honortone.com", "shaopeng.liang@honortone.com", "ping.hu@honortone.com"};
            String[] cc = {"tingming.jiang@honortone.com"};
            //5. 创建一个邮件对象
            MimeMessage message2 = javaMailSender.createMimeMessage();
            MimeMessageHelper helper2 = new MimeMessageHelper(message2, true);
            //6. 设置发件人、收件人、抄送、密送、主题、内容和附件
            helper2.setFrom(from);
            helper2.setTo(to);
            helper2.setCc(cc);
            // 密送
            // helper2.setBcc("tingming.jiang@honortone.com");
            helper2.setSubject("拆箱提醒");
            helper2.setText("请查收附件，谢谢！", true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            helper2.addAttachment(MimeUtility.encodeWord("拆箱邮件.xlsx"), file);
            //7. 发送邮件
            javaMailSender.send(message2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 欠货提醒邮件
    public void sendMail_QH(List<String[]> strings) throws MessagingException, javax.mail.MessagingException, IOException {

        try {
            System.out.println("进入");

            //1. 创建一个工作簿对象
            XSSFWorkbook workbook2 = new XSSFWorkbook();
            //2. 创建一个工作表对象
            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
            // 设置表头   欠货TO单，走货流水号，走货日期，PN，走货数量，欠货数量
            String[] headers = {"欠货TO", "走货单", "走货日期", "PN", "走货数量", "欠货数量"};
            XSSFRow headerRow = sheet2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Cell cell = null;
            // 在表格中写入数据
            int rowCount = 1;

            for (int i = 0;i < strings.size();i++) {
                XSSFRow row = sheet2.createRow(rowCount++);
                int cellCount = 0;

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) strings.get(i)[0] == null ? "" : strings.get(i)[0].toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) strings.get(i)[1] == null ? "" : strings.get(i)[1].toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue(strings.get(i)[2]);

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) strings.get(i)[3] == null ? "" : strings.get(i)[3].toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) strings.get(i)[4] == null ? "" : strings.get(i)[4].toString());

                cell = row.createCell(cellCount++);
                cell.setCellValue((String) strings.get(i)[5] == null ? "" : strings.get(i)[5].toString());

            }
            // 自适应宽度
            for (int i = 0; i < headers.length; i++) {
                sheet2.autoSizeColumn(i);
            }
            //4. 创建文件并保存
            File file = new File("欠货数据报表.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook2.write(outputStream);
            outputStream.close();
            workbook2.close();
            String[] to = {"mei.zhu@honortone.com", "ni.cai@honortone.com", "zhongping.luo@honortone.com", "kaitong.ye@honortone.com", "shaopeng.liang@honortone.com", "ping.hu@honortone.com"};
            String[] cc = {"tingming.jiang@honortone.com"};
            //5. 创建一个邮件对象
            MimeMessage message2 = javaMailSender.createMimeMessage();
            MimeMessageHelper helper2 = new MimeMessageHelper(message2, true);
            //6. 设置发件人、收件人、抄送、密送、主题、内容和附件
            helper2.setFrom(from);
            helper2.setTo(to);
            helper2.setCc(cc);
            // 密送
            // helper2.setBcc("tingming.jiang@honortone.com");
            helper2.setSubject("欠货提醒");
            helper2.setText("请查收附件，谢谢！", true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            helper2.addAttachment(MimeUtility.encodeWord("欠货提醒邮件.xlsx"), file);
            //7. 发送邮件
            javaMailSender.send(message2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public void sendMail(List<Object> objectList) throws MessagingException, javax.mail.MessagingException, IOException {
//
//        try {
//            System.out.println("进入");
//
//            //1. 创建一个工作簿对象
//            XSSFWorkbook workbook2 = new XSSFWorkbook();
//            //2. 创建一个工作表对象
//            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
//            // 设置表头
//            String[] headers = {"UID", "UID数量", "需求数量"};
//            XSSFRow headerRow = sheet2.createRow(0);
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//            }
//            // 在表格中写入数据
//            int rowCount = 1;
//            for (int i = 0;i < objectList.size();i++) {
//                Object[] contents = (Object[]) objectList.get(i);
//                XSSFRow row = sheet2.createRow(rowCount++);
//                for (int j = 0; j < contents.length; j++) {
//                    Cell cell = row.createCell(j);
//                    if (j == 1 || j == 2) {
//                        cell.setCellValue((long) contents[j]);
//                    } else {
//                        cell.setCellValue((String) contents[j]);
//                    }
//                }
//            }
//            // 自适应宽度
//            for (int i = 0; i < headers.length; i++) {
//                sheet2.autoSizeColumn(i);
//            }
//            //4. 创建文件并保存
//            File file = new File("拆箱邮件.xlsx");
//            FileOutputStream outputStream = new FileOutputStream(file);
//            workbook2.write(outputStream);
//            outputStream.close();
//            workbook2.close();
//            //5. 创建一个邮件对象
//            MimeMessage message2 = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper2 = new MimeMessageHelper(message2, true);
//            //6. 设置发件人、收件人、抄送、密送、主题、内容和附件
//            helper2.setFrom(from);
//            helper2.setTo("tingming.jiang@honortone.com");
//            helper2.setCc("tingming.jiang@honortone.com");
//            // 密送
//            // helper2.setBcc("tingming.jiang@honortone.com");
//            helper2.setSubject("拆箱提醒");
//            helper2.setText("请查收附件，谢谢！", true);
//            // MimeUtility.encodeWord 解决附件名和格式乱码问题
//            helper2.addAttachment(MimeUtility.encodeWord("拆箱邮件.xlsx"), file);
//            //7. 发送邮件
//            javaMailSender.send(message2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
