package com.honortone.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honortone.api.entity.*;
import com.honortone.api.mapper.BindStockMapper;
import com.honortone.api.mapper.GMterialsMapper;
import com.honortone.api.service.GMaterialsService;
import com.honortone.api.utils.SAPUtil;
import com.ktg.mes.fg.controller.FgChecklistController;
import com.ktg.mes.fg.domain.FgChecklist;
import io.swagger.models.auth.In;
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

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GMaterialsServiceImpl extends ServiceImpl<GMterialsMapper, Inventory> implements GMaterialsService {

    @Autowired
    private GMterialsMapper gMterialsMapper;

    @Autowired
    private BindStockMapper bindStockMapper;
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public List<String> downloadCarno(String date) {
        return gMterialsMapper.downloadCarno(date);
    }

    @Override
    public List<String> downloadShipmentno(String carno, String date) {
        return gMterialsMapper.downloadShipmentno(carno, date);
    }

    @Override
    public List<String> downloadTos(String shipmentno) {
        return gMterialsMapper.downloadTos(shipmentno);
    }

    @Override
    public List<String> downloadTos2(String shipmentno) {
        return gMterialsMapper.downloadTos2(shipmentno);
    }

    @Override
    public List<ToList> downloadTono(String tono) {
        return gMterialsMapper.downloadTono(tono);
    }

    @Override
    public List<TagsInventory> selectClientTag(String clientPn) {

        List<TagsInventory> tagsInventories = gMterialsMapper.selectClientTag(clientPn);
        return tagsInventories;
    }

    @Override
    public ToList checkTolistUID(String uid) {

        ToList toList = gMterialsMapper.checkTolistUID(uid);
        return toList;
    }

    @Override
    public long getSumQuantity(String uid) {

        long sum = gMterialsMapper.getSumQuantity(uid);
        return sum;
    }

    @Override
    public int updateTagsStauts(String clientPn, long quantity) {

        int n = gMterialsMapper.updateTagsStauts(clientPn, quantity);
        return n;
    }

    @Override
    public List<ToList> downloadOrder() {
        return gMterialsMapper.downloadOrder();
    }

    @Override
    public String checkQuantity(String uid) {

        Inventory inventory = gMterialsMapper.getInventoryInfo(uid);
        Float quantity = gMterialsMapper.checkQuantityByUid(uid) == null ? 0 : gMterialsMapper.checkQuantityByUid(uid);
        float quantity2 = quantity;
        if (inventory == null) {
            return "NA";
        } else if (quantity2 == inventory.getUid_no()) {
            return "Y";
        } else {
            return "N";
        }
    }

    @Override
    public String soldOut(String cpno, String role) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String returnMessage = "成品出库成功";
        Inventory inventory = gMterialsMapper.getInventoryInfo(cpno);
        // 多重if嵌套可拆分为多个单if  （卫语句）
        // UID不在库存直接停止执行
        if (inventory == null) {
            return returnMessage = "库存表未查询到相关成品信息";
        }
        // 判断UID是否在TO明细表（是否是备货单），不是则 执行替换拣料功能
        if (gMterialsMapper.checkTolistByUid(inventory.getUid().toString()) <= 0) {
            // 根据PN、PO、批次、数量、状态 判是否允许替换拣料 并获取被替换的备货单的UID
            String uid = gMterialsMapper.checkTolistInfo(inventory);
            if (uid == null || uid.equals(""))
                return returnMessage = "PO或批次或数量不符合要求，不能替换拣料";

            String recTime = gMterialsMapper.checkDate(uid);
            if (!recTime.substring(0, 10).equals(sdf.format(inventory.getProduction_date()).toString())) {
                if (!role.equals("manager"))
                    return returnMessage = "替换批次生产日期不相同，请切换管理员账号操作";
            }
            // System.out.println(recTime.substring(0,10) + sdf.format(inventory.getProduction_date()).toString());

            // 在原数据replace_uid字段添加替换的UID并更新状态为已拣货
            if (gMterialsMapper.insertAndUpdate(uid, inventory.getUid().toString()) <= 0)
                return returnMessage = "替换拣料后，TO明细表更新失败！";

            // 更新被替换的库存数据状态为未预留
            if (gMterialsMapper.updateInventoryStatus(uid, 1, 0) <= 0)
                return returnMessage = "更新TO明细表后，被替换库存数据更新失败！";

            // 更新替换的库存数据状态为已预留
            //if (gMterialsMapper.updateInventoryStatus(inventory.getUid().toString(), 1, 0) <= 0)
                //return returnMessage = "更新被替换库存数据后，替换库存数据更新失败！";

            // 直接在库存表删除替换的库存数据，将其放到库存下架表（即表示已预留且已下架，代替原来那条已预留的库存数据执行操作）
            if (gMterialsMapper.insertInventoryOut(inventory) <= 0)
                return returnMessage = "替换拣料成品下架失败1(下架表写入失败)";

            if (gMterialsMapper.deleteiinventoryByUid(inventory.getUid().toString()) <= 0)
                return returnMessage = "替换拣料成品下架失败2(库存表删除失败)";

            // 查询即存在已拣货和未拣货备货单（TO明细表）
            int n3 = gMterialsMapper.checkStauts();
            if (n3 > 0) {
                // 更新TO管理表对应备货单为拣货中
                gMterialsMapper.updateTosBHStatus2(cpno, 1);
            } else {
                gMterialsMapper.updateTosBHStatus2(cpno, 2);
            }

            return returnMessage = "替换拣料成功！";
        } else {
            // 将扫描的下架数据存到成品下架表
            int n = gMterialsMapper.insertInventoryOut(inventory);
            if (n <= 0) {
                return returnMessage = "成品下架失败1(下架表写入失败)";
            }
            // 库存表删除下架的成品信息
            int n1 = gMterialsMapper.deleteiinventoryByUid(cpno);
            if (n1 <= 0) {
                return returnMessage = "成品下架失败2(库存表删除失败)";
            }
            // 更新TO明细表对应备货单为已拣货
            int n2 = gMterialsMapper.updateTono(cpno);
            if (n2 <= 0) {
                return returnMessage = "成品下架失败2(TO明细表更新失败)";
            }
            // 查询即存在已拣货和未拣货备货单（TO明细表）
            int n3 = gMterialsMapper.checkStauts();
            if (n3 > 0) {
                // 更新TO管理表对应备货单为拣货中
                gMterialsMapper.updateTosBHStatus(cpno, 1);
            } else {
                gMterialsMapper.updateTosBHStatus(cpno, 2);
            }
        }

        return returnMessage;

    }

    public List<ShipmentInfoByhand> downloadShipmentinfoByhand() {

        return gMterialsMapper.downloadShipmentinfoByhand();
    }

    @Override
    public List<ShipmentInfoByhand> getShipmentInfo(String client) {
        return gMterialsMapper.getShipmentInfo(client);
    }

    @Override
    public int updateByid(int id) {

        return gMterialsMapper.updateByid(id);
    }

    @Override
    public int updateAllByid(String client) {
        return gMterialsMapper.updateAllByid(client);
    }

    @Override
    public List<Map<Integer, Integer>> getQty(String tono) {
        return gMterialsMapper.getQty(tono);
    }

    @Override
    public List<String> downloadShipmentNo2(String date) {
        return gMterialsMapper.downloadShipmentNo2(date);
    }

    @Override
    public List<Map<Integer, Integer>> getQuantity(String client) {
        return gMterialsMapper.getQuantity(client);
    }

    @Override
    public String updateToNo(String shipmintNO) throws javax.mail.MessagingException, IOException {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String startDate = simpleDateFormat.format(date);

        SAPUtil sapUtil = new SAPUtil();
        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS_1(startDate, startDate);
        // 存放船务/货仓确认信息
        List<FgShipmentInfo> list1 = new ArrayList<>();
        // 根据走货单统计出对应的数据
        List<FgShipmentInfo> list2 = new ArrayList<>();

        // 筛选船务确认走货信息
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLastComfirm() != null && (list.get(i).getLastComfirm().equals("船务") || list.get(i).getLastComfirm().equals("货仓"))) {

                list1.add(list.get(i));
                System.out.println(list.get(i).toString());
            }
        }
        // 对应走货单数据
        if (shipmintNO != null && !"".equals(shipmintNO)) {
            for (int i = 0;i < list1.size();i++) {
                if (shipmintNO.equals(list1.get(i).getShipmentNO().toString())) {
                    list2.add(list1.get(i));
                }
            }
        }

        // 记录是否有变更
        int n = 0;
        // 根据走货编号、PN、PO统计数量（与已产生备货单的做对比）
        long sumqty1 = 0l;
        long sumqty2 = 0l;
        List<Object[]> objectList = new ArrayList<>();
        Object[] objects = new Object[4];
        // 循环完一次，再次遇到相同pn、po的话数量已相等，因已更新了一次
        for (int i = 0; i < list2.size(); i++) {

            // 是否存在未拣货的备货单
            FgTosAndTosListDto fgTosAndTosListDto = gMterialsMapper.checkBHorQH(list2.get(i), 0);
            // 是否存在已拣货的备货单
            FgTosAndTosListDto fgTosAndTosListDto_1 = gMterialsMapper.checkBHorQH(list2.get(i), 2);
            // 查询是否存在欠货单
            FgTosAndTosListDto fgTosAndTosListDto2 = gMterialsMapper.checkBHorQH(list2.get(i), 3);
            // 关联库存表
            List<FgTosAndTosListDto> fgTosAndTosListDtos = gMterialsMapper.getTosAndTOListInfo(list2.get(i));
            // 该走货单是否存在对应PN、PO
            int count = gMterialsMapper.checkPnAndPo(list2.get(i));
            if (count == 0) {
                if (fgTosAndTosListDtos.size() == 0) {
                    // 库存没有对应PN、PO的UID数量，产生欠货单 （需邮件告知？）
                    FgTosAndTosListDto fgTosAndTosListDto1 = new FgTosAndTosListDto();
                    fgTosAndTosListDto1.setStatus(3);
                    fgTosAndTosListDto1.setQuantity(list2.get(i).getQuantity());
                    fgTosAndTosListDto1.setSap_qty(list2.get(i).getQuantity());
                    fgTosAndTosListDto1.setShipmentNO(list2.get(i).getShipmentNO().toString());
                    fgTosAndTosListDto1.setCarNo(list2.get(i).getCarNo().toString());
                    fgTosAndTosListDto1.setPlant(list2.get(i).getPlant().toString());
                    fgTosAndTosListDto1.setPn(list2.get(i).getSapPn().toString());
                    fgTosAndTosListDto1.setPo(list2.get(i).getPo().toString());

                    if (fgTosAndTosListDto2 == null) {
                        String QH = generateTo_No("欠货单");
                        fgTosAndTosListDto1.setTo_No(QH);
                        gMterialsMapper.insertTos(fgTosAndTosListDto1);
                        gMterialsMapper.insertToList(fgTosAndTosListDto1);
                    } else {
                        fgTosAndTosListDto1.setTo_No(fgTosAndTosListDto1.getTo_No().toString());
                        fgTosAndTosListDto1.setSap_qty(fgTosAndTosListDto1.getSap_qty() + list2.get(i).getQuantity());
                        gMterialsMapper.insertToList(fgTosAndTosListDto1);
                        gMterialsMapper.updateTosQuantity(fgTosAndTosListDto1);
                    }
                } else {
                    FgTosAndTosListDto fgTosAndTosListDto3 = new FgTosAndTosListDto();
                    String BH = fgTosAndTosListDto.getTo_No() == null ? "" : fgTosAndTosListDto.getTo_No().toString();
                    if (fgTosAndTosListDto == null) {
                        BH = generateTo_No("备货单");
                    }
                    fgTosAndTosListDto3.setTo_No(BH);
                    fgTosAndTosListDto3.setStatus(0);
                    fgTosAndTosListDto3.setQuantity(list2.get(i).getQuantity());
                    fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                    fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                    fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                    fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                    fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                    gMterialsMapper.insertTos(fgTosAndTosListDto3);

                    long sum_uidno = 0l;
                    for (int j = 0; j < fgTosAndTosListDtos.size(); j++) {
                        // 数量大于库存对应PN、PO的总数（产生备货单和欠货单）
                        if (list2.get(i).getQuantity() == fgTosAndTosListDtos.get(j).getSum_uidno()) {

                            fgTosAndTosListDto3.setTo_No(BH);
                            fgTosAndTosListDto3.setStatus(0);
                            fgTosAndTosListDto3.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
                            fgTosAndTosListDto3.setSap_qty(list2.get(i).getQuantity());
                            fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                            fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                            fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                            fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                            fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                            gMterialsMapper.insertToList(fgTosAndTosListDto3);
                        } else if (list2.get(i).getQuantity() > fgTosAndTosListDtos.get(j).getSum_uidno()) {
                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
                            if (sum_uidno > list2.get(i).getQuantity()) {
                                // 累加到大于临界值情况 提醒拆分该成品单后 减去多出的部分 生成欠货单（后续判断库存表是否有拆分的数据 再生成新备货单）
                                // 生成欠货单前判断TO管理是否已存在该走货单的欠货单，存在则直接在TO明细表生成欠货单，不存在则现在TO管理生成欠货单，再存到TO明细表
                                String QH = fgTosAndTosListDto2 == null ? "" : fgTosAndTosListDto2.getTo_No().toString();
                                // 欠货数量（即所需要备货的数量）
                                long qty = sum_uidno - list2.get(i).getQuantity();
                                if (fgTosAndTosListDto2 == null) {
                                    QH = generateTo_No("欠货单");
                                    // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
                                    fgTosAndTosListDto3.setTo_No(QH);
                                    fgTosAndTosListDto3.setStatus(3);
                                    fgTosAndTosListDto3.setQuantity(qty);
                                    fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                    fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                    fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                    fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                    fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                    gMterialsMapper.insertTos(fgTosAndTosListDto3);
                                }
                                // 插入TO明细
                                fgTosAndTosListDto3.setTo_No(QH);
                                fgTosAndTosListDto3.setStatus(2);
                                fgTosAndTosListDto3.setQuantity(qty);
                                fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                gMterialsMapper.insertToList(fgTosAndTosListDto3);

                            } else if (sum_uidno == list2.get(i).getQuantity()) {
                                // 累加到刚好等于的情况 则备货后直接break退出循环，后面的关联数据就不用进行备货了
                                fgTosAndTosListDto3.setTo_No(BH);
                                fgTosAndTosListDto3.setStatus(0);
                                fgTosAndTosListDto3.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
                                fgTosAndTosListDto3.setSap_qty(list2.get(i).getQuantity());
                                fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                gMterialsMapper.insertToList(fgTosAndTosListDto3);
                                gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                break;
                            } else {
                                // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
                                fgTosAndTosListDto3.setTo_No(BH);
                                fgTosAndTosListDto3.setStatus(0);
                                fgTosAndTosListDto3.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
                                fgTosAndTosListDto3.setSap_qty(list2.get(i).getQuantity());
                                fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                gMterialsMapper.insertToList(fgTosAndTosListDto3);
                                gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                            }
                        } else if (list2.get(i).getQuantity() < fgTosAndTosListDtos.get(j).getSum_uidno()) {
                            System.out.println("产生备货单3");
                            String QH = fgTosAndTosListDto2 == null ? "" : fgTosAndTosListDto2.getTo_No().toString();
                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
                            // 临界值为库存总数
                            if (sum_uidno == fgTosAndTosListDtos.get(j).getSum_uidno()) {
                                fgTosAndTosListDto3.setTo_No(BH);
                                fgTosAndTosListDto3.setStatus(0);
                                fgTosAndTosListDto3.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
                                fgTosAndTosListDto3.setSap_qty(list2.get(i).getQuantity());
                                fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                gMterialsMapper.insertToList(fgTosAndTosListDto3);
                                gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                                // 总数小于 批次总数则产生备货单（即剩下的数量为欠货数量）
                                if (fgTosAndTosListDto2 == null) {
                                    QH = generateTo_No("欠货单");
                                    fgTosAndTosListDto3.setTo_No(QH);
                                    fgTosAndTosListDto3.setStatus(3);
                                    fgTosAndTosListDto3.setQuantity(list2.get(i).getQuantity() - sum_uidno);
                                    fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                    fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                    fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                    fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                    fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                    gMterialsMapper.insertTos(fgTosAndTosListDto3);
                                }
                                long qty = list2.get(i).getQuantity() - sum_uidno;
                                fgTosAndTosListDto3.setTo_No(QH);
                                fgTosAndTosListDto3.setStatus(0);
                                fgTosAndTosListDto3.setQuantity(list2.get(i).getQuantity() - sum_uidno);
                                fgTosAndTosListDto3.setSap_qty(fgTosAndTosListDto2.getQuantity() + qty);
                                fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                gMterialsMapper.insertToList(fgTosAndTosListDto3);
                                // long sumqty = gMterialsMapper.getSumqty(list1.get(i).getShipmentNO().toString());
                                gMterialsMapper.updateTosQuantity(fgTosAndTosListDto3);
                            } else {
                                // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
                                fgTosAndTosListDto3.setTo_No(BH);
                                fgTosAndTosListDto3.setStatus(0);
                                fgTosAndTosListDto3.setQuantity(fgTosAndTosListDtos.get(j).getQuantity());
                                fgTosAndTosListDto3.setSap_qty(list2.get(i).getQuantity());
                                fgTosAndTosListDto3.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                fgTosAndTosListDto3.setCarNo(list2.get(i).getCarNo().toString());
                                fgTosAndTosListDto3.setPlant(list2.get(i).getPlant().toString());
                                fgTosAndTosListDto3.setPn(list2.get(i).getSapPn().toString());
                                fgTosAndTosListDto3.setPo(list2.get(i).getPo().toString());
                                gMterialsMapper.insertToList(fgTosAndTosListDto3);
                                gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
                            }
                        }
                    }
                }
            } else {
                sumqty1 = sumQty(list2, list2.get(i).getShipmentNO().toString(), list2.get(i).getSapPn().toString(), list2.get(i).getPo().toString());
                sumqty2 = gMterialsMapper.getsumQty(list2.get(i));
                String QH = fgTosAndTosListDto2 == null ? "" : fgTosAndTosListDto2.getTo_No().toString();
                String BH = fgTosAndTosListDto == null ? "" : fgTosAndTosListDto.getTo_No().toString();
                FgTosAndTosListDto fgTosAndTosListDto4 = new FgTosAndTosListDto();
                if (sumqty1 > sumqty2) {
                    long qty = sumqty1 - sumqty2;
                    if (fgTosAndTosListDtos.size() == 0) {
                        if (fgTosAndTosListDto2 == null) {
                            QH = generateTo_No("欠货单");
                        }
                        fgTosAndTosListDto4.setTo_No(QH);
                        fgTosAndTosListDto4.setStatus(3);
                        fgTosAndTosListDto4.setQuantity(qty);
                        fgTosAndTosListDto4.setShipmentNO(list2.get(i).getShipmentNO().toString());
                        fgTosAndTosListDto4.setCarNo(list2.get(i).getCarNo().toString());
                        fgTosAndTosListDto4.setPlant(list2.get(i).getPlant().toString());
                        fgTosAndTosListDto4.setPn(list2.get(i).getSapPn().toString());
                        fgTosAndTosListDto4.setPo(list2.get(i).getPo().toString());
                        gMterialsMapper.insertTos(fgTosAndTosListDto4);

                        fgTosAndTosListDto4.setSap_qty(qty);
                        fgTosAndTosListDto4.setStatus(2);
                        gMterialsMapper.insertToList(fgTosAndTosListDto4);
                        gMterialsMapper.updateTosQuantity(fgTosAndTosListDto4);
                    } else {
                        if (fgTosAndTosListDto == null) {
                            BH = generateTo_No("备货单");
                        }
                        fgTosAndTosListDto4.setTo_No(BH);
                        fgTosAndTosListDto4.setStatus(0);
                        fgTosAndTosListDto4.setQuantity(list2.get(i).getQuantity());
                        fgTosAndTosListDto4.setShipmentNO(list2.get(i).getShipmentNO().toString());
                        fgTosAndTosListDto4.setCarNo(list2.get(i).getCarNo().toString());
                        fgTosAndTosListDto4.setPlant(list2.get(i).getPlant().toString());
                        fgTosAndTosListDto4.setPn(list2.get(i).getSapPn().toString());
                        fgTosAndTosListDto4.setPo(list2.get(i).getPo().toString());
                        gMterialsMapper.insertTos(fgTosAndTosListDto4);

                        long sum_uidno = 0l;
                        for (int k = 0; k < fgTosAndTosListDtos.size(); k++) {
                            // 数量大于库存对应PN、PO的总数（产生备货单和欠货单）
                            if (qty == fgTosAndTosListDtos.get(k).getSum_uidno()) {

                                // 注意数量和批量
                                fgTosAndTosListDto4.setQuantity(fgTosAndTosListDtos.get(k).getQuantity());
                                fgTosAndTosListDto4.setSap_qty(list2.get(i).getQuantity());
                                gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(k).getUid().toString());
                            } else if (qty > fgTosAndTosListDtos.get(k).getSum_uidno()) {
                                sum_uidno += fgTosAndTosListDtos.get(k).getQuantity();
                                if (sum_uidno == fgTosAndTosListDtos.get(k).getSum_uidno()) {

                                    fgTosAndTosListDto4.setTo_No(BH);
                                    fgTosAndTosListDto4.setStatus(0);
                                    fgTosAndTosListDto4.setQuantity(fgTosAndTosListDtos.get(k).getQuantity());
                                    fgTosAndTosListDto4.setSap_qty(list2.get(i).getQuantity());
                                    fgTosAndTosListDto4.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                    fgTosAndTosListDto4.setCarNo(list2.get(i).getCarNo().toString());
                                    fgTosAndTosListDto4.setPlant(list2.get(i).getPlant().toString());
                                    fgTosAndTosListDto4.setPn(list2.get(i).getSapPn().toString());
                                    fgTosAndTosListDto4.setPo(list2.get(i).getPo().toString());
                                    gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                    gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(k).getUid().toString());
                                    // 欠货数量（即所需要备货的数量）
                                    long qty2 = sum_uidno - qty;
                                    if (fgTosAndTosListDto2 == null) {
                                        QH = generateTo_No("欠货单");
                                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
                                        fgTosAndTosListDto4.setTo_No(QH);
                                        fgTosAndTosListDto4.setStatus(3);
                                        fgTosAndTosListDto4.setQuantity(qty2);
                                        gMterialsMapper.insertTos(fgTosAndTosListDto4);
                                    }
                                    // 插入TO明细
                                    fgTosAndTosListDto4.setTo_No(QH);
                                    fgTosAndTosListDto4.setStatus(2);
                                    fgTosAndTosListDto4.setQuantity(qty2);
                                    fgTosAndTosListDto4.setSap_qty(fgTosAndTosListDto2.getQuantity() == null ? 0l : fgTosAndTosListDto2.getQuantity() + qty2);
                                    gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                    gMterialsMapper.updateTosQuantity(fgTosAndTosListDto4);
                                } else {
                                    // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
                                    fgTosAndTosListDto4.setTo_No(BH);
                                    fgTosAndTosListDto4.setStatus(0);
                                    fgTosAndTosListDto4.setQuantity(fgTosAndTosListDtos.get(k).getQuantity());
                                    fgTosAndTosListDto4.setSap_qty(list2.get(i).getQuantity());
                                    fgTosAndTosListDto4.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                    fgTosAndTosListDto4.setCarNo(list2.get(i).getCarNo().toString());
                                    fgTosAndTosListDto4.setPlant(list2.get(i).getPlant().toString());
                                    fgTosAndTosListDto4.setPn(list2.get(i).getSapPn().toString());
                                    fgTosAndTosListDto4.setPo(list2.get(i).getPo().toString());
                                    gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                    gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(k).getUid().toString());
                                }
                            } else if (qty < fgTosAndTosListDtos.get(k).getSum_uidno()) {
                                System.out.println("产生备货单3");
                                QH = fgTosAndTosListDto2 == null ? "" : fgTosAndTosListDto2.getTo_No().toString();
                                sum_uidno += fgTosAndTosListDtos.get(k).getQuantity();

                                if (sum_uidno > qty) {
                                    // 累加到大于临界值情况 提醒拆分该成品单后 减去多出的部分 生成欠货单（后续判断库存表是否有拆分的数据 再生成新备货单）
                                    // 生成欠货单前判断TO管理是否已存在该走货单的欠货单，存在则直接在TO明细表生成欠货单，不存在则现在TO管理生成欠货单，再存到TO明细表
                                    //String QH = fgTosAndTosListDto2 == null ? "" : fgTosAndTosListDto2.getTo_No().toString();
                                    // 欠货数量（即所需要备货的数量）
                                    long qty2 = sum_uidno - qty;
                                    if (fgTosAndTosListDto2 == null) {
                                        QH = generateTo_No("欠货单");
                                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
                                        fgTosAndTosListDto4.setTo_No(QH);
                                        fgTosAndTosListDto4.setStatus(3);
                                        fgTosAndTosListDto4.setQuantity(qty2);
                                        gMterialsMapper.insertTos(fgTosAndTosListDto4);
                                    }
                                    // 插入TO明细
                                    fgTosAndTosListDto4.setTo_No(QH);
                                    fgTosAndTosListDto4.setStatus(2);
                                    fgTosAndTosListDto4.setQuantity(qty2);
                                    fgTosAndTosListDto4.setSap_qty(fgTosAndTosListDto2.getQuantity() == null ? 0l : fgTosAndTosListDto2.getQuantity() + qty2);
                                    gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                    gMterialsMapper.updateTosQuantity(fgTosAndTosListDto4);

                                } else if (sum_uidno == qty) {
                                    // 累加到刚好等于的情况 则备货后直接break退出循环，后面的关联数据就不用进行备货了
                                    fgTosAndTosListDto4.setTo_No(BH);
                                    fgTosAndTosListDto4.setStatus(0);
                                    fgTosAndTosListDto4.setQuantity(fgTosAndTosListDtos.get(k).getQuantity());
                                    fgTosAndTosListDto4.setSap_qty(list2.get(i).getQuantity());
                                    fgTosAndTosListDto4.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                    fgTosAndTosListDto4.setCarNo(list2.get(i).getCarNo().toString());
                                    fgTosAndTosListDto4.setPlant(list2.get(i).getPlant().toString());
                                    fgTosAndTosListDto4.setPn(list2.get(i).getSapPn().toString());
                                    fgTosAndTosListDto4.setPo(list2.get(i).getPo().toString());
                                    gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                    gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(k).getUid().toString());
                                    break;
                                } else {
                                    // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
                                    fgTosAndTosListDto4.setTo_No(BH);
                                    fgTosAndTosListDto4.setStatus(0);
                                    fgTosAndTosListDto4.setQuantity(fgTosAndTosListDtos.get(k).getQuantity());
                                    fgTosAndTosListDto4.setSap_qty(list2.get(i).getQuantity());
                                    fgTosAndTosListDto4.setShipmentNO(list2.get(i).getShipmentNO().toString());
                                    fgTosAndTosListDto4.setCarNo(list2.get(i).getCarNo().toString());
                                    fgTosAndTosListDto4.setPlant(list2.get(i).getPlant().toString());
                                    fgTosAndTosListDto4.setPn(list2.get(i).getSapPn().toString());
                                    fgTosAndTosListDto4.setPo(list2.get(i).getPo().toString());
                                    gMterialsMapper.insertToList(fgTosAndTosListDto4);
                                    gMterialsMapper.updateStatusByUid(fgTosAndTosListDtos.get(k).getUid().toString());
                                    gMterialsMapper.updateTosListQuantity(fgTosAndTosListDto4.getTo_No().toString(), fgTosAndTosListDto4.getSap_qty());
                                }
                            }
                        }
                    }
                } else if (sumqty1 < sumqty2) {
                    long qty = sumqty2 - sumqty1;
                    // 是否未拣货（未拣货、拣货中、拣完货）
                    if (fgTosAndTosListDto == null) {
                        // 是否拣完或
                        if (fgTosAndTosListDto_1 == null) {
                            // 拣货中，不允许更新备货清单
                        } else {
                            // 查找是否存在于缺少数量相等的已预留的备货单UID
                            ToList toList1 = gMterialsMapper.checkEqualQuantity(list2.get(i), qty, 1);
                            // 大于
                            ToList toList2 = gMterialsMapper.checkEqualQuantity2(list2.get(i), qty, 1);
                            // 小于
                            // ToList toList3 = gMterialsMapper.checkEqualQuantity3(list2.get(i), qty, 1);
                            if (toList1 != null) {
                                // 邮件提醒
                                objects[0] = fgTosAndTosListDto_1.getShipmentNO().toString();
                                objects[1] = fgTosAndTosListDto_1.getTo_No().toString();
                                objects[2] = toList2.getUid().toString();
                                objects[3] = 0l;
                                objectList.add(objects);
                                // sendMail(fgTosAndTosListDto_1.getShipmentNO().toString(), fgTosAndTosListDto_1.getTo_No().toString(), toList1.getUid().toString(), 0l);
                                fgTosAndTosListDto.setSap_qty(fgTosAndTosListDto.getQuantity() - qty);
                                gMterialsMapper.updateTosQuantity(fgTosAndTosListDto);
                                gMterialsMapper.updateTosListQuantity(fgTosAndTosListDto.getTo_No().toString(), fgTosAndTosListDto.getSap_qty());
                                // 设置状态为待回仓
                                gMterialsMapper.updateStatusTos(fgTosAndTosListDto_1.getTo_No().toString());
                            } else if (toList2 != null) {
                                // 查找大于缺少数量的已预留q且已拣货的备货单UID(邮件通知退回？并做拆分UID松祚？)
                                // 退回数量（拆分数量）
                                long qty2 = toList2.getQuantity() - qty;
                                objects[0] = fgTosAndTosListDto_1.getShipmentNO().toString();
                                objects[1] = fgTosAndTosListDto_1.getTo_No().toString();
                                objects[2] = toList2.getUid().toString();
                                objects[3] = qty2;
                                objectList.add(objects);
                                // sendMail(fgTosAndTosListDto_1.getShipmentNO().toString(), fgTosAndTosListDto_1.getTo_No().toString(), toList2.getUid().toString(), qty2);
                            } else {
                                List<Object[]> objectList1 = lessThan(list2.get(i), fgTosAndTosListDto_1, qty);
                                objectList = objectList1;
                            }
                        }
                    } else {
                        // 查找是否存在于缺少数量相等的已预留的备货单UID(未拣货)
                        ToList toList1 = gMterialsMapper.checkEqualQuantity(list2.get(i), qty, 0);
                        ToList toList2 = gMterialsMapper.checkEqualQuantity2(list2.get(i), qty, 0);
                        if (toList1 != null) {
                            // 根据UID去库存下架表查询已预留UID重新放到库存表，表示在库（未预留）
                            Inventory inventory = gMterialsMapper.getInventoryInfo2(toList1.getUid().toString());
                            gMterialsMapper.insertInventory(inventory);
                            gMterialsMapper.deleteToListtoNo2(toList1.getUid().toString());
                            fgTosAndTosListDto.setSap_qty(fgTosAndTosListDto.getQuantity() - qty);
                            gMterialsMapper.updateTosQuantity(fgTosAndTosListDto);
                            gMterialsMapper.updateTosListQuantity(fgTosAndTosListDto.getTo_No().toString(), fgTosAndTosListDto.getSap_qty());
                        } else if (toList2 != null) {
                            // 未拣货 -- 找到TO明细UID，根据差量拆分该UID成品单UID数量，更新原成品单数量，原库存UID数量，新生成的成品单插到库存表，更新TO明细原UID数量，更新原UID对应备货单数量
                            long qty2 = toList2.getQuantity() - qty;
                            // 拆分成品单
                            FgChecklistController fgChecklistController = new FgChecklistController();
                            FgChecklist fgChecklist = fgChecklistController.SpiltFg2(toList2.getUid().toString(), qty2);
                            if (fgChecklist.getOldUid().toString().equals(toList2.getUid().toString())) {
                                Inventory inventory = new Inventory();
                                inventory.setUid(fgChecklist.getUid().toString());
                                inventory.setPn(fgChecklist.getPn().toString());
                                inventory.setPo(fgChecklist.getPo().toString());
                                inventory.setStock(toList2.getStock().toString());
                                inventory.setBatch(fgChecklist.getBatch().toString());
                                inventory.setUid_no(fgChecklist.getUidNo());
                                inventory.setStatus(1);
                                inventory.setWo(fgChecklist.getWo().toString());
                                inventory.setProduction_date(fgChecklist.getProductionDate());
                                inventory.setQa_sign(fgChecklist.getQaSign());
                                inventory.setRollbackReason("走货资料变更");
                                // 将拆分的UID存到库存表
                                bindStockMapper.toinsert(inventory);
                                // 更新Tolist对应UID数量（拆分前UID）
                                gMterialsMapper.updateTosListQuantity2(toList2.getUid().toString(), toList2.getQuantity() - qty2);
                                fgTosAndTosListDto.setSap_qty(fgTosAndTosListDto.getQuantity() - qty2);
                                // 更新TOs数量（减去拆分的）
                                gMterialsMapper.updateTosQuantity(fgTosAndTosListDto);
                                // 更新TOlist批量（减去查分的）
                                gMterialsMapper.updateTosListQuantity(fgTosAndTosListDto.getTo_No().toString(), fgTosAndTosListDto.getSap_qty());
                            }
                            // sendMail(fgTosAndTosListDto.getShipmentNO().toString(), fgTosAndTosListDto.getTo_No().toString(), uid2, 0l);
                        } else {
                            lessThan2(list2.get(i), fgTosAndTosListDto, qty);
                        }
                    }
                }
            }
        }

