package com.ktg.mes.fg.controller;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageHelper;
import com.ktg.common.utils.StringUtils;
import com.ktg.common.utils.sql.SqlUtil;
import com.ktg.mes.fg.domain.FgSealnoinfo;
import com.ktg.mes.fg.mapper.FgShipmentinfoByhandMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ktg.common.annotation.Log;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.enums.BusinessType;
import com.ktg.mes.fg.domain.FgShipmentinfoByhand;
import com.ktg.mes.fg.service.FgShipmentinfoByhandService;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 【请填写功能名称】Controller
 *
 * @author JiangTingming
 * @date 2023-05-17
 */
@RestController
@RequestMapping("/fg/byhand")
public class FgShipmentinfoByhandController extends BaseController {
    @Autowired
    private FgShipmentinfoByhandService fgShipmentinfoByhandService;

    @Autowired
    private FgShipmentinfoByhandMapper fgShipmentinfoByhandMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('fg:byhand:list')")
    @GetMapping("/list")
    public TableDataInfo list(FgShipmentinfoByhand fgShipmentinfoByhand) {
        startPage();
        List<FgShipmentinfoByhand> list = fgShipmentinfoByhandService.selectFgShipmentinfoByhandList(fgShipmentinfoByhand);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('fg:byhand:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FgShipmentinfoByhand fgShipmentinfoByhand) {
        List<FgShipmentinfoByhand> list = fgShipmentinfoByhandService.selectFgShipmentinfoByhandList(fgShipmentinfoByhand);
        ExcelUtil<FgShipmentinfoByhand> util = new ExcelUtil<FgShipmentinfoByhand>(FgShipmentinfoByhand.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
//    @PreAuthorize("@ss.hasPermi('fg:byhand:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id) {
//        return AjaxResult.success(fgShipmentinfoByhandService.selectFgShipmentinfoByhandById(id));
//    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('fg:byhand:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FgShipmentinfoByhand fgShipmentinfoByhand) {
        return toAjax(fgShipmentinfoByhandService.insertFgShipmentinfoByhand(fgShipmentinfoByhand));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('fg:byhand:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FgShipmentinfoByhand fgShipmentinfoByhand) {
        return toAjax(fgShipmentinfoByhandService.updateFgShipmentinfoByhand(fgShipmentinfoByhand));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('fg:byhand:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(fgShipmentinfoByhandService.deleteFgShipmentinfoByhandByIds(ids));
    }

    /**
     * 手动上传走货信息（该数据只用于人工操作，与系统库存表等数据无关）
     *
     * @param file
     */
    @Log(title = "手动上传走货资料", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult uploadByhand(@RequestParam("file") MultipartFile file) throws IOException {

        List<FgShipmentinfoByhand> list = new ArrayList<>();
//        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        // 定义批量插入 三个执行器SIMPLE、REUSE和BATCH。
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        try {
            if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith("xlsx")){
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("所用行数（下标）：" + sheet.getLastRowNum());

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                FgShipmentinfoByhand fgShipmentinfoByhand = new FgShipmentinfoByhand();
                System.out.println("行数：" + row.getRowNum());

                // 客户
                fgShipmentinfoByhand.setClient(row.getCell(1).getStringCellValue());
                // SAP型号
                fgShipmentinfoByhand.setSapPn(row.getCell(3).getStringCellValue());
                // 数量
                fgShipmentinfoByhand.setQuantity((long) row.getCell(8).getNumericCellValue());
                // 走货日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                Date date = sdf.parse(row.getCell(2).getStringCellValue());
                System.out.println(date);
                fgShipmentinfoByhand.setRecTime(date);
                // 车牌号
                fgShipmentinfoByhand.setCarNo(row.getCell(13).getStringCellValue());
                // 去重
                if (!isDataExist(fgShipmentinfoByhand)) {
                    list.add(fgShipmentinfoByhand);
                }
            }
            System.out.println(list);
            if (list.size() > 0) {
                int n = fgShipmentinfoByhandMapper.batchInsert(list);
                sqlSession.commit();
                if (n > 0) {
                    return AjaxResult.success("上传成功！");
                } else {
                    return AjaxResult.error("获取数据异常！");
                }
            } else {
                return AjaxResult.error("上传重复或上传格式错误！");
            }

        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        } finally {
            sqlSession.close();
            workbook.close();
        }
        return AjaxResult.success("上传失败！");

    }

    @GetMapping(value = "/{id}")
    public AjaxResult confirmStockup(@PathVariable("id") Long id) {
        System.out.println("id" + id);
        int n = fgShipmentinfoByhandService.updateStatusByid(id);
        // 返回值为负数原因是mybatis配置文件中defaultExecutorType值设置为BATCH 批量处理，改为SIMPLE即可
        // 需要批量处理的话使用 SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        System.out.println("sss" + n);
        if (n > 0) {
            return AjaxResult.success("已确认");
        } else {
            return AjaxResult.error("确认失败或重复确认！");
        }
    }

    /**
     * 查重操作
     *
     * @param fgShipmentinfoByhand
     * @return boolean
     **/
    private boolean isDataExist(FgShipmentinfoByhand fgShipmentinfoByhand) throws IOException {
//        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FgShipmentinfoByhandMapper mapper = sqlSession.getMapper(FgShipmentinfoByhandMapper.class);
            return mapper.getData(fgShipmentinfoByhand) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*********************************  维护封条号  *************************************/

    /**
     * 上传维护封条号
     *
     * @param file
     */
    @Log(title = "上传封条号", businessType = BusinessType.INSERT)
    @PostMapping("/uploadseal")
    public AjaxResult uploadSeal(@RequestParam("file") MultipartFile file) throws IOException {

        List<FgSealnoinfo> list = new ArrayList<>();
//        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        // 定义批量插入 三个执行器SIMPLE、REUSE和BATCH。
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        try {
            // 2007 -- 2007+ 版本
            if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith("xlsx")){
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("所用行数（下标）：" + sheet.getLastRowNum());

            // 从第二行开始获取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                FgSealnoinfo fgSealnoinfo = new FgSealnoinfo();
                System.out.println("行数：" + row.getRowNum());

                // 从第一个单元格开始（第一列）
                fgSealnoinfo.setSealNo(row.getCell(0).getStringCellValue());
                // 去重
                if (!isDataExist1(fgSealnoinfo)) {
                    list.add(fgSealnoinfo);
                }
            }
            System.out.println(list);
            if (list.size() > 0) {
                int n = fgShipmentinfoByhandMapper.batchInsert1(list);
                sqlSession.commit();
                if (n > 0) {
                    return AjaxResult.success("上传成功！");
                } else {
                    return AjaxResult.error("获取数据异常！");
                }
            } else {
                return AjaxResult.error("上传重复或上传格式错误！");
            }

        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        } finally {
            sqlSession.close();
            workbook.close();
        }
        return AjaxResult.success("上传失败！");

    }

    /**
     * 查重操作
     *
     * @param fgSealnoinfo
     * @return boolean
     **/
    private boolean isDataExist1(FgSealnoinfo fgSealnoinfo) throws IOException {
//        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FgShipmentinfoByhandMapper mapper = sqlSession.getMapper(FgShipmentinfoByhandMapper.class);
            return mapper.getData1(fgSealnoinfo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询封条装车信息列表
     */
    @GetMapping("/list2")
    public TableDataInfo list(FgSealnoinfo fgSealnoinfo)
    {
        // 注意若依框架相关bug：一个页面有多个分页，若使用自动分页startPage()方法执行分页，则默认使用第一个分页数据（pageNum页数和pageSize每页总数等），
        // 这样会导致其它分页功能不可用，
        // 解决方法：其它分页功能需在查询时手动传入分页数据，再判断如下逻辑，遵循若依框架使用的PageHelper构建分页
        if (StringUtils.isNotNull(fgSealnoinfo.getPageNum2()) && StringUtils.isNotNull(fgSealnoinfo.getPageSize2()))
        {
            PageHelper.startPage(fgSealnoinfo.getPageNum2(), fgSealnoinfo.getPageSize2());
        }
        List<FgSealnoinfo> list = fgShipmentinfoByhandMapper.selectFgSealnoinfoList(fgSealnoinfo);
        return getDataTable(list);
    }

    /**
     * 删除【删除封条号】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/seal/{ids}")
    public AjaxResult remove2(@PathVariable Long[] ids) {
        return toAjax(fgShipmentinfoByhandMapper.deleteFgSealnoinfoByIds(ids));
    }



}