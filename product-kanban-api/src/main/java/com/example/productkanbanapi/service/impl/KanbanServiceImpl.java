package com.example.productkanbanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.NotInStorage;
import com.example.productkanbanapi.entity.Shipment;
import com.example.productkanbanapi.entity.TosShipInfo;
import com.example.productkanbanapi.mapper.KanbanMapper;
import com.example.productkanbanapi.service.KanbanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * KanbanServiceImpl
 *
 * @author 丁国钊
 * @date 2023-08-14-16:57
 */
@Slf4j
@Service
public class KanbanServiceImpl implements KanbanService {

    @Autowired
    KanbanMapper kanbanMapper;

    @Override
    public Page<NotInStorage> getStorageList(int current, String startDate, String endDate, List<String> pnList, List<String> stateList, List<String> woList) {
        boolean sNotNull = startDate != null;
        boolean eNoTNull = endDate != null;
        Page<NotInStorage> productNotInStoragePage;
        Page<NotInStorage> inventoryPage = new Page<>(current, 20);
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
        }
        productNotInStoragePage = kanbanMapper.findNotInStockU(inventoryPage, startDate, endDate, pnList, stateList, woList);
        return productNotInStoragePage;
    }

    @Override
    public List<NotInStorage> getAllStorageList(String startDate, String endDate) {
        boolean sNotNull = startDate != null;
        boolean eNoTNull = endDate != null;
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
        }
        return kanbanMapper.findAllNotInStock(startDate, endDate);
    }

    @Override
    public Page<Shipment> getShipmentList(int current, String startDate, String endDate,
                                          List<String> shipmentNumberList, List<String> stateList, List<String> clientCodeList) {
        long s = System.currentTimeMillis();
        Page<Shipment> shipmentPage;
        // current -> 当前页码，每页 20 条数据
        Page<Shipment> page = new Page<>(current, 20);
        QueryWrapper<TosShipInfo> queryWrapper = new QueryWrapper<>();
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        // 根据日期范围查询，SQL Server 分页必须有 order 排序
        queryWrapper
                .eq("ship1.status", 1)
                .and(i -> i.eq("last_comfirm", "货仓")
                        .or(i1 -> i1.eq("last_comfirm", "船务")))
                .and(i -> i.eq("tos1.status", 0)
                        .or(i1 -> i1.eq("tos1.status", 1))
                        .or(i2 -> i2.eq("tos1.status", 2))
                        .or(i2 -> i2.eq("tos1.status", 3)))
                .and(i -> i.notExists("select shipment_no\n" +
                        "               from fg_tos tos2\n" +
                        "                        left join fg_shipmentinfo ship2 on tos2.shipment_no = ship2.shipment_number\n" +
                        "               where is_loading_truck = '已装车'\n" +
                        "                 and tos2.shipment_no = tos1.shipment_no"));
        if (!CollectionUtils.isEmpty(shipmentNumberList)) {
            queryWrapper.in("tos1.shipment_no", shipmentNumberList);
        }
        if (!CollectionUtils.isEmpty(clientCodeList)) {
            queryWrapper.in("client_code", clientCodeList);
        }
        if (!CollectionUtils.isEmpty(stateList)) {
            queryWrapper.in("(case\n" +
                    "         when tos1.status = 0 then '待拣货'\n" +
                    "         when tos1.status = 1 then '备货中'\n" +
                    "         when tos1.status = 2 and tos1.stock is not null and tos1.is_loading_truck = '待装车' then '待装车'\n" +
                    "         when tos1.status = 2 and tos1.stock is null then '待绑定出货区'\n" +
                    "         when tos1.status = 3 then '欠货'\n" +
                    "    end)", stateList);
        }
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            queryWrapper
                    .apply("shipment_date >= str_to_date('" + startDate + "', '%Y-%m-%d %H:%i:%s')")
                    .apply("shipment_date <= str_to_date('" + endDate + "', '%Y-%m-%d %H:%i:%s')")
                    .orderByAsc("shipment_date", "time(loading_time)");
        } else {
            queryWrapper
                    .apply("createdate >= '2023-10-25'")
                    .orderByAsc("shipment_date", "time(loading_time)");
        }
        shipmentPage = kanbanMapper.findShipment(page, queryWrapper);
        // 获取每张出货单的所有零部件号
        for (Shipment shipment : shipmentPage.getRecords()) {
            QueryWrapper<TosShipInfo> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("shipment_number", shipment.getShipmentNo());
            List<String> pnList = kanbanMapper.findPnByShipNo(queryWrapper2);
            shipment.setPartNumberList(pnList);
        }
        long e = System.currentTimeMillis();
        log.info("getShipmentList 耗时：{}", e - s);
        return shipmentPage;
    }

    @Override
    public List<Shipment> getAllShipmentList(String startDate, String endDate) {
        List<Shipment> shipmentList;
        QueryWrapper<TosShipInfo> queryWrapper = new QueryWrapper<>();
        // 开始日期非空
        boolean sNotNull = startDate != null;
        // 结束日期非空
        boolean eNoTNull = endDate != null;
        /*
         * 日期都不为空 -> 按时间范围查询
         * 有一个为空 -> 不按时间条件查询
         */
        if (sNotNull && eNoTNull) {
            startDate += " 00:00:00.000";
            endDate += " 23:59:59.999";
            // 根据日期范围查询，SQL Server 分页必须有 order 排序
            queryWrapper
                    .eq("ship1.status", 1)
                    .and(i -> i.eq("last_comfirm", "货仓")
                            .or(i1 -> i1.eq("last_comfirm", "船务")))
                    .and(i -> i.eq("tos1.status", 0)
                            .or(i1 -> i1.eq("tos1.status", 1))
                            .or(i2 -> i2.eq("tos1.status", 2))
                            .or(i2 -> i2.eq("tos1.status", 3)))
                    .and(i -> i.notExists("select shipment_no\n" +
                            "               from fg_tos tos2\n" +
                            "                        left join fg_shipmentinfo ship2 on tos2.shipment_no = ship2.shipment_number\n" +
                            "               where is_loading_truck = '已装车'\n" +
                            "                 and tos2.shipment_no = tos1.shipment_no"))
                    .apply("shipment_date >= str_to_date('" + startDate + "', '%Y-%m-%d %H:%i:%s')")
                    .apply("shipment_date <= str_to_date('" + endDate + "', '%Y-%m-%d %H:%i:%s')")
                    .orderByAsc("shipment_date", "time(loading_time)");
        } else {
            queryWrapper
                    .eq("ship1.status", 1)
                    .and(i -> i.eq("last_comfirm", "货仓")
                            .or(i1 -> i1.eq("last_comfirm", "船务")))
                    .and(i -> i.eq("tos1.status", 0)
                            .or(i1 -> i1.eq("tos1.status", 1))
                            .or(i2 -> i2.eq("tos1.status", 2))
                            .or(i2 -> i2.eq("tos1.status", 3)))
                    .and(i -> i.notExists("select shipment_no\n" +
                            "               from fg_tos tos2\n" +
                            "                        left join fg_shipmentinfo ship2 on tos2.shipment_no = ship2.shipment_number\n" +
                            "               where is_loading_truck = '已装车'\n" +
                            "                 and tos2.shipment_no = tos1.shipment_no"))
                    .apply("createdate >= '2023-10-25'")
                    .orderByAsc("shipment_date", "time(loading_time)");
        }
        shipmentList = kanbanMapper.findAllShipment(queryWrapper);
        // 获取每张出货单的零部件号
        for (Shipment shipment : shipmentList) {
            QueryWrapper<TosShipInfo> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("shipment_number", shipment.getShipmentNo());
            List<String> pnList = kanbanMapper.findPnByShipNo(queryWrapper2);
            shipment.setPartNumberList(pnList);
        }
        return shipmentList;
    }

}
