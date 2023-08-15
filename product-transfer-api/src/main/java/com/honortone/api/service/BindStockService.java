package com.honortone.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honortone.api.entity.Inventory;
import org.apache.ibatis.annotations.Param;

public interface BindStockService extends IService<Inventory> {

    public String bindStock_1(String uid,String stock, String username);

    public String bindStock_2(String uid,String tz,String stock,String htpn,String khpn,String rectime, long qty, String clientBatch);

    String firstInsert(String uid);

    String RollBack(String uid, String rollbackReason);

    int updateTosStock(String toNo, String stock);

    String checkBHandQH(String toNo);
}
