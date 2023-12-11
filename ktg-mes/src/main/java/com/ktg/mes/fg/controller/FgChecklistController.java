package com.ktg.mes.fg.controller;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import cn.hutool.core.bean.BeanUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.ktg.mes.fg.domain.*;
import com.ktg.mes.fg.mapper.FgChecklistMapper;
import com.ktg.mes.fg.mapper.FgInventoryMapper;
import com.ktg.mes.fg.mapper.FgTosMapper;
import com.ktg.mes.fg.service.FgInventoryService;
import com.ktg.mes.fg.utils.SAPUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.enums.BusinessType;
import com.ktg.mes.fg.service.IFgChecklistService;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.common.core.page.TableDataInfo;

/**
 * 成品送检单Controller
 *
 * @author JiangTingming
 * @date 2023-03-18 start
 */
@EnableScheduling
@RestController
@RequestMapping("/fg/checklist")
public class FgChecklistController extends BaseController {
    private static volatile int Guid = 100;
    @Autowired
    private IFgChecklistService fgChecklistService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    FgChecklistMapper fgChecklistMapper;

    @Autowired
    FgInventoryService fgInventoryService;

    @Autowired
    FgInventoryMapper fgInventoryMapper;

    @Autowired
    FgTosMapper fgTosMapper;

    /**
     * 查询成品送检单列表
     */
    @PreAuthorize("@ss.hasPermi('fg:checklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(FgChecklist fgChecklist) {
        startPage();
        List<FgChecklist> list = fgChecklistService.selectFgChecklistList(fgChecklist);
        return getDataTable(list);
    }

    /**
     * 导出成品送检单列表
     */
    @PreAuthorize("@ss.hasPermi('fg:checklist:export')")
    @Log(title = "成品送检单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FgChecklist fgChecklist) {
        List<FgChecklist> list = fgChecklistService.selectFgChecklistList(fgChecklist);
        ExcelUtil<FgChecklist> util = new ExcelUtil<FgChecklist>(FgChecklist.class);
        util.exportExcel(response, list, "成品送检单数据");
    }

    /**
     * 获取成品送检单详细信息
     */
    @PreAuthorize("@ss.hasPermi('fg:checklist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(fgChecklistService.selectFgChecklistById(id));
    }

