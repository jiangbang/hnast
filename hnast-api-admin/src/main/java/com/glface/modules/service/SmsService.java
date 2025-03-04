package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.model.SysSms;
import com.glface.modules.mapper.SmsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SmsService {

    @Resource
    private SmsMapper smsMapper;

    public SysSms get(String id){
        return smsMapper.selectById(id);
    }

    /**
     * 分页查找
     */
    public Page<SysSms> find(Page<SysSms> page) {
        LambdaQueryWrapper<SysSms> queryWrapper = Wrappers.<SysSms>query().lambda();

        //计算总数
        if(!page.isNotCount()){
            page.setCount(smsMapper.selectCount(queryWrapper));
        }
        //添加排序
        String lastSql = "";
        if(StringUtils.isNotEmpty(page.getOrderBy())){
            lastSql = " order by "+page.getOrderBy();
        }
        //添加分页
        lastSql = lastSql +" "+page.toLimit();
        queryWrapper.last(lastSql);//此方法只有最后一次调用的生效
        page.setList(smsMapper.selectList(queryWrapper));

        return page;
    }

    @Transactional
    public void delete(String id) {
        smsMapper.deleteById(id);
    }

 }
