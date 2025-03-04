package com.glface.modules.service;


import com.glface.modules.mapper.ProjectFundsNameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectFundsNameService {

    @Resource
    private ProjectFundsNameMapper projectFundsNameMapper;


    /*
    * 查询所有支出内容名称
    * */
    @Transactional
    public List<String> findAllFundsName(){
        Set<String> fundsName = projectFundsNameMapper.findAllFundsName();
        List<String> fundsNameList = new ArrayList<>();
        fundsNameList.addAll(fundsName);
        //排序
        Collections.sort(fundsNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.compareTo(o2);
            }
        });
        return fundsNameList;
    }
}