    /**
     * 新增成品送检单（目前该功能用不上，检查PO已有单独方法）
     */
    @PreAuthorize("@ss.hasPermi('fg:checklist:add')")
    @Log(title = "成品送检单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FgChecklist fgChecklist) {

        SAPUtil sapUtil = new SAPUtil();
        if (fgChecklist.getPn().toString().substring(0, 3).equals("650")) {

            System.out.println("ok");
            // 打印650型号送检单时查询SAP BOM 层次中对应的660型号，然后将650送检单自动关联至660型号送检单中
            String pn_660 = sapUtil.get660Info(fgChecklist.getPn().toString(), fgChecklist.getPlant().toString());
            if (pn_660.equals("")) {
                return AjaxResult.error("该650型号没有找到相关连的660型号");
            }
            fgChecklist.setPn660(pn_660);
            // 查询SAP中是否有该PO（查询的返回结果只要有PO即可保存）
            List<String[]> list = sapUtil.getPoInfo(fgChecklist.getPn().toString());
            int n = list.size();
            int i = 0;
            for (String[] arr : list) {
                // 数组转为集合，使用contains方法判断是否存在前端输入的PO号
                if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                    break;
                }
                i += 1;
            }
            if (n == i) {
                // 遍历完list中的String数组，没有PO则返回异常至前端页面提示
                return AjaxResult.error("PO未查询到");
            } else {
                // 生成新UID
                /*String uuid = UUID.randomUUID().toString().replace("-", "");
                // 小写转大写 （大写转小写toLowerCase）
                String uid = uuid.toUpperCase();
                fgChecklist.setUid(uuid);*/
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);

                return toAjax(fgChecklistService.insertFgChecklist(fgChecklist));
            }
        } else {
            // 660开头的PN直接查询SAP中是否有该PO（查询的返回结果只要有PO即可保存）
            List<String[]> list = sapUtil.getPoInfo(fgChecklist.getPn().toString());
            int n = list.size();
            int i = 0;
            for (String[] arr : list) {
                if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                    break;
                }
                i += 1;
            }
            if (n == i) {
                return AjaxResult.error("PO未查询到");
            } else {
                // 生成新UID
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码前UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);

                return toAjax(fgChecklistService.insertFgChecklist(fgChecklist));
            }
        }
    }

    /**
     * 修改成品送检单
     */
    @PreAuthorize("@ss.hasPermi('fg:checklist:edit')")
    @Log(title = "成品送检单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FgChecklist fgChecklist) {
        //String returnMessage = fgChecklistService.checkBarcodeUnique(fgChecklist);
        String path = fgChecklistService.generateBarcode(fgChecklist);
        System.out.println("path:" + path);
        fgChecklist.setBarcodeUrl(path);

        return toAjax(fgChecklistService.updateFgChecklist(fgChecklist));
    }

    /**
     * 删除成品送检单
     */
    @PreAuthorize("@ss.hasPermi('fg:checklist:remove')")
    @Log(title = "成品送检单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(fgChecklistService.deleteFgChecklistByIds(ids));
    }

    /**
     * 模糊查询型号pn，返回值有多个 -- (暂时用不上)
     */
    @Log(title = "模糊查询型号pn")
    @GetMapping("/selectpn/{pnn}")
    public AjaxResult GetPnListByPn(@PathVariable String pnn) {
        System.out.println("测试：" + pnn);
        List<FgChecklist> list = fgChecklistService.selectPn(pnn);
        return AjaxResult.success(list);
    }

    /**
     * 根据具体型号查询，也可能查到多条数据，因 存在不同批次 -- （暂时用不上）
     */
    @Log(title = "根据具体型号查询")
    @GetMapping("/selectonepn/{pn}")
    public AjaxResult GetOnePn(@PathVariable String pn) {
        System.out.println("测试：" + pn);
        List<FgChecklist> fgChecklist = fgChecklistService.selectOnePn(pn);
        return AjaxResult.success(fgChecklist);
    }

    /**
     * 根据型号和批次查询，正常情况下二者唯一（特殊：存在相同批次情况，在SQL语句默认获取过账编号为50开头的数据，过账编号唯一） -- （暂时用不上）
     * 不用过账编号获取是因为用户手上只有PN和batch
     */
    @Log(title = "根据批次查询")
    @GetMapping("/selectonepn1/{pn}/{batch}")
    public AjaxResult GetOnePn1(@PathVariable String pn, @PathVariable String batch) {
        System.out.println("测试：" + pn + batch);
        FgChecklist fgChecklist1 = fgChecklistService.selectOnePn1(pn, batch);
        System.out.println("测试：" + pn + batch);
        return AjaxResult.success(fgChecklist1);
    }

    /*----------------------------------- SAP导入数据（用于手动导入） -----------------------------------*/

    /**
     * 导入SAP数据生成成品送检单
     */
    @Log(title = "手动下载SAP数据")
    @Scheduled(cron = "0 */10 * * * ?")
    @PostMapping("/handimport")
    public AjaxResult handImport() throws Exception {
        String returnMessage = execute();
        if (returnMessage.equals("导入成功")) {
            return AjaxResult.success(returnMessage);
        } else {
            return AjaxResult.error(returnMessage);
        }

    }

    /**
     * 手动导入PMC确认走货信息 -- 用于提醒是否需要拆箱
     */
    @Log(title = "导入PMC确认走货信息")
    @Scheduled(cron = "0 0 8,14 * * ?")
    @ResponseBody
    @PostMapping("/downloadpmc")
    public AjaxResult downloadpmc() throws Exception {
//        String isok = devanning_PMC();
        String isok = fgChecklistService.devanning_PMC();
        if ("OK".equals(isok)) {
            return AjaxResult.success(isok);
        } else {
            return AjaxResult.success(isok);
        }
    }

    /**
     * 手动导入船务确认走货信息 -- 用于生成备货单/欠货单
     */

    @Log(title = "导入船务确认走货信息")
    // @Scheduled(cron = "0 0 8,14 * * ?")
    @ResponseBody
    @PostMapping("/downloadcw/{date}")
    public AjaxResult downloadpcw(@PathVariable("date") Date date) throws Exception {
       // System.out.println(date);
//        String isok = generateTO_NO();
        String isok = fgChecklistService.generateTO_NO(date);
        if ("OK".equals(isok)) {
            return AjaxResult.success(isok);
        } else {
            return AjaxResult.success(isok);
        }
    }
    @Log(title = "自动导入船务确认走货信息")
    @Scheduled(cron = "0 0 8,14 * * ?")
    public AjaxResult downloadpcw2() throws Exception {
        // System.out.println(date);
//        String isok = generateTO_NO();
        String isok = fgChecklistService.generateTO_NO(new Date());
        if ("OK".equals(isok)) {
            return AjaxResult.success(isok);
        } else {
            return AjaxResult.success(isok);
        }
    }
    /*----------------------------------- SAP导入数据 -----------------------------------*/

    /**
     * 打印成品送检单 -- 检查PO -- 重打印（只重打印/修改PO重打印）
     *
     * @param fgChecklist
     */
    @Log(title = "检查PO、打印、重打印（销毁/不销毁）")
    @PostMapping("/selectpo")
    public AjaxResult checkPo(@RequestBody FgChecklist fgChecklist) {
        /*if (fgChecklist.getQaSign() == null || fgChecklist.getQaSign().equals(""))
            return AjaxResult.error("请输入打印人");*/

        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list1 = new ArrayList<>();
        List<String> listPN1 = new ArrayList<>();
        List<String> listPN2 = new ArrayList<>();
        listPN1.addAll(Arrays.asList("660-V830008-00R0", "660-V830007-00R0", "660-V850003-00R0", "660-V850004-00R0", "660-V700009-00R0", "660-V700017-00R1", "660-V900010-00R1", "660-V110003-00R1", "660-V110007-00R1", "660-V110008-00R1"));
        listPN2.addAll(Arrays.asList("660-V600003-00R0", "660-V10E011-00R0", "660-V10E020-00R1", "660-V10E014-00R1", "660-V10E016-00R1", "660-V10A011-00R1", "660-V10A013-00R1", "660-V10B008-00R1", "660-V10B009-00R1", "660-V10E022-00R1"));
        String pn_660 = "";
        // uid为空即 第一次打印（非重打印）
        if (fgChecklist.getUid() == null || fgChecklist.getUid() == "") {
            System.out.println("==-=-2" + fgChecklist.getPn().toString().substring(0, 3));
            if (fgChecklist.getPn().toString().substring(0, 3).equals("650")) {

                System.out.println("ok");
                // 打印650型号送检单时查询SAP BOM 层次中对应的660型号，将650送检单自动关联至660型号送检单中
                pn_660 = sapUtil.get660Info(fgChecklist.getPn().toString(), fgChecklist.getPlant().toString());
                if (pn_660.equals("")) {
                    return AjaxResult.error("该650型号没有找到相关连的660型号");
                }
                System.out.println("==-=-" + pn_660);
                fgChecklist.setPn660(pn_660);
                // 检查po，并插入数据
                return checkPo2(fgChecklist, pn_660);
            } else {
                // 设置旧UID
                // fgChecklist.setOldUid(fgChecklist.getId().toString());
                // 660开头PN直接查PO并插入数据
                return checkPo2(fgChecklist, pn_660);
            }
        } else {
            int a = 0;
            if (listPN1.contains(fgChecklist.getPn().toString()) && fgChecklist.getPo().toString().equals("551050260")) {
                a = 1;
            } else if (listPN2.contains(fgChecklist.getPn().toString()) && fgChecklist.getPo().toString().equals("550853038")) {
                a = 1;
            } else if (fgChecklist.getPn660() == null || fgChecklist.getPn660().equals("")) {
                list1 = sapUtil.getPoInfo(fgChecklist.getPn().toString());
            } else {
                list1 = sapUtil.getPoInfo(fgChecklist.getPn660().toString());
            }
            int nn = list1.size();
            int i = 0;
            if (list1.size() > 0) {
                for (String[] arr : list1) {
                    if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                        break;
                    }
                    i += 1;
                }
            }
            if (a == 1) {
                int aa = 0;
            } else if (nn == i) {
                return AjaxResult.error("PO未查询到");
            }

            // UID不为空则 重打印 -- 检查QA检验结果
            FgChecklist fgChecklist1 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
            if (fgChecklist1.getQaResult() == null) {
                String returnMessage = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String productDate = sdf.format(fgChecklist1.getProductionDate());
                List<String[]> list = sapUtil.Z_HTMES_YMMR04_2(fgChecklist1.getPlant().toString(), fgChecklist1.getPn().toString(), productDate, productDate);
                System.out.println("查询QA结果数量：" + list.size());
                if (list.size() == 0) {
                    return AjaxResult.error("未查询到QA检验结果，不允许重打印");
                }
                int n1 = 0;
                int qaresult = 999;
                for (String[] arr : list) {
                    // 直接输出是带有[]框
                    String batch = Arrays.asList(arr[0]).toString();
                    String Mvt = Arrays.asList(arr[1]).toString();
                    System.out.println("ceshi:" + batch.substring(1, batch.length() - 1) + "---" + fgChecklist1.getBatch().toString());
                    if (batch.substring(1, batch.length() - 1).equals(fgChecklist1.getBatch().toString())) {
                        System.out.println("测试：" + Mvt.substring(1, Mvt.length() - 1) + "---");
                        if (Mvt.substring(1, Mvt.length() - 1).equals("321")) {
                            qaresult = 1;
                            // 321 QA结果为TRUE
                            int n = fgChecklistService.updateQAresult(fgChecklist1.getPn().toString(), qaresult, fgChecklist1.getPlant().toString(), fgChecklist1.getBatch().toString());
                            FgChecklist fgChecklist2 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
                            System.out.println(fgChecklist2.getUidNo() + "===" + fgChecklist.getUidNo());
                            return repeatPrint(fgChecklist, fgChecklist2);
                        } else if (Mvt.substring(1, Mvt.length() - 1).equals("122")) {
                            qaresult = 0;
                            // 122 QA结果为Fail
                            int n = fgChecklistService.updateQAresult(fgChecklist1.getPn().toString(), qaresult, fgChecklist1.getPlant().toString(), fgChecklist1.getBatch().toString());
                            FgChecklist fgChecklist2 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
                            System.out.println(fgChecklist2.getUidNo() + "===" + fgChecklist.getUidNo());
                            return AjaxResult.error("QA检验结果为fail，不允许重打印");
                        }
                    }
//                    if (Arrays.asList(arr).contains("321")) {
//
//                        // 321 QA结果为TRUE
//                        int n = fgChecklistService.updateQAresult(fgChecklist1.getPn().toString(), 1, fgChecklist1.getPlant().toString(), fgChecklist1.getBatch().toString());
//                        FgChecklist fgChecklist2 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
//                        System.out.println(fgChecklist2.getUidNo() + "===" + fgChecklist.getUidNo());
//                        return repeatPrint(fgChecklist, fgChecklist2);
//                    }
                    n1 += 1;
                    if (n1 == list.size()) {
//                        int n = fgChecklistService.updateQAresult(fgChecklist1.getPn().toString(), 2, fgChecklist1.getPlant().toString(), fgChecklist1.getBatch().toString());
//                        returnMessage = "QA未检验，不允许重打印";
                        return repeatPrint(fgChecklist, fgChecklist1);
                    }
                }
                return AjaxResult.error(returnMessage);
            } else if (fgChecklist1.getQaResult() == 1) {
                System.out.println(fgChecklist.getUidNo() + "===" + fgChecklist1.getUidNo());
                return repeatPrint(fgChecklist, fgChecklist1);
            } else {
                return AjaxResult.error("QA检验结果为fail，不允许重打印");
            }
        }
    }

    // 只判321
