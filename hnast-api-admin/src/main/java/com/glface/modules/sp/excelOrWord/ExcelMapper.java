package com.glface.modules.sp.excelOrWord;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.modules.sp.model.ExpertCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Coovright (C), 2020-2023
 * FileName: ExcelMapper
 * Author: wanluixng
 * Date: 2023/4/21 11:36
 * Description:
 * History:
 * <author>  <time>  <version> <desc>
 * 作者姓名   修改时间    版本号    描述
 */
@Mapper
@Repository
public interface ExcelMapper extends BaseMapper<Excel> {
    List<Excel> searchCategoryId(List<String> categories);

    List<Excel> searchExpertId(List<String> names);

    List<ExpertCategory> queryIsNull(String expertId);

    String queryEducationId(String name);
}
