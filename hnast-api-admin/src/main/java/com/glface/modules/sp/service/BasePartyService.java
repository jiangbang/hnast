package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BasePartyMapper;
import com.glface.modules.sp.model.BaseParty;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 党派
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BasePartyService {
    @Resource
    private BasePartyMapper basePartyMapper;

    public BaseParty get(String id) {
        return basePartyMapper.selectById(id);
    }

    public List<BaseParty> all() {
        LambdaQueryWrapper<BaseParty> queryWrapper = Wrappers.<BaseParty>query().lambda();
        queryWrapper.eq(BaseParty::getDelFlag, BaseParty.DEL_FLAG_NORMAL).orderByAsc(BaseParty::getSort);
        return basePartyMapper.selectList(queryWrapper);
    }

    public Page<BaseParty> pageSearch(Page<BaseParty> page, BaseParty bean) {
        page.setCount(basePartyMapper.pageSearchCount(bean));
        page.setList(basePartyMapper.pageSearch(page, bean));
        return page;
    }

    /**
     * 通过名称查询
     */
    public BaseParty getByName(String name) {
        LambdaQueryWrapper<BaseParty> queryWrapper = Wrappers.<BaseParty>query().lambda()
                .eq(BaseParty::getName, name)
                .eq(BaseParty::getDelFlag, BaseParty.DEL_FLAG_NORMAL);
        return basePartyMapper.selectOne(queryWrapper);
    }

    /**
     * 新增
     *
     * @param name 名称
     * @param sort 排序(升序)
     */
    @Transactional
    public void create(String name, String remark,Integer sort) {
        //参数验证
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(BASEPARTY_NAME_REQUIRED);
        }

        // 验证name是否唯一
        BaseParty model = getByName(name);
        if (model != null) {
            throw new ServiceException(BASEPARTY_NAME_EXIST);
        }
        // 创建
        BaseParty baseParty = new BaseParty();
        baseParty.setName(name);
        baseParty.setSort(sort);
        baseParty.setRemark(remark);
        UserUtils.preAdd(baseParty);
        basePartyMapper.insert(baseParty);
    }

    /**
     * 编辑
     *
     * @param name 名称
     * @param sort 排序(升序)
     */
    @Transactional
    public void update(String id, String name, String remark,Integer sort) {
        //参数验证
        BaseParty baseParty = get(id);
        if (baseParty == null) {
            throw new ServiceException(BASEPARTY_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(BASEPARTY_NAME_REQUIRED);
        }

        // 验证name是否唯一
        BaseParty model2 = getByName(name);
        if (model2 != null && !baseParty.getId().equals(model2.getId())) {
            throw new ServiceException(BASEPARTY_NAME_EXIST);
        }
        // 修改
        baseParty.setName(name);
        baseParty.setSort(sort);
        baseParty.setRemark(remark);
        UserUtils.preUpdate(baseParty);
        basePartyMapper.updateById(baseParty);
    }

    @Transactional
    public void delete(String id) {
        basePartyMapper.deleteById(id);
    }
}
