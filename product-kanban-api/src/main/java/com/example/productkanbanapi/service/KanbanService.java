package com.example.productkanbanapi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.NotInStorage;
import com.example.productkanbanapi.entity.Shipment;

import java.util.List;

/**
 * KanbanService
 *
 * @author 丁国钊
 * @date 2023-08-14-16:57
 */
public interface KanbanService {

    /**
     * 获取成品待入库数据（分页）
     *
     * @param current   当前页码
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param pnList    物料号
     * @param stateList 状态
     * @param woList    工单
     * @return 成品待入库数据
     */
    Page<NotInStorage> getStorageList(int current, String startDate, String endDate,
                                      List<String> pnList, List<String> stateList, List<String> woList);

    /**
     * 获取成品库存数据
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 成品库存数据
     */
    List<NotInStorage> getAllStorageList(String startDate, String endDate);

    /**
     * 获取成品待出货数据
     *
     * @param current            当前页码
     * @param startDate          开始时间
     * @param endDate            结束时间
     * @param shipmentNumberList
     * @param stateList
     * @param clientCodeList
     * @return 成品待出货数据
     */
    Page<Shipment> getShipmentList(int current, String startDate, String endDate,
                                   List<String> shipmentNumberList, List<String> stateList, List<String> clientCodeList);

    /**
     * 获取成品待出货数据
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 成品待出货数据
     */
    List<Shipment> getAllShipmentList(String startDate, String endDate);

}
