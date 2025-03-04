package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.model.SysDict;
import com.glface.modules.mapper.DictMapper;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DictService {
    @Resource
    private DictMapper dictMapper;

    public SysDict get(String id){
        return dictMapper.selectById(id);
    }

    public SysDict getByTypeAndLabel(String type,String label){
        LambdaQueryWrapper<SysDict> queryWrapper = Wrappers.<SysDict>query().lambda();
        queryWrapper.eq(SysDict::getType, type);
        queryWrapper.eq(SysDict::getLabel, label);
        //不需要加delFlag条件 查询时会自带
        return dictMapper.selectOne(queryWrapper);
    }

    public SysDict getByTypeAndValue(String type,String value){
        LambdaQueryWrapper<SysDict> queryWrapper = Wrappers.<SysDict>query().lambda();
        queryWrapper.eq(SysDict::getType, type);
        queryWrapper.eq(SysDict::getValue, value);
        //不需要加delFlag条件 查询时会自带
        return dictMapper.selectOne(queryWrapper);
    }

    /**
     * 分页查找
     */
    public Page<SysDict> find(Page<SysDict> page, SysDict dict){
        LambdaQueryWrapper<SysDict> queryWrapper = Wrappers.<SysDict>query().lambda();
        if(dict!=null){
            if (StringUtils.isNotEmpty(dict.getType())) {
                queryWrapper.eq(SysDict::getType, dict.getType());
            }
            if (StringUtils.isNotEmpty(dict.getRemark())) {
                queryWrapper.like(SysDict::getRemark, dict.getRemark());
            }
        }
        //计算总数
        if(!page.isNotCount()){
            page.setCount(dictMapper.selectCount(queryWrapper));
        }
        //添加排序
        String lastSql = "";
        if(StringUtils.isNotEmpty(page.getOrderBy())){
            lastSql = " order by "+page.getOrderBy();
        }
        //添加分页
        lastSql = lastSql +" "+page.toLimit();
        queryWrapper.last(lastSql);//此方法只有最后一次调用的生效
        page.setList(dictMapper.selectList(queryWrapper));

        return page;
    }

    @Transactional(readOnly = false)
    public void create(String type,String label,String value,Integer sort,String remark) throws ServiceException {
        if(StringUtils.isBlank(label) || label.length() > 100){
            throw new ServiceException(ApiCode.PERMISSION_DICT_LABEL_VERIFY);
        }
        if(StringUtils.isBlank(value) || value.length() > 100){
            throw new ServiceException(ApiCode.PERMISSION_DICT_VALUE_VERIFY);
        }
        if(StringUtils.isBlank(type) || type.length() > 100){
            throw new ServiceException(ApiCode.PERMISSION_DICT_TYPE_VERIFY);
        }
        if(StringUtils.isNotBlank(remark) && remark.length() > 255){
            throw new ServiceException(ApiCode.INFO_CONFIG_REMARK_VERIFY);
        }
        SysDict dict = getByTypeAndLabel(type,label);
        if(dict!=null){
            throw new ServiceException(ApiCode.PERMISSION_DICT_EXIST);
        }
        dict = new SysDict();
        dict.setLabel(label);
        dict.setType(type);
        dict.setValue(value);
        dict.setSort(sort);
        dict.setRemark(remark);
        UserUtils.preAdd(dict);
        dictMapper.insert(dict);
    }

    @Transactional(readOnly = false)
    public void update(String id,String type,String label,String value,Integer sort,String remark) throws ServiceException {
        if(StringUtils.isBlank(label) || label.length() > 100){
            throw new ServiceException(ApiCode.PERMISSION_DICT_LABEL_VERIFY);
        }
        if(StringUtils.isBlank(value) || value.length() > 100){
            throw new ServiceException(ApiCode.PERMISSION_DICT_VALUE_VERIFY);
        }
        if(StringUtils.isBlank(type) || type.length() > 100){
            throw new ServiceException(ApiCode.PERMISSION_DICT_TYPE_VERIFY);
        }
        if(StringUtils.isNotBlank(remark) && remark.length() > 255){
            throw new ServiceException(ApiCode.INFO_CONFIG_REMARK_VERIFY);
        }
        SysDict dict = get(id);
        if(dict==null){
            throw new ServiceException(ApiCode.PERMISSION_DICT_NOTEXIST);
        }

        SysDict dict2 = getByTypeAndLabel(type,label);
        if(dict2!=null&&!dict2.getId().equals(dict.getId())){
            throw new ServiceException(ApiCode.PERMISSION_DICT_EXIST);
        }

        dict.setLabel(label);
        dict.setRemark(remark);
        dict.setType(type);
        dict.setValue(value);
        dict.setSort(sort);
        UserUtils.preUpdate(dict);
        dictMapper.updateById(dict);
    }

    public Set<String> allTypes(){
        return dictMapper.allTypes();
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        dictMapper.deleteById(id);
    }

}
