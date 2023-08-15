package com.ktg.mes.fg.controller;

import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.core.page.TableDataInfo;
import com.ktg.common.enums.BusinessType;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.mes.fg.domain.FgToList;
import com.ktg.mes.fg.service.FgTolistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    /**
     * 查询TO明细列表
     */
    @PreAuthorize("@ss.hasPermi('fg:tolist:list')")
    @GetMapping("/list")
    public TableDataInfo list(FgToList fgTolist)
    {
        startPage();
        List<FgToList> list = fgTolistService.selectFgTolistList(fgTolist);
        //List<FgToList> list = fgTolistService.selectFgTolistList2(fgTolist);
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
}
