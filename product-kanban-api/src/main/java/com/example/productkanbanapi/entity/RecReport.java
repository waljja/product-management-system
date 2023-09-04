package com.example.productkanbanapi.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 收获报表实体类
 *
 * @author 丁国钊
 * @date 2023-08-31-13:57
 */
@Setter
@Getter
@EqualsAndHashCode
@HeadRowHeight(60)
@ContentRowHeight(20)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, verticalAlignment = VerticalAlignmentEnum.CENTER)
public class RecReport {

    /**
     * 批次
     */
    @ColumnWidth(11)
    @ExcelProperty({"批次号"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String batch;

    /**
     * 零部件号
     */
    @ColumnWidth(17)
    @ExcelProperty({"PartNumber"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String partNumber;

    /**
     * uid
     */
    @ColumnWidth(20)
    @ExcelProperty({"UID"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String uid;

    /**
     * 数量
     */
    @ColumnWidth(12)
    @ExcelProperty({"数量"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private Double quantity;

    /**
     * 发出仓位
     */
    @ColumnWidth(8)
    @ExcelProperty({"发出库位"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String fromStock;

    /**
     * 接收仓位
     */
    @ColumnWidth(8)
    @ExcelProperty({"接收库位"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)private String toStock;

    /**
     * 过账用户
     */
    @ColumnWidth(15)
    @ExcelProperty({"过账操作人"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String transactionUser;

    /**
     * 过账时间
     */
    @ColumnWidth(17)
    @ExcelProperty({"过账时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private Date transactionTime;

    /**
     * 过账类型
     */
    @ColumnWidth(8)
    @ExcelProperty({"过账类型"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String transactionType;

    /**
     * 过账编号
     */
    @ColumnWidth(12)
    @ExcelProperty({"过账编号"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String refDocNo;

    /**
     * 过账年份
     */
    @ColumnWidth(8)
    @ExcelProperty({"过账年份"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String docHeader;

    /**
     * 厂区
     */
    @ColumnWidth(8)
    @ExcelProperty({"厂区"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String plant;

    /**
     * 工单
     */
    @ColumnWidth(12)
    @ExcelProperty({"工单"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String wo;

}
