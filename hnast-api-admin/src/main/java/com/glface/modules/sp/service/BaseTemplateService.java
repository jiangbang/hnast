package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.modules.sp.mapper.BaseTemplateMapper;
import com.glface.modules.sp.model.BaseTemplate;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 模板信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseTemplateService {
    @Resource
    private BaseTemplateMapper templateMapper;

    public BaseTemplate get(String id){
        return templateMapper.selectById(id);
    }

    public List<BaseTemplate> all(){
        LambdaQueryWrapper<BaseTemplate> queryWrapper = Wrappers.<BaseTemplate>query().lambda();
        queryWrapper.eq(BaseTemplate::getDelFlag,BaseTemplate.DEL_FLAG_NORMAL).orderByAsc(BaseTemplate::getSort);
        return templateMapper.selectList(queryWrapper);
    }


    public Page<BaseTemplate> pageSearch(Page<BaseTemplate> page, BaseTemplate bean) {
        page.setCount(templateMapper.pageSearchCount(bean));
        page.setList(templateMapper.pageSearch(page,bean));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name,String fileId, int sort, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_TEMPLATE_NAME_REQUIRED);
        }

        if (StringUtils.isBlank(fileId)) {
            throw new ServiceException(ApiCode.SP_TEMPLATE_FILEID_REQUIRED);
        }

        BaseTemplate model = findByName(name);
        if(model!=null){
            throw new ServiceException(SP_TEMPLATE_NAME_EXIST);
        }

        // 创建
        model = new BaseTemplate();
        model.setName(name);
        model.setFileId(fileId);
        model.setSort(sort);
        model.setRemark(remark);
        UserUtils.preAdd(model);
        templateMapper.insert(model);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name,String fileId,int sort,String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        BaseTemplate model = get(id);
        if (model == null) {
            throw new ServiceException(SP_TEMPLATE_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(SP_TEMPLATE_NAME_REQUIRED);
        }

        if (StringUtils.isBlank(fileId)) {
            throw new ServiceException(ApiCode.SP_TEMPLATE_FILEID_REQUIRED);
        }

        BaseTemplate mode2 = findByName(name);
        if(mode2!=null&&!model.getId().equals(mode2.getId())){
            throw new ServiceException(SP_TEMPLATE_NAME_EXIST);
        }

        // 修改
        model.setName(name);
        model.setFileId(fileId);
        model.setSort(sort);
        model.setRemark(remark);
        //存储
        UserUtils.preUpdate(model);
        templateMapper.updateById(model);
    }

    public BaseTemplate findByName(String name){
        LambdaQueryWrapper<BaseTemplate> queryWrapper = Wrappers.<BaseTemplate>query().lambda()
                .eq(BaseTemplate::getName, name)
                .eq(BaseTemplate::getDelFlag,BaseTemplate.DEL_FLAG_NORMAL);
        return templateMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        templateMapper.deleteById(id);
    }

 }
