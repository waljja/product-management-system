package com.example.productkanbanapi.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.productkanbanapi.entity.NotInStorage;
import generator.domain.Fg313notrec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author dingguozhao
* @description 针对表【FG_313NotRec】的数据库操作Mapper
* @createDate 2024-01-23 09:56:49
* @Entity generator.domain.Fg313notrec
*/
@Mapper
@Repository
public interface Fg313notrecMapper extends BaseMapper<Fg313notrec> {

    /**
     * 插入转运未收货数据
     *
     * @return 成功/失败
     */
    @DS("sqlserver")
    int insert(@Param("notRec313List") List<NotInStorage> notRec313List);

}




