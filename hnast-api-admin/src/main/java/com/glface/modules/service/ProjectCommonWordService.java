package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysUser;
import com.glface.modules.mapper.ProjectCommonWordMapper;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

/**
 * 常用语
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectCommonWordService {
    @Resource
    private ProjectCommonWordMapper projectCommonWordMapper;

    public ProjectCommonWord get(String id){
        return projectCommonWordMapper.selectById(id);
    }

    public List<ProjectCommonWord> all(){
        LambdaQueryWrapper<ProjectCommonWord> queryWrapper = Wrappers.<ProjectCommonWord>query().lambda();
        queryWrapper.eq(ProjectCommonWord::getDelFlag,ProjectCommonWord.DEL_FLAG_NORMAL)
                .eq(ProjectCommonWord::getCreateBy,UserUtils.getUserId())
                .orderByAsc(ProjectCommonWord::getSort);
        return projectCommonWordMapper.selectList(queryWrapper);
    }
    public Page<ProjectCommonWord> pageSearch(Page<ProjectCommonWord> page, ProjectCommonWord projectPlanType) {
        page.setCount(projectCommonWordMapper.pageSearchCount(projectPlanType));
        page.setList(projectCommonWordMapper.pageSearch(page,projectPlanType));
        return page;
    }
    /**
     * 新增
     */
    @Transactional
    public void create(String word, int sort) {
        word = StringUtils.trim(word);

        if (StringUtils.isBlank(word)) {
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_REQUIRED);
        }
        SysUser user = UserUtils.getUser();
        ProjectCommonWord projectCommonWord = findByWord(word,user.getId());
        if(projectCommonWord!=null){
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_EXIST);
        }

        // 创建
        projectCommonWord = new ProjectCommonWord();
        projectCommonWord.setWord(word);
        projectCommonWord.setSort(sort);
        UserUtils.preAdd(projectCommonWord);
        projectCommonWordMapper.insert(projectCommonWord);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String word, int sort) {
        word = StringUtils.trim(word);
        // 数据验证
        ProjectCommonWord projectCommonWord = get(id);
        if (projectCommonWord == null) {
            throw new ServiceException(PROJECT_COMMON_WORD_NOTEXIST);
        }
        if (StringUtils.isBlank(word)) {
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_REQUIRED);
        }
        SysUser user = UserUtils.getUser();
        ProjectCommonWord projectCommonWord2 = findByWord(word,user.getId());
        if(projectCommonWord2!=null&&!projectCommonWord.getId().equals(projectCommonWord2.getId())){
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_EXIST);
        }

        // 修改
        projectCommonWord.setWord(word);
        projectCommonWord.setSort(sort);
        //存储
        UserUtils.preUpdate(projectCommonWord);
        projectCommonWordMapper.updateById(projectCommonWord);
    }

    public ProjectCommonWord findByWord(String word, String userId){
        LambdaQueryWrapper<ProjectCommonWord> queryWrapper = Wrappers.<ProjectCommonWord>query().lambda()
                .eq(ProjectCommonWord::getWord, word)
                .eq(ProjectCommonWord::getCreateBy, userId)
                .eq(ProjectCommonWord::getDelFlag,ProjectCommonWord.DEL_FLAG_NORMAL);
        return projectCommonWordMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        projectCommonWordMapper.deleteById(id);
    }

 }
