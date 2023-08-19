package com.example.productkanbanapi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.productkanbanapi.entity.ProductNotInStorage;

/**
 * KanbanService
 *
 * @author 丁国钊
 * @date 2023-08-14-16:57
 */
public interface KanbanService {

    /**
     * 获取成品入库数据
     *
     * @param current 当前页码
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 成品入库数据
     */
    Page<ProductNotInStorage> getStorageList(int current, String startDate, String endDate);

}
