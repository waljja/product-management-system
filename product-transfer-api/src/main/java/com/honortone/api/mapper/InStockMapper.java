package com.honortone.api.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.honortone.api.controller.dto.MaterialTransationsDto;
import com.honortone.api.entity.CheckList;
import com.honortone.api.entity.Inventory;
import com.honortone.api.entity.MaterialTransactions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InStockMapper extends BaseMapper<Inventory> {

    /**
     * @DS切换数据源
     *
     * **/
    @DS("slave_2")
    CheckList ifSuccess(String uid);

    /**
     * 是否存在数据（是否已收货）
     *
     * **/
    List<MaterialTransactions> checkInfoByUid(String uid);

    /**
     * 收货信息存到中间表
     *
     * **/
    @DS("slave_3")
    int toInsert(@Param("checkList") CheckList checkList, @Param("username") String username, @Param("FromStock") String FromStock, @Param("ToStock") String ToStock, @Param("RefDocNo") String RefDocNo, @Param("DocHeader") String DocHeader);

    /**
     * 对比批次总数与收货总数
     *
     * **/
    @DS("slave_3")
    float selectQuantity(MaterialTransationsDto materialTransationsDto);
//    @Select("select ISNULL(SUM(quantity), 0) FROM [HTMES_MES_Main].[dbo].[xTend_MaterialTransactions]\n" +
//            " where PartNumber = #{pn} and Plant = #{plant} and batch = #{batch} and TransactionType = #{transactiontype} and RecordStatus = 0")
    /**
     * 一并提交功能
     *
     * **/
    @DS("slave_3")
    int updateRecordStatus(MaterialTransationsDto materialTransationsDto);
    // int updateRecordStatus(MaterialTransationsDto materialTransationsDto);

    @DS("slave_2")
    int updateQAresult(@Param("pn") String pn, @Param("qaresult") int qaresult, @Param("plant") String plant, @Param("batch") String batch);

    @DS("slave_3")
    int checkInfoAlreadyTransfer(MaterialTransationsDto materialTransationsDto);
}
