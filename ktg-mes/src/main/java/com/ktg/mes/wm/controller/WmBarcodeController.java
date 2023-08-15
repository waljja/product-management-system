package com.ktg.mes.wm.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.ktg.common.annotation.Log;
import com.ktg.common.constant.UserConstants;
import com.ktg.common.core.controller.BaseController;
import com.ktg.common.core.domain.AjaxResult;
import com.ktg.common.core.page.TableDataInfo;
import com.ktg.common.enums.BusinessType;
import com.ktg.common.utils.poi.ExcelUtil;
import com.ktg.mes.wm.domain.WmBarcode;
import com.ktg.mes.wm.service.IWmBarcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 条码清单Controller
 * 
 * @author yinjinlu
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/mes/wm/barcode")
public class WmBarcodeController extends BaseController
{
    @Autowired
    private IWmBarcodeService wmBarcodeService;

    @Autowired
    private HttpServletResponse response;

    /**
     * 查询条码清单列表
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:barcode:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmBarcode wmBarcode)
    {
        startPage();
        List<WmBarcode> list = wmBarcodeService.selectWmBarcodeList(wmBarcode);

        return getDataTable(list);
    }

    /**
     * 导出条码清单列表
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:barcode:export')")
    @Log(title = "条码清单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WmBarcode wmBarcode)
    {
        List<WmBarcode> list = wmBarcodeService.selectWmBarcodeList(wmBarcode);
        ExcelUtil<WmBarcode> util = new ExcelUtil<WmBarcode>(WmBarcode.class);
        util.exportExcel(response, list, "条码清单数据");
    }

    /**
     * 获取条码清单详细信息
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:barcode:query')")
    @GetMapping(value = "/{barcodeId}")
    public AjaxResult getInfo(@PathVariable("barcodeId") Long barcodeId)
    {
        return AjaxResult.success(wmBarcodeService.selectWmBarcodeByBarcodeId(barcodeId));
    }

    /**
     * 新增条码清单
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:barcode:add')")
    @Log(title = "条码清单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WmBarcode wmBarcode)
    {
        if(UserConstants.NOT_UNIQUE.equals(wmBarcodeService.checkBarcodeUnique(wmBarcode))){
            return AjaxResult.error("当前业务内容的条码已存在!");
        }

        String path =wmBarcodeService.generateBarcode(wmBarcode);
        System.out.println("path:"+path);
        wmBarcode.setBarcodeUrl(path);
        return toAjax(wmBarcodeService.insertWmBarcode(wmBarcode));
    }

    /**
     * 修改条码清单
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:barcode:edit')")
    @Log(title = "条码清单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WmBarcode wmBarcode)
    {
        if(UserConstants.NOT_UNIQUE.equals(wmBarcodeService.checkBarcodeUnique(wmBarcode))){
            return AjaxResult.error("当前业务内容的条码已存在!");
        }
        String path =wmBarcodeService.generateBarcode(wmBarcode);
        wmBarcode.setBarcodeUrl(path);
        return toAjax(wmBarcodeService.updateWmBarcode(wmBarcode));
    }

    /**
     * 删除条码清单
     */
    @PreAuthorize("@ss.hasPermi('mes:wm:barcode:remove')")
    @Log(title = "条码清单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{barcodeIds}")
    public AjaxResult remove(@PathVariable Long[] barcodeIds)
    {
        return toAjax(wmBarcodeService.deleteWmBarcodeByBarcodeIds(barcodeIds));
    }

    /**
     *
     */
    @Log(title = "打印功能")
    @GetMapping("/Print-order")
    public void Printorder() {
        try {
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/pdf");

            Document document = new Document(PageSize.A4, 20, 20, 50, 40);

            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

            // 打开文档
            document.open();
            Font font1 = new Font(BaseFont.createFont( "c://windows//fonts//simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED), 30);
            Font font4 = new Font(BaseFont.createFont( "c://windows//fonts//simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED), 16, Font.UNDERLINE);//下划线
            Font font2 = new Font(BaseFont.createFont( "c://windows//fonts//simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED), 16);

            Paragraph p1 = new Paragraph("成品送检单", font1);
            p1.setLeading(39);//行间距
            p1.setAlignment(Element.ALIGN_CENTER);//居中
            document.add(p1);

            PdfPTable table = new PdfPTable(200);
            table.setWidthPercentage(100);//总宽度100%
            table.setSpacingBefore(20);//上边距
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("型号:"+ "\r\n"+ "Mode1", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("生产线:"+ "\r\n"+ "Line", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("工单:"+ "\r\n"+ "WO No", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("客户订单编号:"+ "\r\n"+ "PO NP", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("卡板号:"+ "\r\n"+ "pallet No    ", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("批量:"+ "\r\n"+ "Lot Qty", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("数量:"+ "\r\n"+ "Qty", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("物料文件:"+ "\r\n"+ "MatenialDocument", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("批次号:"+ "\r\n"+ "Lot No", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("生产日期:"+ "\r\n"+ "Production Date", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("卡板数:"+ "\r\n"+ "pallet Item", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("开单人员:", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("检验结果:"+ "\r\n"+ "QA Inspection Result", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("客户检查结果:"+ "\r\n"+ "Customer inspection", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("检验员:"+ "\r\n"+ "QA Inspector", font2));
            cell.setColspan(50);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            //cell.disableBorderSide(15);//隐藏边框
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("", font2));
            cell.setColspan(150);//合并列
            cell.setPadding(5);//内边距
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);//设置水平居中
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);//设置垂直居中
            table.addCell(cell);


            document.add(table);

            //=====读入并设置印章图片============================
            Image image = Image.getInstance("http://172.31.2.131:9000/ht-mes/2023/03/18/sdad_20230318100940A001.png");
            image.setScaleToFitLineWhenOverflow(true);
            image.setAlignment(Element.ALIGN_RIGHT);
            float x = 635.0F;
            float y = 758.0F;
            /*System.out.println("getTotalWidth:"+table.getTotalHeight());
            System.out.println("getTotalWidth:"+table.getTotalWidth());
            System.out.println("X:"+x);
            System.out.println("Y:"+y);*/
//
            image.setAbsolutePosition(x-200 , y);
            image.scaleAbsolute(80, 80);
            PdfContentByte pcb = writer.getDirectContentUnder();
            pcb.addImage(image);
            document.add(image);
            document.add(Chunk.NEWLINE);

            Image image1 = Image.getInstance("http://172.31.2.131:9000/ht-mes/Log.png");
            image1.setScaleToFitLineWhenOverflow(true);
            image1.setAlignment(Element.ALIGN_RIGHT);
            float x1 = 225.0F;
            float y1 = 758.0F;
            /*System.out.println("X1:"+x1);
            System.out.println("Y1:"+y1);*/
//
            image1.setAbsolutePosition(x1-200 , y1);
            image1.scaleAbsolute(160, 80);
            PdfContentByte pcb1 = writer.getDirectContentUnder();
            pcb1.addImage(image1);
            document.add(image1);
            document.add(Chunk.NEWLINE);
            //===================================================

            document.close();
        }catch (Exception e){
            System.out.println("55555555");
            e.printStackTrace();
        }

    }
}
