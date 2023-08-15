package com.honortone.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honortone.api.entity.Inventory;
import com.honortone.api.entity.InventoryOut;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OutStockMapper extends BaseMapper<Inventory> {

    @DS("slave_2")
    Inventory checkCreateTime(String cpno);

    @DS("slave_2")
    int updateStatus(String uid);

    @DS("slave_2")
    int insertInventoryOut(InventoryOut inventoryOut);

    @DS("slave_2")
    int deleteInventoryById(Long id);

    @DS("slave_2")
    List<Inventory> checkTimeInfo(String recTime);
}
