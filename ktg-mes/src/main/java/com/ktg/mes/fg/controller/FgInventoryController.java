package com.ktg.mes.fg.controller;

import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.core.page.TableDataInfo;
import com.ktg.common.enums.BusinessType;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.mes.fg.domain.FgInventory;
import com.ktg.mes.fg.service.FgInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 成品上架清单Controller
 * @author JiangTingming
 * @date 2023-05-05 start
 */
@RestController
@RequestMapping("/fg/inventory")
public class FgInventoryController extends BaseController {
    @Autowired
    private FgInventoryService fgInventoryService;

    /**
     * 查询成品库存列表
     */
    @PreAuthorize("@ss.hasPermi('fg:inventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FgInventory fgInventory)
    {
        startPage();
        List<FgInventory> list = fgInventoryService.selectFgInventoryList(fgInventory);
        return getDataTable(list);
    }

    /**
     * 导出成品库存列表
     */
    @PreAuthorize("@ss.hasPermi('fg:inventory:export')")
    @Log(title = "成品库存", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FgInventory fgInventory)
    {
        List<FgInventory> list = fgInventoryService.selectFgInventoryList(fgInventory);
        ExcelUtil<FgInventory> util = new ExcelUtil<FgInventory>(FgInventory.class);
        util.exportExcel(response, list, "成品库存数据");
    }

    /**
     * 获取成品库存详细信息
     */
    @PreAuthorize("@ss.hasPermi('fg:inventory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(fgInventoryService.selectFgInventoryById(id));
    }

    /**
     * 新增成品库存
     */
    @PreAuthorize("@ss.hasPermi('fg:inventory:add')")
    @Log(title = "成品库存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FgInventory fgInventory)
    {
        return toAjax(fgInventoryService.insertFgInventory(fgInventory));
    }

    /**
     * 修改成品库存
     */
    @PreAuthorize("@ss.hasPermi('fg:inventory:edit')")
    @Log(title = "成品库存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FgInventory fgInventory)
    {
        return toAjax(fgInventoryService.updateFgInventory(fgInventory));
    }

    /**
     * 删除成品库存
     */
    @PreAuthorize("@ss.hasPermi('fg:inventory:remove')")
    @Log(title = "成品库存", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(fgInventoryService.deleteFgInventoryByIds(ids));
    }
}
