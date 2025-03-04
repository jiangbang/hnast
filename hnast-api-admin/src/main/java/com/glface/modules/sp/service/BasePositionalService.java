package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BasePositionalMapper;
import com.glface.modules.sp.model.BasePositional;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 职称信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BasePositionalService {
    @Resource
    private BasePositionalMapper positionalMapper;

    public BasePositional get(String id){
        return positionalMapper.selectById(id);
    }

    public List<BasePositional> all(){
        LambdaQueryWrapper<BasePositional> queryWrapper = Wrappers.<BasePositional>query().lambda();
        queryWrapper.eq(BasePositional::getDelFlag,BasePositional.DEL_FLAG_NORMAL).orderByAsc(BasePositional::getSort);
        return positionalMapper.selectList(queryWrapper);
    }


    public Page<BasePositional> pageSearch(Page<BasePositional> page, BasePositional bean) {
        page.setCount(positionalMapper.pageSearchCount(bean));
        page.setList(positionalMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_POSITIONAL_NAME_REQUIRED);
        }

        BasePositional model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_POSITIONAL_NAME_EXIST);
        }

        // 创建
        model = new BasePositional();
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        positionalMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name,int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BasePositional model = get(id);
        if (model == null) {
            throw new ServiceException(SP_POSITIONAL_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_POSITIONAL_NAME_REQUIRED);
        }

        BasePositional mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_POSITIONAL_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        positionalMapper.updateById(model);
    }

    public BasePositional findByName(String name){
        LambdaQueryWrapper<BasePositional> queryWrapper = Wrappers.<BasePositional>query().lambda()
                .eq(BasePositional::getName, name)
                .eq(BasePositional::getDelFlag,BasePositional.DEL_FLAG_NORMAL);
        return positionalMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        positionalMapper.deleteById(id);
    }

 }