//        if (list2.size() == 0) {
//            System.out.println("走货日期变更");
//            // 更新走货信息表对应数据（删掉重新插入？）
//
//            int deleteShipmentNo = gMterialsMapper.deleteShipmentNo(list2.get(0).getShipmentNO().toString());
//            if (deleteShipmentNo > 0) {
//                gMterialsMapper.insertShipmentInfo(list2);
//            }
//        } else {
//            if (objectList.size() > 0) {
//                System.out.println("数量变更");
//                // 退回邮件
//                sendMail(objectList);
//            }
//            // 更新走货信息表对应数据（删掉重新插入？）
//            int deleteShipmentNo = gMterialsMapper.deleteShipmentNo(list2.get(0).getShipmentNO().toString());
//            if (deleteShipmentNo > 0) {
//                gMterialsMapper.insertShipmentInfo(list2);
//            }
//        }


        return null;
    }

    /**
     * 已拣货 且备货数量小于缺少数量（需累加的情况）
     * */
    public List<Object[]> lessThan(FgShipmentInfo fgShipmentInfo, FgTosAndTosListDto fgTosAndTosListDto, long qty) {

        List<Object[]> objectList = new ArrayList<>();
        Object[] objects = new Object[4];
        // 小于
        List<ToList> toList3 = gMterialsMapper.checkEqualQuantity3(fgShipmentInfo, qty, 1);
        long sum = 0l;
        for (int i = 0;i < toList3.size();i++) {
            sum += toList3.get(i).getQuantity();
            objects[0] = fgTosAndTosListDto.getShipmentNO().toString();
            objects[1] = fgTosAndTosListDto.getTo_No().toString();
            objects[2] = toList3.get(i).getUid().toString();
            objects[3] = toList3.get(i).getQuantity();
            if (sum == qty) {
                objectList.add(objects);
                break;
            } else if (sum > qty) {
                long qty2 = sum - qty;
                objects[3] = toList3.get(i).getQuantity() - qty2;
                objectList.add(objects);
                break;
            } else {
                objectList.add(objects);
            }
        }
        return objectList;
    }

    /**
     * 未拣货 且备货数量小于缺少数量（需累加的情况）
     * */
    public void lessThan2(FgShipmentInfo fgShipmentInfo, FgTosAndTosListDto fgTosAndTosListDto, long qty) {

        // 小于
        List<ToList> toList3 = gMterialsMapper.checkEqualQuantity3(fgShipmentInfo, qty, 0);
        List<ToList> list = new ArrayList<>();
        long sum = 0l;
        for (int i = 0;i < toList3.size();i++) {
            sum += toList3.get(i).getQuantity();
            if (sum == qty) {
                list.add(toList3.get(i));
                break;
            } else if (sum > qty) {
                // 需拆分数量
                long qty2 = sum - qty;
                // 拆分成品单
                FgChecklistController fgChecklistController = new FgChecklistController();
                FgChecklist fgChecklist = fgChecklistController.SpiltFg2(toList3.get(i).getUid().toString(), qty2);
                if (fgChecklist.getOldUid().toString().equals(toList3.get(i).getUid().toString())) {
                    Inventory inventory = new Inventory();
                    inventory.setUid(fgChecklist.getUid().toString());
                    inventory.setPn(fgChecklist.getPn().toString());
                    inventory.setPo(fgChecklist.getPo().toString());
                    inventory.setStock(toList3.get(i).getStock().toString());
                    inventory.setBatch(fgChecklist.getBatch().toString());
                    inventory.setUid_no(fgChecklist.getUidNo());
                    inventory.setStatus(1);
                    inventory.setWo(fgChecklist.getWo().toString());
                    inventory.setProduction_date(fgChecklist.getProductionDate());
                    inventory.setQa_sign(fgChecklist.getQaSign());
                    inventory.setRollbackReason("走货资料变更");
                    bindStockMapper.toinsert(inventory);
                    // 更新单个TOlist的数量（拆了后剩余数量）
                    gMterialsMapper.updateTosListQuantity2(toList3.get(i).getUid().toString(), toList3.get(i).getQuantity() - qty2);
                    fgTosAndTosListDto.setSap_qty(fgTosAndTosListDto.getQuantity() - qty2);
                    gMterialsMapper.updateTosQuantity(fgTosAndTosListDto);
                    gMterialsMapper.updateTosListQuantity(fgTosAndTosListDto.getTo_No().toString(), fgTosAndTosListDto.getSap_qty());
                    break;
                }
            } else {
                list.add(toList3.get(i));
            }
        }
        // 统计需删除的备货单的数量, 查找下架表数据重新存到库存表
        List<Inventory> inventories = new ArrayList<>();
        long sum1 = 0l;
        for (int i = 0;i < list.size();i++) {
            sum1 += list.get(i).getQuantity();
            Inventory inventory = gMterialsMapper.getInventoryInfo2(list.get(i).getUid().toString());
            inventories.add(inventory);
        }
        // 批量插入库存表
        int n = gMterialsMapper.insertInventory2(inventories);
        if (n > 0) {
            // 批量删除已预留的UID（需退回，因未拣货，所以直接删除）
            gMterialsMapper.deleteToListtoNo3(toList3);
        }
        // 查询实际备货数量 用于更新TOS数量和Tolist批量
        long sum_quantity = gMterialsMapper.getQuantitySum(toList3.get(0).getToNo().toString());
        FgTosAndTosListDto fgTosAndTosListDto1 = new FgTosAndTosListDto();
        fgTosAndTosListDto1.setSap_qty(sum_quantity);
        fgTosAndTosListDto1.setTo_No(toList3.get(0).getToNo().toString());
        gMterialsMapper.updateTosQuantity(fgTosAndTosListDto);
        gMterialsMapper.updateTosListQuantity(fgTosAndTosListDto.getTo_No().toString(), fgTosAndTosListDto.getSap_qty());

    }


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
                // FG + BH + 年月日时分秒 毫秒
                To_No = "BH" + simpleDateFormat.format(date);
                System.out.println(To_No);
            } else if (tono.equals("欠货单")) {
                // FG + QH + 年月日时分秒 毫秒
                To_No = "QH" + simpleDateFormat.format(date);
                System.out.println(To_No);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return To_No;

    }

