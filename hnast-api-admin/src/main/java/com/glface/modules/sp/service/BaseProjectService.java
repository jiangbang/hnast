package com.glface.modules.sp.service;


import com.glface.modules.sp.mapper.BasePositionalMapper;
import com.glface.modules.sp.mapper.BaseProjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 制裁项目类别信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BaseProjectService {
    @Resource
    private BaseProjectMapper baseProjectMapper;


    public List<String> findProjectNamesByStatus(){
        Set<String> projectNameSet = baseProjectMapper.findProjectNamesByStatus();
        List<String> projectNameList = new ArrayList<>();
        projectNameList.addAll(projectNameSet);
        //排序
        Collections.sort(projectNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.compareTo(o2);
            }
        });
        return projectNameList;
    }
}
