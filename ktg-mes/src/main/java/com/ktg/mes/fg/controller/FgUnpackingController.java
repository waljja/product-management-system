package com.ktg.mes.fg.controller;

import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.core.page.TableDataInfo;
import com.ktg.common.enums.BusinessType;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.mes.fg.domain.FgUnpacking;
import com.ktg.mes.fg.service.FgUnpackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 成品拆箱清单Controller
 * @author JiangTingming
 * @date 2023-05-05 start
 */
@RestController
@RequestMapping("/fg/unpacking")
public class FgUnpackingController extends BaseController {
    @Autowired
    private FgUnpackingService fgUnpackingService;

    /**
     * 查询拆分明细列表
     */
    @PreAuthorize("@ss.hasPermi('fg:unpacking:list')")
    @GetMapping("/list")
    public TableDataInfo list(FgUnpacking fgUnpacking)
    {
        startPage();
        System.out.println(fgUnpacking.getCreateTime());
        List<FgUnpacking> list = fgUnpackingService.selectFgUnpackingList(fgUnpacking);
        System.out.println(list);
        return getDataTable(list);
    }

    /**
     * 导出拆分明细列表
     */
    @PreAuthorize("@ss.hasPermi('fg:unpacking:export')")
    @Log(title = "拆分明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FgUnpacking fgUnpacking)
    {
        List<FgUnpacking> list = fgUnpackingService.selectFgUnpackingList(fgUnpacking);
        ExcelUtil<FgUnpacking> util = new ExcelUtil<FgUnpacking>(FgUnpacking.class);
        util.exportExcel(response, list, "拆分明细数据");
    }

    /**
     * 获取拆分明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('fg:unpacking:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(fgUnpackingService.selectFgUnpackingById(id));
    }

    /**
     * 新增拆分明细
     */
    @PreAuthorize("@ss.hasPermi('fg:unpacking:add')")
    @Log(title = "拆分明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FgUnpacking fgUnpacking)
    {
        return toAjax(fgUnpackingService.insertFgUnpacking(fgUnpacking));
    }

    /**
     * 修改拆分明细
     */
    @PreAuthorize("@ss.hasPermi('fg:unpacking:edit')")
    @Log(title = "拆分明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FgUnpacking fgUnpacking)
    {
        return toAjax(fgUnpackingService.updateFgUnpacking(fgUnpacking));
    }

    /**
     * 删除拆分明细
     */
    @PreAuthorize("@ss.hasPermi('fg:unpacking:remove')")
    @Log(title = "拆分明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(fgUnpackingService.deleteFgUnpackingByIds(ids));
    }
}
