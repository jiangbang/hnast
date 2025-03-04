package com.glface.modules.sp.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseParty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * 党派
 */
@Mapper
public interface BasePartyMapper extends BaseMapper<BaseParty> {
    /**
     * 分页查询
     */
    List<BaseParty> pageSearch(@Param("page") Page<BaseParty> page, @Param("bean") BaseParty baseParty);
    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseParty baseParty);
}