//
//    @Override
//    public String updateToNo() {
//
//        Date date = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String startDate = simpleDateFormat.format(date);
//
//        SAPUtil sapUtil = new SAPUtil();
//        List<FgShipmentInfo> list = sapUtil.Z_HTMES_ZSDSHIPLS_1(startDate, startDate);
//        // 存放船务确认信息
//        List<FgShipmentInfo> list1 = new ArrayList<>();
//        // 根据走货单统计出对应的数据
//        List<FgShipmentInfo> list2 = new ArrayList<>();
//
//        // 筛选船务确认走货信息
//        for (int i = 0;i < list.size();i++) {
//            if (list.get(i).getLastComfirm() != null && list.get(i).getLastComfirm().equals("船务")) {
//
//                list1.add(list.get(i));
//                System.out.println(list.get(i).toString());
//            }
//        }
//        // 记录是否有变更
//        int n = 0;
//        // 根据走货编号、PN、PO统计数量（与已产生备货单的做对比）
//        long sumqty1 = 0l;
//        long sumqty2 = 0l;
//        // 循环完一次，再次遇到相同pn、po的话数量已相等，因已更新了一次
//        for (int i = 0;i < list1.size();i++) {
//            sumqty1 = sumQty(list1, list1.get(i).getShipmentNO().toString(), list1.get(i).getSapPn().toString(), list1.get(i).getPo().toString());
//            sumqty2 = gMterialsMapper.getsumQty(list1.get(i));
//            // 该走货单没有产生TO则直接跳过
//            int count1 = gMterialsMapper.checkGenerate(list1.get(i));
//            // 判断是否是存在走货单，但不存在该PN、PO的
//            int count2 = gMterialsMapper.checkPnAndPo(list1.get(i));
//            if (count2 > 0) {
//                if (count1 == 0) {
//                    continue;
//                }
//            } else {
//                continue;
//            }
//
//            // 获取拣货状态
//            String toNo = gMterialsMapper.checkStauts(list1.get(i)) == null ? "" : gMterialsMapper.checkStauts(list1.get(i));
//            long quantity = 0l;
//            if (sumqty1 > sumqty2) {
//                quantity = sumqty1 - sumqty2;
//                // 未拣货情况
//                if (!"".equals(toNo)) {
//                    // 查找存在的备货单/欠货单进行删除操作
//                    List<String> strings = gMterialsMapper.checktoNo(list1.get(i));
//                    // 删除对应TO明细、对应To管理、走货信息
//                    int deleteToListtoNo = gMterialsMapper.deleteToListtoNo(strings);
//                    int deleteTostoNo = gMterialsMapper.deleteTostoNo(list1.get(i).getShipmentNO().toString());
//                    int deleteFgShipmentInfo = gMterialsMapper.deleteFgShipmentInfo(list1.get(i).getShipmentNO().toString());
//                    // 重新插入对应走货信息 并 新生成 To单
//                    list2 = ListByShipmentNo(list1, list1.get(i).getShipmentNO().toString());
//                    int insertShipmentInfo = gMterialsMapper.insertShipmentInfo(list2);
//                    List<FgShipmentInfo> list3 = ListByPNAndPO(list2);
//                    for (int j = 0;j < list3.size();j++) {
//                        List<FgTosAndTosListDto> fgTosAndTosListDtos = gMterialsMapper.getTosAndTOListInfo(list3.get(j));
//
//                    }
//                }
//            } else if (sumqty1 < sumqty2) {
//                quantity = sumqty2 - sumqty1;
//                if (!"".equals(toNo)) {
//                    // 删除对应走货信息、对应To管理、TO明细
//                    // 重新插入对应走货信息 并 新生成 To单
//                }
//            } else {
//                n += 1;
//            }
//        }
//        if (n == list1.size()) {
//            // 没有走货单变更
//        } else {
//            // 备货清单已更新
//        }
//
//        return null;
//    }

    /**
     * 统计数量
     * */
    public long sumQty(List<FgShipmentInfo> fgShipmentInfos, String shipmentNo, String pn, String  po) {

        long sumqty = 0l;
        for (int i = 0; i < fgShipmentInfos.size(); i++) {
            if (fgShipmentInfos.get(i).getShipmentNO().toString().equals(shipmentNo) && fgShipmentInfos.get(i).getSapPn().toString().equals(pn) &&
                    fgShipmentInfos.get(i).getPo().toString().equals(po)) {
                sumqty += fgShipmentInfos.get(i).getQuantity();

            }
        }
        return sumqty;

    }

    /**
     * 统计走货单对应数据信息
     * */
    public List<FgShipmentInfo> ListByShipmentNo(List<FgShipmentInfo> fgShipmentInfos, String shipmentNo) {

        List<FgShipmentInfo> fgShipmentInfoList = new ArrayList<>();
        for (int i = 0; i < fgShipmentInfos.size(); i++) {
            if (fgShipmentInfos.get(i).getShipmentNO().toString().equals(shipmentNo)) {

                fgShipmentInfoList.add(fgShipmentInfos.get(i));
            }
        }
        return fgShipmentInfoList;

    }

    /**
     * 同一个PN、PO的数量累加作为一条数据（集合数据都为同一个走货单）
     * */
    public List<FgShipmentInfo> ListByPNAndPO(List<FgShipmentInfo> fgShipmentInfos) {

        List<FgShipmentInfo> fgShipmentInfoList = new ArrayList<>();
        // 设置原集合第一个元素为新集合元素
        fgShipmentInfoList.add(fgShipmentInfos.get(0));
        // 原集合从第二个元素开始循环
        for (int i = 1; i < fgShipmentInfos.size(); i++) {
            // 标识是否存在相等PN、PO
            boolean flag = false;
            // 原集合元素逐个遍历新集合元素
            for (int j = 0;j < fgShipmentInfoList.size();j++) {

                // 只要存在相等则累加数量，flag表示设为TRUE，退出循环，从原集合下一个元素开始新循环
                if (fgShipmentInfos.get(i).getSapPn().toString().equals(fgShipmentInfoList.get(j).getSapPn().toString()) &&
                        fgShipmentInfos.get(i).getPo().toString().equals(fgShipmentInfoList.get(j).getPo().toString())) {
                    fgShipmentInfoList.get(j).setQuantity(fgShipmentInfos.get(i).getQuantity() + fgShipmentInfoList.get(j).getQuantity());
                    flag = true;
                    break;
                }
            }
            // 不存在PN、PO相等 则把该元素 数据添加到新集合
            if (flag == false) {
                fgShipmentInfoList.add(fgShipmentInfos.get(i));
            }
        }
        return fgShipmentInfoList;

    }

    /**
     * 邮件
     * */
    public void sendMail(List<Object[]> objectList) throws MessagingException, javax.mail.MessagingException, IOException {

        try {
            System.out.println("进入");

            //1. 创建一个工作簿对象
            XSSFWorkbook workbook2 = new XSSFWorkbook();
            //2. 创建一个工作表对象
            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
            // 设置表头
            String[] headers = {"走货单", "备货单", "成品UID", "退回数量"};
            XSSFRow headerRow = sheet2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 在表格中写入数据
            int rowCount = 1;
            for (int i = 0;i < objectList.size();i++) {
                Object[] objects = objectList.get(i);
                XSSFRow row = sheet2.createRow(rowCount++);
                for (int j = 0; j < objects.length; j++) {
                    Cell cell = row.createCell(i);
                    if (i == 3) {
                        cell.setCellValue((long) objects[i]);
                    } else {
                        cell.setCellValue((String) objects[i]);
                    }
                }
            }
            // 自适应宽度
            for (int i = 0; i < headers.length; i++) {
                sheet2.autoSizeColumn(i);
            }
            //4. 创建文件并保存
            File file = new File("sendBack.xlsx");
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
            helper2.setSubject("SAP批量减少，需退回的成品");
            helper2.setText("请查收附件，谢谢！", true);
            // MimeUtility.encodeWord 解决附件名和格式乱码问题
            helper2.addAttachment(MimeUtility.encodeWord("sendBack.xlsx"), file);
            //7. 发送邮件
            javaMailSender.send(message2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void sendMail(String shipmentNo, String toNo, String uid, long qty) throws MessagingException, javax.mail.MessagingException, IOException {
//
//        try {
//            System.out.println("进入");
//            List<String> contentList = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                contentList.add(shipmentNo + "," + toNo + "," + uid);
//            }
//
//            //1. 创建一个工作簿对象
//            XSSFWorkbook workbook2 = new XSSFWorkbook();
//            //2. 创建一个工作表对象
//            XSSFSheet sheet2 = workbook2.createSheet("Sheet1");
//            // 设置表头
//            String[] headers = {"走货单", "备货单", "成品UID", "退回数量"};
//            XSSFRow headerRow = sheet2.createRow(0);
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//            }
//            // 在表格中写入数据
//            int rowCount = 1;
//            for (String content : contentList) {
//                String[] contents = content.split(",");
//                XSSFRow row = sheet2.createRow(rowCount++);
//                for (int i = 0; i < contents.length; i++) {
//                    Cell cell = row.createCell(i);
//                    cell.setCellValue(contents[i]);
//                }
//            }
//            // 自适应宽度
//            for (int i = 0; i < headers.length; i++) {
//                sheet2.autoSizeColumn(i);
//            }
//            //4. 创建文件并保存
//            File file = new File("sendBack.xlsx");
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
//            helper2.setSubject("SAP批量减少，需退回的成品");
//            helper2.setText("请查收附件，谢谢！", true);
//            // MimeUtility.encodeWord 解决附件名和格式乱码问题
//            helper2.addAttachment(MimeUtility.encodeWord("sendBack.xlsx"), file);
//            //7. 发送邮件
//            javaMailSender.send(message2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
