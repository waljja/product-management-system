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
 * 库存实体类
 *
 * @author 丁国钊
 * @date 2023-08-31-13:22
 */
@Setter
@Getter
@EqualsAndHashCode
@HeadRowHeight(60)
@ContentRowHeight(20)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, verticalAlignment = VerticalAlignmentEnum.CENTER)
public class StorageReport {

    /**
     * 零部件号
     */
    @ColumnWidth(17)
    @ExcelProperty({"PartNumber"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String partNumber;

    /**
     * 订单号
     */
    @ColumnWidth(12)
    @ExcelProperty({"订单号"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String po;

    /**
     * 批次
     */
    @ColumnWidth(11)
    @ExcelProperty({"批次号"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String batch;

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
     * 库位
     */
    @ColumnWidth(25)
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
     * 工单
     */
    @ColumnWidth(12)
    @ExcelProperty({"工单"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String wo;

    /**
     * 收货时间
     */
    @ColumnWidth(17)
    @ExcelProperty({"收货时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private Date recTime;

    /**
     * 厂区
     */
    @ColumnWidth(8)
    @ExcelProperty({"厂区"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String plant;

    /**
     * 入库员工
     */
    @ColumnWidth(15)
    @ExcelProperty({"入库员工"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private String createUser;

    /**
     * 入库时间
     */
    @ColumnWidth(17)
    @ExcelProperty({"入库时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 44)
    private Date createTime;

}
