package com.example.productkanbanapi.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * StockReport
 *
 * @author 丁国钊
 * @date 2023-08-23-15:32
 */
@Setter
@Getter
@EqualsAndHashCode
@HeadRowHeight(60)
@ContentRowHeight(20)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, verticalAlignment = VerticalAlignmentEnum.CENTER)
public class StockReport {

    /**
     * 序号
     */
    @ColumnWidth(7)
    @ExcelProperty({"序号"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private Integer item;

    /**
     * 型号
     */
    @ColumnWidth(17)
    @ExcelProperty({"PartNumber"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String partNumber;

    /**
     * UID
     */
    @ColumnWidth(18)
    @ExcelProperty({"UID"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String uid;

    /**
     * 工单
     */
    @ColumnWidth(12)
    @ExcelProperty({"工单"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String wo;

    /**
     * 批次
     */
    @ColumnWidth(9)
    @ExcelProperty({"批次号"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String batch;

    /**
     * 数量
     */
    @ColumnWidth(12)
    @ExcelProperty({"工单数量"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private Double quantity;

    /**
     * 厂区
     */
    @ColumnWidth(8)
    @ExcelProperty({"工厂"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String plant;

    /**
     * 库存位置
     */
    @ExcelProperty({"库位"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String storageLoc;

    /**
     * 状态
     */
    @ExcelProperty({"状态"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String state;

    /**
     * 接收时间
     */
    @ColumnWidth(18)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"收板时间"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recTime;

}