//    public AjaxResult checkPo(@RequestBody FgChecklist fgChecklist) {
//        SAPUtil sapUtil = new SAPUtil();
//        String pn_660 = "";
//        // uid为空即 第一次打印（非重打印）
//        if (fgChecklist.getUid() == null || fgChecklist.getUid() == "") {
//            if (fgChecklist.getPn().toString().substring(0, 3).equals("650")) {
//
//                System.out.println("ok");
//                // 打印650型号送检单时查询SAP BOM 层次中对应的660型号，将650送检单自动关联至660型号送检单中
//                pn_660 = sapUtil.get660Info(fgChecklist.getPn().toString(), fgChecklist.getPlant().toString());
//                if (pn_660.equals("")) {
//                    return AjaxResult.error("该650型号没有找到相关连的660型号");
//                }
//                fgChecklist.setPn660(pn_660);
//                // 检查po，并插入数据
//                return checkPo1(fgChecklist, pn_660);
//            } else {
//                // 设置旧UID
//                fgChecklist.setOldUid(fgChecklist.getId().toString());
//                // 660开头PN直接查PO并插入数据
//                return checkPo1(fgChecklist, pn_660);
//            }
//        } else {
//            // UID不为空则 重打印 -- 检查QA检验结果
//            FgChecklist fgChecklist1 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
//            if (fgChecklist1.getQaResult() == null) {
//                String returnMessage = "";
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                String productDate = sdf.format(fgChecklist1.getProductionDate());
//                List<String[]> list = sapUtil.Z_HTMES_YMMR04_2(fgChecklist1.getPlant().toString(), fgChecklist1.getPn().toString(), productDate, productDate);
//                System.out.println("查询QA结果数量：" + list.size());
//                if (list.size() == 0) {
//                    return AjaxResult.error("未查询到QA检验结果，不允许重打印");
//                }
//                int n1 = 0;
//                for (String[] arr : list) {
//                    if (Arrays.asList(arr).contains("321")) {
//                        // 321 QA结果为TRUE
//                        int n = fgChecklistService.updateQAresult(fgChecklist1.getPn().toString(), 1, fgChecklist1.getPlant().toString(), fgChecklist1.getBatch().toString());
//                        FgChecklist fgChecklist2 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
//                        System.out.println(fgChecklist2.getUidNo() + "===" + fgChecklist.getUidNo());
//                        return repeatPrint(fgChecklist, fgChecklist2);
//                    }
//                    n1 += 1;
//                    if (n1 == list.size()) {
//                        int n = fgChecklistService.updateQAresult(fgChecklist1.getPn().toString(), 0, fgChecklist1.getPlant().toString(), fgChecklist1.getBatch().toString());
//                        returnMessage = "查询结果为fail，不允许重打印";
//                    }
//                }
//                return AjaxResult.error(returnMessage);
//            } else if (fgChecklist1.getQaResult() == 1) {
//                System.out.println(fgChecklist.getUidNo() + "===" + fgChecklist1.getUidNo());
//                return repeatPrint(fgChecklist, fgChecklist1);
//            } else {
//                return AjaxResult.error("QA检验结果为fail，不允许重打印");
//            }
//        }
//    }

    /**
     * 根据拆箱提醒 拆分成品送检单
     *
     * @param fgChecklist
     */
    @Log(title = "拆分成品单")
    @PostMapping("/splitfg")
    public AjaxResult SpiltFg(@RequestBody FgChecklist fgChecklist) {
        SAPUtil sapUtil = new SAPUtil();

        FgChecklist fgChecklist1 = fgChecklistService.getQAresult(fgChecklist.getUid().toString());
        if (fgChecklist1.getQaResult() != null && fgChecklist1.getQaResult() == 0)
            return AjaxResult.error("QA检验结果为fail,不允许拆分！");

        if (!fgChecklist1.getPo().toString().equals(fgChecklist.getPo().toString())) {
            return AjaxResult.error("拆分成品单PO不能改变");
        }

        // 下架/出库（拣货）的成品不能拆分
        if (fgChecklistMapper.checkOut(fgChecklist.getUid().toString()) > 0)
            return AjaxResult.error("该成品已下架");

        // 绑定贴纸不能拆分
        if (fgChecklistMapper.checkTags(fgChecklist.getUid().toString()) > 0)
            return AjaxResult.error("绑定贴纸不能拆分");

        FgToList toList = fgTosMapper.getTolistInfo(fgChecklist.getUid().toString());
        if (toList != null) {
            FgInventory inventory = fgInventoryService.getInventoryInfo(fgChecklist.getUid().toString());
            String msg = inventory.getQuantity() - fgChecklist.getUidNo() == toList.getQuantity() ? "Y" : "N";
            if (!"Y".equals(msg))
                return AjaxResult.error("拆分数量应输入" + (inventory.getQuantity() - toList.getQuantity()));

            fgChecklist.setOldUid(fgChecklist1.getUid().toString());
            // 生成新UID
            String uid = getGuid();
            fgChecklist.setUid(uid);
            // 生成二维码前UID不能为空
            String path = fgChecklistService.generateBarcode(fgChecklist);
            System.out.println("path:" + path);
            fgChecklist.setBarcodeUrl(path);
            // 插入新送检单
            int n = fgChecklistService.insertFgChecklist(fgChecklist);
            if (n > 0) {
                System.out.println("插入成功");
                int n1 = fgChecklistService.updateBeforeUidNo(fgChecklist1.getUid().toString(), fgChecklist.getUidNo());
                int n2 = fgInventoryService.checkInventoty(fgChecklist1.getUid().toString());
                if (n2 > 0)
                    fgInventoryService.updateQuantity(fgChecklist1.getUid().toString(), fgChecklist.getUidNo());

                FgInventory fgInventory = new FgInventory();
                BeanUtil.copyProperties(inventory, fgInventory);
                fgInventory.setUid(fgChecklist.getUid().toString());
                fgInventory.setStatus(1);
                fgInventory.setQuantity(fgChecklist.getUidNo());
                fgInventoryService.insertFgInventory(fgInventory);

                return AjaxResult.success("成品单已拆分");
            }
        } else {
            // fgChecklist1.getUidNo() == fgChecklist.getUidNo()
            if (Math.abs(fgChecklist1.getUidNo() - fgChecklist.getUidNo()) < 0.000000002) {
                return AjaxResult.error("拆分数量应比拆分前数量小");
            }
            fgChecklist.setOldUid(fgChecklist1.getUid().toString());
            // 生成新UID
            String uid = getGuid();
            fgChecklist.setUid(uid);
            // 生成二维码前UID不能为空
            String path = fgChecklistService.generateBarcode(fgChecklist);
            System.out.println("path:" + path);
            fgChecklist.setBarcodeUrl(path);
            // 插入新送检单
            int n = fgChecklistService.insertFgChecklist(fgChecklist);
            if (n > 0) {
                System.out.println("插入成功");
                int n1 = fgChecklistService.updateBeforeUidNo(fgChecklist1.getUid().toString(), fgChecklist.getUidNo());
                int n2 = fgInventoryService.checkInventoty(fgChecklist1.getUid().toString());
                if (n2 > 0)
                    fgInventoryService.updateQuantity(fgChecklist1.getUid().toString(), fgChecklist.getUidNo());

                return AjaxResult.success("成品单已拆分");
            }
        }
        return AjaxResult.error("拆分失败");
    }

    /**
     * 用于SAP批量减少且TO明细表UID数量大于缺少数量的情况（未拣货）
     * */
    public FgChecklist SpiltFg2(String uid, long uidNo) {
        SAPUtil sapUtil = new SAPUtil();
        FgChecklist fgChecklist1 = fgChecklistService.getQAresult(uid);
        FgChecklist fgChecklist2 = new FgChecklist();
        BeanUtil.copyProperties(fgChecklist1, fgChecklist2, true);
        // fgChecklist1.getUidNo() == fgChecklist.getUidNo()
        if (Math.abs(fgChecklist1.getUidNo() - uidNo) < 0.000000002) {
            return fgChecklist2;
        }

        if (fgChecklist1.getQaResult() != 1) {
            return fgChecklist2;
        }
        fgChecklist2.setOldUid(fgChecklist1.getUid().toString());
        // 生成新UID
        String uid2 = getGuid();
        fgChecklist2.setUid(uid2);
        fgChecklist2.setUidNo(uidNo);
        // 生成二维码前UID不能为空
        String path = fgChecklistService.generateBarcode(fgChecklist2);
        System.out.println("path:" + path);
        fgChecklist2.setBarcodeUrl(path);
        // 插入新送检单
        int n = fgChecklistService.insertFgChecklist(fgChecklist2);
        if (n > 0) {
            System.out.println("插入成功");
            int n1 = fgChecklistService.updateBeforeUidNo(fgChecklist1.getUid().toString(), fgChecklist2.getUidNo());
            int n2 = fgInventoryService.checkInventoty(fgChecklist1.getUid().toString());
            if (n2 > 0)
                fgInventoryService.updateQuantity(fgChecklist1.getUid().toString(), fgChecklist2.getUidNo());

            return fgChecklist2;
        }
        return fgChecklist2;
    }

    /**
     * 可批量打印（批量打印存在样式问题，待完善），目前用户一次只打印一张，可正常使用
     *
     * @param ids
     */
    @Log(title = "页面中的打印功能（目前在前端已隐藏/不显示）")
    @GetMapping("/Print-order/{ids}")
    public void Printorder(@PathVariable String[] ids) {
        try {
            System.out.println("---" + ids);
            List<FgChecklist> fgChecklists = fgChecklistService.selectFgChecklistByUid(ids);
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/pdf");

            Document document = new Document(PageSize.A4, 20, 20, 50, 40);

            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

            // 打开文档
            document.open();
            Font font1 = new Font(BaseFont.createFont("c://windows//fonts//simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 24);
            // 下划线
            Font font4 = new Font(BaseFont.createFont("c://windows//fonts//simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 16, Font.UNDERLINE);
            Font font2 = new Font(BaseFont.createFont("c://windows//fonts//simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 14);

            // 当选择了多个UID打印
            for (int i = 0; i < fgChecklists.size(); i++) {
                FgChecklist fgChecklist = fgChecklists.get(i);
                Paragraph p1 = new Paragraph("成品送检单" + "\r\n" + "Product Submission Form", font1);
                // 行间距
                p1.setLeading(39);
                // 居中
                p1.setAlignment(Element.ALIGN_CENTER);
                document.add(p1);

                PdfPTable table = new PdfPTable(200);
                // 总宽度100%
                table.setWidthPercentage(100);
                // 上边距
                table.setSpacingBefore(15);
                PdfPCell cell;

                cell = new PdfPCell(new Phrase("UID:" + fgChecklist.getUid().toString(), font2));
                // 合并列
                cell.setColspan(200);
                // 内边距
                cell.setPadding(5);
                // 设置水平居中
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                // 设置垂直居中
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.disableBorderSide(15);//隐藏边框
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("型号:" + "\r\n" + "Mode1", font2));
                // 合并列
                cell.setColspan(50);
                // 内边距
                cell.setPadding(5);
                // 设置水平居中
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                // 设置垂直居中
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                // cell.disableBorderSide(15);//隐藏边框
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getPn() == null ? "" : fgChecklist.getPn().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("生产线:" + "\r\n" + "Line", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getLine() == null ? "" : fgChecklist.getLine().toString()), font2));
                cell.setColspan(150);//合并列
                cell.setPadding(5);//内边距
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("工单:" + "\r\n" + "WO No", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("客户订单编号:" + "\r\n" + "PO NP", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(fgChecklist.getPo().toString(), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("卡板号:" + "\r\n" + "pallet No    ", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getPalletNo() == null ? "" : fgChecklist.getPalletNo().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("批量:" + "\r\n" + "Lot Qty", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(fgChecklist.getBatchQty().toString(), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("数量:" + "\r\n" + "Qty", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getUidNo() == null ? "" : fgChecklist.getUidNo().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("物料文件:" + "\r\n" + "MatenialDocument", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("批次号:" + "\r\n" + "Lot No", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getBatch() == null ? "" : fgChecklist.getBatch().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("生产日期:" + "\r\n" + "Production Date", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getProductionDate() == null ? "" : fgChecklist.getProductionDate().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("卡板数:" + "\r\n" + "pallet Item", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getPalletItems() == null ? "" : fgChecklist.getPalletItems().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("开单人员:", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("检验结果:" + "\r\n" + "QA Inspection Result", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase((fgChecklist.getQaResult() == null ? "" : fgChecklist.getQaResult().toString()), font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("客户检查结果:" + "\r\n" + "Customer inspection", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("检验员:" + "\r\n" + "QA Inspector", font2));
                cell.setColspan(50);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", font2));
                cell.setColspan(150);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);


                document.add(table);

                // =====读入并设置印章图片============================
                Image image = Image.getInstance(fgChecklist.getBarcodeUrl());
                image.setScaleToFitLineWhenOverflow(true);
                image.setAlignment(Element.ALIGN_RIGHT);
                float x = 635.0F;
                float y = 740.0F;
                /*System.out.println("getTotalWidth:"+table.getTotalHeight());
                System.out.println("getTotalWidth:"+table.getTotalWidth());
                System.out.println("X:"+x);
                System.out.println("Y:"+y);*/

                image.setAbsolutePosition(x - 200, y);
                image.scaleAbsolute(80, 80);
                PdfContentByte pcb = writer.getDirectContentUnder();
                pcb.addImage(image);
                document.add(image);
                document.add(Chunk.NEWLINE);

                Image image1 = Image.getInstance("http://172.31.2.131:9000/ht-mes/Log.png");
                image1.setScaleToFitLineWhenOverflow(true);
                image1.setAlignment(Element.ALIGN_RIGHT);
                float x1 = 225.0F;
                float y1 = 750.0F;
                /*System.out.println("X1:"+x1);
                System.out.println("Y1:"+y1);*/

                image1.setAbsolutePosition(x1 - 200, y1);
                image1.scaleAbsolute(160, 80);
                PdfContentByte pcb1 = writer.getDirectContentUnder();
                pcb1.addImage(image1);
                document.add(image1);
                document.add(Chunk.NEWLINE);
                //===================================================
            }
            document.close();

        } catch (Exception e) {
            System.out.println("55555555");
            e.printStackTrace();
        }
    }

    /**
     * 生成PDF打印模板（正式使用）
     *
     * @param fgChecklist
     */
    @Log(title = "生成模板中的打印功能(单个)")
    @PostMapping("/Print-order1")
    public void Printorder1(@RequestBody FgChecklist fgChecklist) {
        try {
            System.out.println("---" + fgChecklist.getPn().toString());
            System.out.println("---" + fgChecklist.getQaSign());
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/pdf");

            Document document = new Document(PageSize.A4, 20, 20, 50, 40);

            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

            // 打开文档
            document.open();
            Font font1 = new Font(BaseFont.createFont("c://windows//fonts//simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 24);
            // 下划线
            Font font4 = new Font(BaseFont.createFont("c://windows//fonts//simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 16, Font.UNDERLINE);
            Font font2 = new Font(BaseFont.createFont("c://windows//fonts//simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 14);

            // + "\r\n" + "Product Submission Form"
            Paragraph p1 = new Paragraph("成品送检单", font1);
            Paragraph p2 = new Paragraph("Product Submission Form", font2);
            // 行间距
            p1.setLeading(35);
            // 居中
            p1.setAlignment(Element.ALIGN_CENTER);
            document.add(p1);
            // 行间距
            p2.setLeading(20);
            // 居中
            p2.setAlignment(Element.ALIGN_CENTER);
            document.add(p2);

            PdfPTable table = new PdfPTable(200);
            // 总宽度100%
            table.setWidthPercentage(100);
            // 上边距
            table.setSpacingBefore(10);

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("UID:" + fgChecklist.getUid().toString(), font2));
            // 合并列
            cell.setColspan(200);
            // 内边距
            cell.setPadding(5);
            // 设置水平居中
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            // 设置垂直居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("型号" + "\r\n" + "Mode1", font2));
            // 合并列
            cell.setColspan(50);
            // 内边距
            cell.setPadding(5);
            // 设置水平居中
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            // 设置垂直居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            // cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getPn() == null ? "" : fgChecklist.getPn().toString()) + (fgChecklist.getPn660() == null ? "" : " (关联：" + fgChecklist.getPn660().toString() + ")"), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("生产线" + "\r\n" + "Line", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getLine() == null ? "" : fgChecklist.getLine().toString()), font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("工单" + "\r\n" + "WO No", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("客户订单编号" + "\r\n" + "PO NP", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(fgChecklist.getPo() == null ? "" : fgChecklist.getPo().toString(), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("卡板号" + "\r\n" + "pallet No    ", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getPalletNo() == null ? "" : fgChecklist.getPalletNo().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("批量" + "\r\n" + "Lot Qty", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(fgChecklist.getBatchQty().toString(), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("数量" + "\r\n" + "Qty", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getUidNo() == null ? "" : fgChecklist.getUidNo().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("物料文件" + "\r\n" + "MatenialDocument", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getSap101() == null ? "" : fgChecklist.getSap101().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("批次号" + "\r\n" + "Lot No", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getBatch() == null ? "" : fgChecklist.getBatch().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("生产日期" + "\r\n" + "Production Date", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cell = new PdfPCell(new Phrase((fgChecklist.getProductionDate() == null ? "" : sdf.format(fgChecklist.getProductionDate()).toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("卡板数" + "\r\n" + "pallet Item", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getPalletItems() == null ? "" : fgChecklist.getPalletItems().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("开单人员\r\nBilling officer", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getCreateUser() == null ? "" : fgChecklist.getCreateUser().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("QA检验结果" + "\r\n" + "QA Inspection Result", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            String result = "";
            if (fgChecklist.getQaResult() != null && fgChecklist.getQaResult().toString().equals("1")) {
                result = "Pass";
            }
            cell = new PdfPCell(new Phrase(result, font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("客户检查结果" + "\r\n" + "Customer inspection", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("检验员" + "\r\n" + "QA Inspector", font2));
            cell.setColspan(50);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase((fgChecklist.getQaSign() == null ? "" : fgChecklist.getQaSign().toString()), font2));
            cell.setColspan(150);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);


            document.add(table);

            // =====读入并设置印章图片============================
            Image image = Image.getInstance(fgChecklist.getBarcodeUrl());
            image.setScaleToFitLineWhenOverflow(true);
            image.setAlignment(Element.ALIGN_RIGHT);
            float x = 635.0F;
            float y = 740.0F;
                /*System.out.println("getTotalWidth:"+table.getTotalHeight());
                System.out.println("getTotalWidth:"+table.getTotalWidth());
                System.out.println("X:"+x);
                System.out.println("Y:"+y);*/

            image.setAbsolutePosition(x - 200, y);
            image.scaleAbsolute(80, 80);
            PdfContentByte pcb = writer.getDirectContentUnder();
            pcb.addImage(image);
            document.add(image);
            document.add(Chunk.NEWLINE);

            Image image1 = Image.getInstance("http://172.31.2.131:9000/ht-mes/Log.png");
            image1.setScaleToFitLineWhenOverflow(true);
            image1.setAlignment(Element.ALIGN_RIGHT);
            float x1 = 225.0F;
            float y1 = 750.0F;
                /*System.out.println("X1:"+x1);
                System.out.println("Y1:"+y1);*/

            image1.setAbsolutePosition(x1 - 200, y1);
            image1.scaleAbsolute(160, 80);
            PdfContentByte pcb1 = writer.getDirectContentUnder();
            pcb1.addImage(image1);
            document.add(image1);
            document.add(Chunk.NEWLINE);
            //===================================================
            document.close();

        } catch (Exception e) {
            System.out.println("55555555");
            e.printStackTrace();
        }

    }

    /**
     * 生成新UID;
     * 新UID格式： FG + 年份 + 时间戳（从第三位开始，包含第三位） + 自定义ran（100开始+=1,999结束）
     *
     * @return String
     */
    public String getGuid() {
        FgChecklistController.Guid += 1;
        // 获取时间戳
        long now = System.currentTimeMillis();
        System.out.println(now);
        // 获取4位年份数字
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        // 获取年份
        String time = dateFormat.format(now);
        System.out.println(time);
        String info = now + "";
        int ran = 0;
        if (FgChecklistController.Guid > 999) {
            FgChecklistController.Guid = 100;
        }
        ran = FgChecklistController.Guid;

        // 新UID格式： FG + 年份 + 时间戳（从第三位开始，包含第三位） + 自定义ran（100开始+=1,999结束）
        return "FG" + time + info.substring(2, info.length()) + ran;
    }

    /**
     * 检查PO并将数据插入成品送检单表
     *
     * @param fgChecklist
     */
    public AjaxResult checkPo3(FgChecklist fgChecklist, String pn_660) {
        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = new ArrayList<>();
        // 查询SAP中是否有该PO
        if (!pn_660.equals("")) {
            list = sapUtil.getPoInfo(pn_660);
        } else {
            list = sapUtil.getPoInfo(fgChecklist.getPn().toString());
        }
        if (fgChecklist.getPo().toString().equals("NA")) {

            // 卡控打印数量总和 与 批量
            Long sum = fgChecklistService.getUidNo_Sum(fgChecklist.getPn().toString(), fgChecklist.getSap101().toString());
            System.out.println("eee" + sum);
            if (sum == 0) {
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                if (n1 > 0) {
                    return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                } else {
                    return AjaxResult.error("未知错误，请联系开发人员");
                }

            } else if (fgChecklist.getBatchQty() >= (sum + fgChecklist.getUidNo())) {
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                // 判重
                if (fgChecklistService.checkInfonChcklist(fgChecklist) == 0) {
                    int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                    if (n2 > 0) {
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }
                } else {
                    return AjaxResult.error("已存在数据，请在“已打印”中选择打印");
                }

            } else {
                return AjaxResult.error("打印数量总和不能大于批量，剩余可打印数量为" + (fgChecklist.getBatchQty() - sum));
            }
        } else {
            int n = list.size();
            int i = 0;
            for (String[] arr : list) {
                if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                    break;
                }
                i += 1;
            }
            if (n == i) {
                return AjaxResult.error("PO未查询到");
            } else {
                // 卡控打印数量总和 与 批量
                Long sum = fgChecklistService.getUidNo_Sum(fgChecklist.getPn().toString(), fgChecklist.getSap101().toString());
                System.out.println("eee" + sum);
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                FgChecklist fgChecklist1 = new FgChecklist();
                fgChecklist1 = fgChecklistMapper.getIdInfo(fgChecklist);
                System.out.println(fgChecklist);
                System.out.println(fgChecklist1);

                if (sum == 0) {

                    System.out.println(fgChecklist.getBatchQty() + "===" + fgChecklist.getUidNo());
                    // 打印数量等于批量直接修改原数据为已打印
                    if (Math.abs(fgChecklist.getBatchQty() - fgChecklist.getUidNo()) < 0.000000002f) {
                        int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                        if (n2 > 0) {
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    } else {
                        int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                        if (n1 > 0) {
                            fgChecklist1.setUidNo(fgChecklist.getBatchQty() - fgChecklist.getUidNo());
                            fgChecklist1.setStatus(3);
                            // 设置原数据的数量为打印后的剩余数量，状态还是未打印
                            System.out.println("1221" + fgChecklist);
                            System.out.println("2212" + fgChecklist1);
                            int n2 = fgChecklistService.insertFgChecklist(fgChecklist1);
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    }
                } else {
                    FgChecklist fgChecklist2 = fgChecklistMapper.getIdInfo2(fgChecklist);
                    if (fgChecklist.getBatchQty() > (sum + fgChecklist.getUidNo())) {

                        // 判重
                        if (fgChecklistService.checkInfonChcklist(fgChecklist) == 0) {
                            int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                            if (n2 > 0) {
                                fgChecklist2.setUidNo(fgChecklist.getBatchQty() - (sum + fgChecklist.getUidNo()));
                                // 设置原数据的数量为打印后的剩余数量，状态还是未打印
                                int n3 = fgChecklistService.updateFgChecklist(fgChecklist2);
                                return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                            } else {
                                return AjaxResult.error("未知错误，请联系开发人员");
                            }
                        } else {
                            return AjaxResult.error("已存在数据，请在“已打印”中选择打印");
                        }

                    } else if (Math.abs(fgChecklist.getBatchQty() - (sum + fgChecklist.getUidNo())) < 0.000000002f) {
                        fgChecklist2.setUid(uid);
                        fgChecklist2.setPo(fgChecklist.getPo().toString());

                        // 打印数量刚好等于剩余数量
                        int n2 = fgChecklistService.updateFgChecklist(fgChecklist);
                        if (n2 > 0) {
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    } else {
                        return AjaxResult.error("打印数量总和不能大于批量，剩余可打印数量为" + (fgChecklist.getBatchQty() - sum));
                    }
                }
            }
        }
    }


    public AjaxResult checkPo2(FgChecklist fgChecklist, String pn_660) {
        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = new ArrayList<>();
        List<String> listPN1 = new ArrayList<>();
        List<String> listPN2 = new ArrayList<>();
        listPN1.addAll(Arrays.asList("660-V830008-00R0", "660-V830007-00R0", "660-V850003-00R0", "660-V850004-00R0", "660-V700009-00R0", "660-V700017-00R1", "660-V900010-00R1", "660-V110003-00R1", "660-V110007-00R1", "660-V110008-00R1"));
        listPN2.addAll(Arrays.asList("660-V600003-00R0", "660-V10E011-00R0", "660-V10E020-00R1", "660-V10E014-00R1", "660-V10E016-00R1", "660-V10A011-00R1", "660-V10A013-00R1", "660-V10B008-00R1", "660-V10B009-00R1", "660-V10E022-00R1"));
        Date date = new Date();
        // 查询SAP中是否有该PO
        if (!pn_660.equals("")) {
            list = sapUtil.getPoInfo(pn_660);
        } else {
            list = sapUtil.getPoInfo(fgChecklist.getPn().toString());
        }
        if (fgChecklist.getPo() == null)
            return AjaxResult.error("请输入PO号，无PO则输入NA");

        if (fgChecklist.getPo().toString().equals("NA")) {

            // 卡控打印数量总和 与 批量
            Long sum = fgChecklistService.getUidNo_Sum(fgChecklist.getPn().toString(), fgChecklist.getSap101().toString());
            System.out.println("eee" + sum);
            System.out.println("eee" + sum);
            String uid = getGuid();
            fgChecklist.setUid(uid);
            // 生成二维码UID不能为空
            String path = fgChecklistService.generateBarcode(fgChecklist);
            System.out.println("path:" + path);
            fgChecklist.setBarcodeUrl(path);
            FgChecklist fgChecklist1 = new FgChecklist();
            fgChecklist1 = fgChecklistMapper.getIdInfo(fgChecklist);
            fgChecklist.setUpdateDate(date);
            fgChecklist1.setUpdateDate(date);
            System.out.println(fgChecklist);
            System.out.println(fgChecklist1);
            if (sum == 0) {

                System.out.println(fgChecklist.getBatchQty() + "===" + fgChecklist.getUidNo());
                // 打印数量等于批量直接修改原数据为已打印
                if (Math.abs(fgChecklist.getBatchQty() - fgChecklist.getUidNo()) < 0.000000002f) {
                    fgChecklist1.setUid(uid);
                    fgChecklist1.setBarcodeUrl(path);
                    fgChecklist1.setUidNo(fgChecklist.getUidNo());
                    fgChecklist1.setStatus(1);
                    fgChecklist1.setPo(fgChecklist.getPo().toString());
                    fgChecklist1.setPalletNo(fgChecklist.getPalletNo());
                    fgChecklist1.setPalletItems(fgChecklist.getPalletItems());
                    fgChecklist1.setCreateUser(fgChecklist.getCreateUser());
                    // 条码类型
                    fgChecklist1.setBarcodeFormart(fgChecklist.getBarcodeFormart().toString());
                    int n2 = fgChecklistService.updateFgChecklist(fgChecklist1);
                    if (n2 > 0) {
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist1.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }
                } else {
                    int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                    if (n1 > 0) {
                        fgChecklist1.setUidNo(fgChecklist.getBatchQty() - fgChecklist.getUidNo());
                        // 设置原数据的数量为打印后的剩余数量，状态还是未打印
                        System.out.println("1221" + fgChecklist);
                        System.out.println("2212" + fgChecklist1);
                        int n2 = fgChecklistService.updateFgChecklist(fgChecklist1);
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }
                }

            } else if (fgChecklist.getBatchQty() > (sum + fgChecklist.getUidNo())) {

                // 判重
                if (fgChecklistService.checkInfonChcklist(fgChecklist) == 0) {
                    int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                    if (n2 > 0) {
                        fgChecklist1.setUidNo(fgChecklist.getBatchQty() - (sum + fgChecklist1.getUidNo()));
                        // 设置原数据的数量为打印后的剩余数量，状态还是未打印
                        int n3 = fgChecklistService.updateFgChecklist(fgChecklist1);
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }
                } else {
                    return AjaxResult.error("已存在数据，请在“已打印”中选择打印");
                }

            } else if (Math.abs(fgChecklist.getBatchQty() - (sum + fgChecklist.getUidNo())) < 0.000000002f) {
                // 打印数量刚好等于剩余数量
                int n2 = fgChecklistService.updateFgChecklist(fgChecklist);
                if (n2 > 0) {
                    return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                } else {
                    return AjaxResult.error("未知错误，请联系开发人员");
                }
            } else {
                return AjaxResult.error("打印数量总和不能大于批量，剩余可打印数量为" + (fgChecklist.getBatchQty() - sum));
            }
        } else {
            int n = list.size();
            int i = 0;
            int a = 0;
            if (listPN1.contains(fgChecklist.getPn().toString()) && fgChecklist.getPo().toString().equals("551050260")) {
                a = 1;
            } else if (listPN2.contains(fgChecklist.getPn().toString()) && fgChecklist.getPo().toString().equals("550853038")) {
                a = 1;
            } else {
                for (String[] arr : list) {
                    if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                        break;
                    }
                    i += 1;
                }
            }
            if (n == i && a != 1) {
                return AjaxResult.error("PO未查询到");
            } else {
                // 卡控打印数量总和 与 批量
                Long sum = fgChecklistService.getUidNo_Sum(fgChecklist.getPn().toString(), fgChecklist.getSap101().toString());
                System.out.println("eee" + sum);
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                FgChecklist fgChecklist1 = new FgChecklist();
                fgChecklist1 = fgChecklistMapper.getIdInfo(fgChecklist);
                fgChecklist.setUpdateDate(date);
                fgChecklist1.setUpdateDate(date);
                fgChecklist1.setPn660(fgChecklist.getPn660());
                System.out.println(fgChecklist);
                System.out.println(fgChecklist1);

                if (sum == 0) {

                    System.out.println(fgChecklist.getBatchQty() + "===" + fgChecklist.getUidNo());
                    // 打印数量等于批量直接修改原数据为已打印
                    if (Math.abs(fgChecklist.getBatchQty() - fgChecklist.getUidNo()) < 0.000000002f) {
                        fgChecklist1.setUid(uid);
                        fgChecklist1.setBarcodeUrl(path);
                        fgChecklist1.setUidNo(fgChecklist.getUidNo());
                        fgChecklist1.setStatus(1);
                        fgChecklist1.setPo(fgChecklist.getPo().toString());
                        fgChecklist1.setPalletNo(fgChecklist.getPalletNo());
                        fgChecklist1.setPalletItems(fgChecklist.getPalletItems());
                        fgChecklist1.setCreateUser(fgChecklist.getCreateUser());
                        // 条码类型
                        fgChecklist1.setBarcodeFormart(fgChecklist.getBarcodeFormart().toString());
                        int n2 = fgChecklistService.updateFgChecklist(fgChecklist1);
                        if (n2 > 0) {
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist1.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    } else {
                        int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                        if (n1 > 0) {
                            fgChecklist1.setUidNo(fgChecklist.getBatchQty() - fgChecklist.getUidNo());
                            // 设置原数据的数量为打印后的剩余数量，状态还是未打印
                            System.out.println("1221" + fgChecklist);
                            System.out.println("2212" + fgChecklist1);
                            int n2 = fgChecklistService.updateFgChecklist(fgChecklist1);
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    }
                } else if (fgChecklist.getBatchQty() > (sum + fgChecklist.getUidNo())) {

                    // 判重
                    if (fgChecklistService.checkInfonChcklist(fgChecklist) == 0) {
                        int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                        if (n2 > 0) {
                            fgChecklist1.setUidNo(fgChecklist.getBatchQty() - (sum + fgChecklist1.getUidNo()));
                            // 设置原数据的数量为打印后的剩余数量，状态还是未打印
                            int n3 = fgChecklistService.updateFgChecklist(fgChecklist1);
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    } else {
                        return AjaxResult.error("已存在数据，请在“已打印”中选择打印");
                    }

                } else if (Math.abs(fgChecklist.getBatchQty() - (sum + fgChecklist.getUidNo())) < 0.000000002f) {
                    // 打印数量刚好等于剩余数量
                    int n2 = fgChecklistService.updateFgChecklist(fgChecklist);
                    if (n2 > 0) {
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }
                } else {
                    return AjaxResult.error("打印数量总和不能大于批量，剩余可打印数量为" + (fgChecklist.getBatchQty() - sum));
                }
            }
        }
    }

    public AjaxResult checkPo1(FgChecklist fgChecklist, String pn_660) {
        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = new ArrayList<>();
        // 查询SAP中是否有该PO
        if (!pn_660.equals("")) {
            list = sapUtil.getPoInfo(pn_660);
        } else {
            list = sapUtil.getPoInfo(fgChecklist.getPn().toString());
        }
        if (fgChecklist.getPo().toString().equals("NA")) {

            // 卡控打印数量总和 与 批量
            Long sum = fgChecklistService.getUidNo_Sum(fgChecklist.getPn().toString(), fgChecklist.getSap101().toString());
            System.out.println("eee" + sum);
            if (sum == 0) {
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                if (n1 > 0) {
                    return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                } else {
                    return AjaxResult.error("未知错误，请联系开发人员");
                }

            } else if (fgChecklist.getBatchQty() >= (sum + fgChecklist.getUidNo())) {
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                // 判重
                if (fgChecklistService.checkInfonChcklist(fgChecklist) == 0) {
                    int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                    if (n2 > 0) {
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }
                } else {
                    return AjaxResult.error("已存在数据，请在“已打印”中选择打印");
                }

            } else {
                return AjaxResult.error("打印数量总和不能大于批量，剩余可打印数量为" + (fgChecklist.getBatchQty() - sum));
            }
        } else {
            int n = list.size();
            int i = 0;
            for (String[] arr : list) {
                if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                    break;
                }
                i += 1;
            }
            if (n == i) {
                return AjaxResult.error("PO未查询到");
            } else {
                // 卡控打印数量总和 与 批量
                Long sum = fgChecklistService.getUidNo_Sum(fgChecklist.getPn().toString(), fgChecklist.getSap101().toString());
                System.out.println("eee" + sum);
                if (sum == 0) {
                    String uid = getGuid();
                    fgChecklist.setUid(uid);
                    // 生成二维码UID不能为空
                    String path = fgChecklistService.generateBarcode(fgChecklist);
                    System.out.println("path:" + path);
                    fgChecklist.setBarcodeUrl(path);
                    int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                    if (n1 > 0) {
                        return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                    } else {
                        return AjaxResult.error("未知错误，请联系开发人员");
                    }

                } else if (fgChecklist.getBatchQty() >= (sum + fgChecklist.getUidNo())) {
                    String uid = getGuid();
                    fgChecklist.setUid(uid);
                    // 生成二维码UID不能为空
                    String path = fgChecklistService.generateBarcode(fgChecklist);
                    System.out.println("path:" + path);
                    fgChecklist.setBarcodeUrl(path);
                    // 判重
                    if (fgChecklistService.checkInfonChcklist(fgChecklist) == 0) {
                        int n2 = fgChecklistService.insertFgChecklist(fgChecklist);
                        if (n2 > 0) {
                            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                        } else {
                            return AjaxResult.error("未知错误，请联系开发人员");
                        }
                    } else {
                        return AjaxResult.error("已存在数据，请在“已打印”中选择打印");
                    }

                } else {
                    return AjaxResult.error("打印数量总和不能大于批量，剩余可打印数量为" + (fgChecklist.getBatchQty() - sum));
                }
            }
        }
    }

    /**
     * 重打印（销毁/不销毁）
     *
     * @param fgChecklist fgChecklist1
     **/
    public AjaxResult repeatPrint(FgChecklist fgChecklist, FgChecklist fgChecklist1) {
        SAPUtil sapUtil = new SAPUtil();
        // 判断是否销毁前送检单（po与之前的不一致则为销毁）
        if (!fgChecklist.getPo().toString().equals(fgChecklist1.getPo().toString())) {
            // 不能直接使用==判断浮点数
            if (Math.abs(fgChecklist.getUidNo() - fgChecklist1.getUidNo()) > 0.000000002) {
                return AjaxResult.error("数量不能修改");
            }
            // 避免使用== !=来判断堆值
            System.out.println("数据库" + fgChecklist.getPo().toString() + "前端" + fgChecklist1.getPo().toString());
            System.out.println("数据库" + fgChecklist.getUidNo().toString() + "前端" + fgChecklist1.getUidNo().toString());

            // 检查新输入PO是否存在
            List<String[]> list = sapUtil.getPoInfo(fgChecklist.getPn().toString());
            int n = list.size();
            int i = 0;
            for (String[] arr : list) {
                if (Arrays.asList(arr).contains(fgChecklist.getPo().toString())) {
                    break;
                }
                i += 1;
            }
            if (n == i) {
                return AjaxResult.error("PO未查询到");
            } else {
                fgChecklist.setOldUid(fgChecklist1.getUid().toString());
                fgChecklist.setQaResult(1);
                // 生成新UID
                String uid = getGuid();
                fgChecklist.setUid(uid);
                // 生成二维码前UID不能为空
                String path = fgChecklistService.generateBarcode(fgChecklist);
                System.out.println("path:" + path);
                fgChecklist.setBarcodeUrl(path);
                // 插入新送检单
                int n1 = fgChecklistService.insertFgChecklist(fgChecklist);
                if (n1 > 0) {
                    // 更改原送检单状态为2 销毁状态
                    fgChecklistService.destroyUid(fgChecklist.getOldUid().toString());
                    return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
                } else {
                    return AjaxResult.error("前送检单销毁失败请联系开发人员");
                }
            }
        } else {
            // PO相等直接重打印
            return AjaxResult.success(fgChecklistService.getPrintInfo(fgChecklist.getUid().toString()));
        }
    }

    /**
     * SAP导入成品送检单信息(用于手动导入)
     *
     * @return String
     */
    public String execute() throws Exception {

        String returnMessage = "";
        try {
            System.out.println("Starting..  BitchPrint start....." + new Date());

            // 如果物料模糊查询,过账日期不传值,则默认当天（日期格式20230331）
            // 如果物料模糊查询,过账日期不传值,则默认当天; 如果开始日期有值,结束日期没有值,则结束日期默认当天
            // 获取当天日期和一个月后日期(目前10分钟执行一次)
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusMonths(1);
            // 日期时间与时区相结合创建 ZonedDateTime
            ZonedDateTime zonedDateTimeStart = startDate.atZone(ZoneId.systemDefault());
            ZonedDateTime zonedDateTimeEnd = endDate.atZone(ZoneId.systemDefault());
            // 本地时间线LocalDateTime到即时 时间线Instant时间戳
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
            System.out.println("1212" + startDate1);
//            List<FgChecklist> fgChecklistList = new ArrayList<>();

            // 查询SAP过账接口1100工厂
            List<FgChecklist> list1 = sap.Z_HTMES_YMMR04("1100", "660*", startDate1, endDate1, "101");
            logger.info("1100工厂获取660*数量：" + list1.size());
            insertInto(list1);
            List<FgChecklist> list2 = sap.Z_HTMES_YMMR04("1100", "650*", startDate1, endDate1, "101");
            System.out.println("---" + list2.size());
            logger.info("1100工厂获取650*数量：" + list2.size());
            insertInto(list2);

            // 查询SAP过账接口5000工厂
            List<FgChecklist> list3 = sap.Z_HTMES_YMMR04("5000", "660*", startDate1, endDate1, "101");
            logger.info("5000工厂获取660*数量：" + list3.size());
            insertInto(list3);
            List<FgChecklist> list4 = sap.Z_HTMES_YMMR04("5000", "650*", startDate1, endDate1, "101");
            logger.info("5000工厂获取650*数量：" + list4.size());
            insertInto(list4);
//            fgChecklistList.addAll(list1);
//            fgChecklistList.addAll(list2);
//            fgChecklistList.addAll(list3);
//            fgChecklistList.addAll(list4);
//            insertInto(fgChecklistList);

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
     * 将处理过的SAP成品数据存到成品数据表
     *
     * @param list
     **/
    public void insertInto(List<FgChecklist> list) {

        for (int i = 0; i < list.size(); i++) {
            FgChecklist fgChecklist = list.get(i);
            System.out.println(fgChecklist.toString());
            System.out.println(fgChecklist.getSap101().toString() + "---" + fgChecklist.getPn().toString());
            String sap101 = fgChecklist.getSap101().toString();
            String pn = fgChecklist.getPn().toString();
            String line = "";
            String qasign = "";
            FgChecklist fgChecklist1 = new FgChecklist();
            // 判断该数据是否已在数据库
            int n = fgChecklistMapper.checkinfo(sap101, pn);
            if (n == 0) {
                // 获取拉别 (建立索引，否则很慢)
                // 获取检验员 51
                fgChecklist1 = fgChecklistMapper.checkQasign(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
                System.out.println("测试" + fgChecklist1);
                if (fgChecklist1 == null) {
                    // 72
                    fgChecklist1 = fgChecklistMapper.checkQasign2(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
                    if (fgChecklist1 == null) {
                        // 75
                        fgChecklist1 = fgChecklistMapper.checkQasign3(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
                    }
                    qasign = fgChecklist1 == null ? "" : fgChecklist1.getQaSign().toString();
                    line = fgChecklist1 == null ? "" : fgChecklist1.getLine().toString();
                } else {
                    qasign = fgChecklist1 == null ? "" : fgChecklist1.getQaSign().toString();
                    line = fgChecklist1 == null ? "" : fgChecklist1.getLine().toString();
                }
                System.out.println("测试2" + fgChecklist1);

//                qasign = fgChecklistMapper.checkQasign(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
//                if (qasign == null || qasign.equals("")) {
//                    qasign = fgChecklistMapper.checkQasign2(fgChecklist.getWo() == null ? "" : fgChecklist.getWo().toString().toString());
//                }
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

    /************************        以下代码已转移到业务层（serviceImpl）        ***************************/

    /**
     * SAP导入PMC确认的走货信息并判断是否拆箱
     *
     * @return String
     */
//    public String devanning_PMC() throws Exception {
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
//                                int n1 = fgTosMapper.updatePMCstatus(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "PMC", list1.get(i).getShipmentNO().toString());
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
//        return isok;
//    }

    /**
     * SAP导入船务确认的走货信息并产生备货单/走货单 后期可优化多重if嵌套
     *
     * @return String
     */
//    public String generateTO_NO() throws Exception {
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
//            List<String[]> strings = new ArrayList<>();
//            // 统计相同走货信息（相同则数量累加【相当于去重】）
//            List<ShipmentPart> list1 = fgTosMapper.selectShipmentPart("船务");
//            for (int i = 0; i < list1.size(); i++) {
//
//                FgTos fgTos = new FgTos();
//                FgToList fgToList = new FgToList();
//                long shipment_qty = fgTosMapper.getQuantityByshipmentno(list1.get(i).getShipmentNO().toString());
//                System.out.println("测试2" + list1.get(i).getPn().toString() + list1.get(i).getPo().toString() + "ss" + list1.get(i).getBatchsum());
//                // 走货信息逐条 根据 PN PO 船务关联库存表查询
//                List<FgTosAndTosListDto> fgTosAndTosListDtos = fgTosMapper.getTosAndTOListInfo(list1.get(i).getPn().toString(), list1.get(i).getPo().toString(), "船务");
//                // 查询是否已存在备货单/欠货单
//                String BH = fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_BH(list1.get(i).getShipmentNO().toString());
//                String QH = fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString()) == null ? "" : fgTosMapper.checkTosByshipmentno_QH(list1.get(i).getShipmentNO().toString());
//                // 当走货单的某个PN  PO在库存表中无数据，产生欠货单（欠货数量为改PN的批量），并邮件提醒
//                if (fgTosAndTosListDtos.size() == 0) {
//                    if (!QH.contains("QH")) {
//                        QH = generateTo_No("欠货单");
//                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
//                        fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), 0l, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
//                        fgTosMapper.insertFgTos(fgTos);
//                    }
//                    // 没有批次、库位、UID  状态（0备货单，1已拣货，2欠货单，3欠货单已备货）
//                    fgToList = setFgToList(fgToList, QH, list1.get(i).getBatchsum(), 2);
//                    fgToList.setPn(list1.get(i).getPn().toString());
//                    fgToList.setPo(list1.get(i).getPo().toString());
//                    fgToList.setSap_qty(shipment_qty);
//                    fgTosMapper.insertFgTolist(fgToList);
//                    // 在TO明细表查询欠货总数赋值到TO管理表
//                    long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
//                    fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
//                    // 用于邮件提醒欠货
//                    String[] s = new String[2];
//                    s[0] = list1.get(i).getPn().toString();
//                    s[1] = list1.get(i).getBatchsum() + "";
//                    strings.add(s);
//
//                } else {
//                    if (!BH.contains("BH")) {
//                        BH = generateTo_No("备货单");
//                        // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
//                        fgTos = setFgTos(BH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 0);
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
//                            // 状态（0备货单，1已拣货，2欠货单，3欠货单已备货）
//                            fgToList.setStatus(0);
//                            fgTosMapper.insertFgTolist(fgToList);
//                            fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() > list1.get(i).getBatchsum()) {
//                            System.out.println("产生备货单2");
//                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
//                            long qty = -1;
//                            if (sum_uidno > list1.get(i).getBatchsum()) {
//                                // 累加到大于临界值情况 提醒拆分该成品单后 减去多出的部分 生成欠货单（后续判断库存表是否有拆分的数据 再生成新备货单）
//                                // 生成欠货单前判断TO管理是否已存在该走货单的欠货单，存在则直接在TO明细表生成欠货单，不存在则现在TO管理生成欠货单，再存到TO明细表
//                                if (!QH.contains("QH")) {
//                                    QH = generateTo_No("欠货单");
//                                    // 状态（0可备货，1备货中，2备货完成，3欠货中，4欠货单已备货）
//                                    fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
//                                    fgTosMapper.insertFgTos(fgTos);
//                                }
//                                // 欠货数量（即所需要备货的数量）
//                                qty = sum_uidno - list1.get(i).getBatchsum();
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                // 统一赋值
//                                fgToList = setFgToList(fgToList, QH, qty, 0);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
//                                fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
//                                String[] s = new String[2];
//                                s[0] = fgTosAndTosListDtos.get(j).getUid().toString();
//                                s[1] = qty + "";
//                                strings.add(s);
//                            } else if (sum_uidno == list1.get(i).getBatchsum()) {
//                                // 累加到刚好等于的情况 则备货后直接break退出循环，后面的关联数据就不用进行备货了
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                break;
//                            } else {
//                                // 累加还未达到临界值前 直接将关联的数据直接存到TO明细表里作为备货单
//                                System.out.println("11111");
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                            }
//                            System.out.println("邮件提醒还未拆箱");
//                        } else if (fgTosAndTosListDtos.get(j).getSum_uidno() < list1.get(i).getBatchsum()) {
//                            System.out.println("产生备货单3");
//                            sum_uidno += fgTosAndTosListDtos.get(j).getQuantity();
//                            // 临界值为库存总数
//                            if (sum_uidno == fgTosAndTosListDtos.get(j).getSum_uidno()) {
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
//                                // 总数小于 批次总数则产生备货单（即剩下的数量为欠货数量）
//                                if (!QH.contains("QH")) {
//                                    QH = generateTo_No("欠货单");
//                                    fgTos = setFgTos(QH, list1.get(i).getShipmentNO().toString(), shipment_qty, list1.get(i).getCarno() == null ? "" : list1.get(i).getCarno().toString(), list1.get(i).getPlant().toString(), 3);
//                                    fgTosMapper.insertFgTos(fgTos);
//                                }
//                                long qty = list1.get(i).getBatchsum() - fgTosAndTosListDtos.get(j).getSum_uidno();
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                // 将欠料数存到TO明细表
//                                fgToList = setFgToList(fgToList, QH, qty, 2);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                long sumqty = fgTosMapper.getSumqty(list1.get(i).getShipmentNO().toString());
//                                fgTosMapper.updateQuantity(sumqty, list1.get(i).getShipmentNO().toString());
//                                String[] s = new String[2];
//                                s[0] = fgTosAndTosListDtos.get(j).getPn().toString();
//                                s[1] = qty + "";
//                                strings.add(s);
//                            } else {
//                                BeanUtil.copyProperties(fgTosAndTosListDtos.get(j), fgToList, true);
//                                fgToList = setFgToList(fgToList, BH, 0l, 0);
//                                fgTosMapper.insertFgTolist(fgToList);
//                                fgInventoryMapper.updateStatusByUid(fgTosAndTosListDtos.get(j).getUid().toString());
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
//            // AutoDownload_BitchPrint autoDownloadBitchPrint = new AutoDownload_BitchPrint();
//            // 参数strings集合
//            // autoDownloadBitchPrint.sendMail();
//            System.out.println("邮件提醒某UID成品单需要拆分成***");
//
//            isok = "OK";
//            System.out.println("The end...." + new Date());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isok;
//    }

    /**
     * 根据FG + BH或QH + 年、月、日、时、分、秒、毫秒（三位数） 生成备货单/备货单
     *
     * @param tono 描述是备货单还是欠货单
     * @return String
     **/
//    public String generateTo_No(String tono) {
//
//        String To_No = "";
//        try {
//            Date date = new Date();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//            if (tono.equals("备货单")) {
//                // FG + BH + 年月日时分秒 毫秒
//                To_No = "BH" + simpleDateFormat.format(date);
//                System.out.println(To_No);
//            } else if (tono.equals("欠货单")) {
//                // FG + QH + 年月日时分秒 毫秒
//                To_No = "QH" + simpleDateFormat.format(date);
//                System.out.println(To_No);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return To_No;
//
//    }

    /***
     * 生成备货单/欠货单TO管理表同一赋值
     */
//    public FgTos setFgTos(String toNo, String shipmentNo, long sapQty, String carNo, String plant, int status) {
//
//        // 直接创建对象赋值，因为没有原值需要返回（对比setFgToList()区别）
//        FgTos fgTos = new FgTos();
//        fgTos.setTo_No(toNo);
//        fgTos.setShipmentNO(shipmentNo);
//        fgTos.setSap_qty(sapQty);
//        fgTos.setCarNo(carNo);
//        fgTos.setPlant(plant);
//        fgTos.setStatus(status);
//
//        return fgTos;
//    }

    /***
     * 生成备货单/欠货单TO明细表同一赋值
     *
     */
//    public FgToList setFgToList(FgToList fgToList, String toNo, long quantity, int status) {
//
//        // 不在方法内创建FgToList对象是因为只需要设置其它字段的值，原有的值需要保留一并返回（对比setFgTos()区别）
//        // 数量为0则不需要设置数量，原值已有
//        if (quantity == 0l) {
//            fgToList.setTo_No(toNo);
//            fgToList.setStatus(status);
//        } else {
//            fgToList.setTo_No(toNo);
//            fgToList.setQuantity(quantity);
//            fgToList.setStatus(status);
//        }
//        return fgToList;
//    }

}
