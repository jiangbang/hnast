package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.model.SysLog;
import com.glface.model.SysRole;
import com.glface.modules.mapper.LogMapper;
import com.glface.modules.model.ProjectBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LogService {

    @Resource
    private LogMapper logMapper;

    public SysLog get(String id){
        return logMapper.selectById(id);
    }

    /**
     * 分页查找
     */
    public Page<SysLog> find(Page<SysLog> page) {
        LambdaQueryWrapper<SysLog> queryWrapper = Wrappers.<SysLog>query().lambda();

        //计算总数
        if(!page.isNotCount()){
            page.setCount(logMapper.selectCount(queryWrapper));
        }
        //添加排序
        String lastSql = "";
        if(StringUtils.isNotEmpty(page.getOrderBy())){
            lastSql = " order by "+page.getOrderBy();
        }
        //添加分页
        lastSql = lastSql +" "+page.toLimit();
        queryWrapper.last(lastSql);//此方法只有最后一次调用的生效
        page.setList(logMapper.selectList(queryWrapper));

        return page;
    }

    @Transactional
    public void delete(String id) {
        logMapper.deleteById(id);
    }

 }
