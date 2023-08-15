package com.honortone.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honortone.api.entity.Inventory;

public interface OutStockService extends IService<Inventory> {

    public String outStock(String cpno);
}
