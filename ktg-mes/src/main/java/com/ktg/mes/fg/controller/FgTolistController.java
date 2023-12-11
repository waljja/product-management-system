package com.ktg.mes.fg.controller;

import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.core.page.TableDataInfo;
import com.ktg.common.enums.BusinessType;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.mes.fg.domain.FgSealnoinfo;
import com.ktg.mes.fg.domain.FgShipmentInfo;
import com.ktg.mes.fg.domain.FgToList;
import com.ktg.mes.fg.service.FgTolistService;
import com.ktg.mes.fg.utils.SAPUtil;
import org.apache.ibatis.javassist.Loader;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 成品备货清单Controller
 * @author JiangTingming
 * @date 2023-05-05 start
 */
@RestController
@RequestMapping("/fg/tolist")
public class FgTolistController extends BaseController {
    @Autowired
    private FgTolistService fgTolistService;

    @PostMapping("/reday")
    public AjaxResult reday()
    {
        long readySum = fgTolistService.getReadyBH();
        return AjaxResult.success(readySum);
    }

    @PostMapping("/ing")
    public AjaxResult ing()
    {
        long ingSum = fgTolistService.getBHing();
        return AjaxResult.success(ingSum);
    }

    /**
     * 查询TO明细列表
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:list')")
    @GetMapping("/list")
    public TableDataInfo list(FgToList fgTolist)
    {
        startPage();
        List<FgToList> list = fgTolistService.selectFgTolistList(fgTolist);
        // List<FgToList> list = fgTolistService.selectFgTolistList2(fgTolist);
        System.out.println(list);
        return getDataTable(list);
    }

    /**
     * 导出TO明细列表
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:export')")
    @Log(title = "TO明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FgToList fgTolist)
    {
        List<FgToList> list = fgTolistService.selectFgTolistList(fgTolist);
        ExcelUtil<FgToList> util = new ExcelUtil<FgToList>(FgToList.class);
        util.exportExcel(response, list, "TO明细数据");
    }

    /**
     * 获取TO明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(fgTolistService.selectFgTolistById(id));
    }

    /**
     * 新增TO明细
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:add')")
    @Log(title = "TO明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FgToList fgTolist)
    {
        return toAjax(fgTolistService.insertFgTolist(fgTolist));
    }

    /**
     * 修改TO明细
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:edit')")
    @Log(title = "TO明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FgToList fgTolist)
    {
        return toAjax(fgTolistService.updateFgTolist(fgTolist));
    }

    /**
     * 删除TO明细
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:remove')")
    @Log(title = "TO明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(fgTolistService.deleteFgTolistByIds(ids));
    }

    /**
     * 上传CK00走货信息和库存信息生成TO
     *
     * @param file
     */
    @Log(title = "自定义走货信息和库存信息生成TO", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult uploadCK00(@RequestParam("file") MultipartFile file) throws IOException {

        String returnMessage = "";
        List<FgSealnoinfo> list = new ArrayList<>();
//        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // 2007 -- 2007+ 版本
            if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("所用行数（下标）：" + sheet.getLastRowNum());

            // 从第二行开始获取数据（第一行为字段） -- 统一判断满足条件后再统一产生
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                String ship_pn = String.valueOf(row.getCell(2));
                String pn = String.valueOf(row.getCell(6));
                System.out.println(ship_pn + "==" + pn);
                if (!ship_pn.equals(pn))
                    return AjaxResult.error("走货PN" + ship_pn + "与库存PN" + pn + "不相等");

                String ship_po = String.valueOf(row.getCell(3));
                String po = String.valueOf(row.getCell(8));
                String client_po = String.valueOf(row.getCell(9));
                System.out.println(ship_po + "==" + po + "==" + client_po);
                if ((!ship_po.equals(po)) && (!ship_po.equals(client_po)))
                    return AjaxResult.error("走货PO" + ship_po + "与库存PO" + po + "--" + client_po + "不相等");

                long ship_quantity = (long) row.getCell(5).getNumericCellValue();
                long quantity = Long.parseLong(row.getCell(12).getStringCellValue());
                if (ship_quantity != quantity)
                    return AjaxResult.error("走货数量" + ship_quantity + "与库存数量" + quantity + "不相等");

                // 判SAP数据
                String shipmentNo = String.valueOf(row.getCell(0));
                String shipmentDate = sdf.format(row.getCell(1).getDateCellValue());
                SAPUtil sapUtil = new SAPUtil();
                List<FgShipmentInfo> list2 = sapUtil.Z_HTMES_ZSDSHIPLS_2(shipmentNo);
                System.out.println(sdf.format(list2.get(0).getShipmentDate()) + "==" + shipmentDate + "==" + row.getCell(1) + list2.get(0).getSapPn() + ship_pn);
                if (list2.size() == 0 || !shipmentDate.equals(sdf.format(list2.get(0).getShipmentDate())))
                    return AjaxResult.error("SAP不存在" + shipmentNo + ",或走货日期不相等，请检查");

                boolean flag = list2.stream().anyMatch(fgShipmentInfo -> (fgShipmentInfo.getSapPn() == null ? "" : fgShipmentInfo.getSapPn()).equals(ship_pn));
//                boolean flag = false;
//                for (int j = 0;j < list2.size();j++) {
//                    // 一直报空指针异常是因为有些空白栏位也获取了，因此需要判空
//                    System.out.println(list2.get(j).getSapPn());
//                    if (list2.get(j).getSapPn() != null && list2.get(j).getSapPn().equals(ship_pn)) {
//                        flag = true;
//                    }
//                }
                if (flag == false)
                    return AjaxResult.error(shipmentNo + "对应PN不存在SAP中，请检查");

                boolean flag2 = list2.stream().anyMatch(fgShipmentInfo -> (fgShipmentInfo.getPo() == null ? "" : fgShipmentInfo.getPo()).equals(ship_po));
//                boolean flag2 = false;
//                for (int j = 0;j < list2.size();j++) {
//                    if (list2.get(j).getPo().equals(ship_po)) {
//                        flag = true;
//                    }
//                }
                if (flag2 == false)
                    return AjaxResult.error(shipmentNo + "对应PO不存在SAP中，请检查");

                System.out.println("行数：" + row.getRowNum());
            }
            // 统一产生TO单
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String shipmentNo = String.valueOf(row.getCell(0));
                String carNo = String.valueOf(row.getCell(4) == null ? "" : row.getCell(4));
                String batch = String.valueOf(row.getCell(10));
                String pn = String.valueOf(row.getCell(6));
                String client_po = String.valueOf(row.getCell(9));
                long quantity = Long.parseLong(row.getCell(12).getStringCellValue());
                returnMessage = fgTolistService.uploadCK00(shipmentNo, carNo, batch, pn, client_po, quantity);
                if ("TO已生成".equals(returnMessage)) {
                    continue;
                } else {
                    return AjaxResult.error(returnMessage);
                }
            }

            System.out.println(returnMessage);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
        if ("TO已生成".equals(returnMessage)) {
            return AjaxResult.success("TO已生成！");
        } else {
            return AjaxResult.error("上传失败！");
        }

    }

}
