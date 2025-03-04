package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BaseEducationMapper;
import com.glface.modules.sp.model.BaseEducation;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 学历信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseEducationService {
    @Resource
    private BaseEducationMapper educationMapper;

    public BaseEducation get(String id){
        return educationMapper.selectById(id);
    }

    public List<BaseEducation> all(){
        LambdaQueryWrapper<BaseEducation> queryWrapper = Wrappers.<BaseEducation>query().lambda();
        queryWrapper.eq(BaseEducation::getDelFlag,BaseEducation.DEL_FLAG_NORMAL).orderByAsc(BaseEducation::getSort);
        return educationMapper.selectList(queryWrapper);
    }


    public Page<BaseEducation> pageSearch(Page<BaseEducation> page, BaseEducation bean) {
        page.setCount(educationMapper.pageSearchCount(bean));
        page.setList(educationMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_EDUCATION_NAME_REQUIRED);
        }

        BaseEducation model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_EDUCATION_NAME_EXIST);
        }

        // 创建
        model = new BaseEducation();
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        educationMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name,int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BaseEducation model = get(id);
        if (model == null) {
            throw new ServiceException(SP_EDUCATION_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_EDUCATION_NAME_REQUIRED);
        }

        BaseEducation mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_EDUCATION_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        educationMapper.updateById(model);
    }

    public BaseEducation findByName(String name){
        LambdaQueryWrapper<BaseEducation> queryWrapper = Wrappers.<BaseEducation>query().lambda()
                .eq(BaseEducation::getName, name)
                .eq(BaseEducation::getDelFlag,BaseEducation.DEL_FLAG_NORMAL);
        return educationMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        educationMapper.deleteById(id);
    }

 }
