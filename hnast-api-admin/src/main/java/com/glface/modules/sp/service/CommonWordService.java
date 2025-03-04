package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysUser;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.sp.mapper.CommonWordMapper;
import com.glface.modules.sp.model.CommonWord;
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
public class CommonWordService {
    @Resource
    private CommonWordMapper commonWordMapper;

    public CommonWord get(String id){
        return commonWordMapper.selectById(id);
    }

    public List<CommonWord> all(){
        LambdaQueryWrapper<CommonWord> queryWrapper = Wrappers.<CommonWord>query().lambda();
        queryWrapper.eq(CommonWord::getDelFlag,CommonWord.DEL_FLAG_NORMAL)
                .eq(CommonWord::getCreateBy,UserUtils.getUserId())
                .orderByAsc(CommonWord::getSort);
        return commonWordMapper.selectList(queryWrapper);
    }
    public Page<CommonWord> pageSearch(Page<CommonWord> page, CommonWord projectPlanType) {
        page.setCount(commonWordMapper.pageSearchCount(projectPlanType));
        page.setList(commonWordMapper.pageSearch(page,projectPlanType));
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
        CommonWord commonWord = findByWord(word,user.getId());
        if(commonWord!=null){
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_EXIST);
        }

        // 创建
        commonWord = new CommonWord();
        commonWord.setWord(word);
        commonWord.setSort(sort);
        UserUtils.preAdd(commonWord);
        commonWordMapper.insert(commonWord);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String word, int sort) {
        word = StringUtils.trim(word);
        // 数据验证
        CommonWord commonWord = get(id);
        if (commonWord == null) {
            throw new ServiceException(PROJECT_COMMON_WORD_NOTEXIST);
        }
        if (StringUtils.isBlank(word)) {
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_REQUIRED);
        }
        SysUser user = UserUtils.getUser();
        CommonWord projectCommonWord2 = findByWord(word,user.getId());
        if(projectCommonWord2!=null&&!commonWord.getId().equals(projectCommonWord2.getId())){
            throw new ServiceException(PROJECT_COMMON_WORD_WORD_EXIST);
        }

        // 修改
        commonWord.setWord(word);
        commonWord.setSort(sort);
        //存储
        UserUtils.preUpdate(commonWord);
        commonWordMapper.updateById(commonWord);
    }

    public CommonWord findByWord(String word, String userId){
        LambdaQueryWrapper<CommonWord> queryWrapper = Wrappers.<CommonWord>query().lambda()
                .eq(CommonWord::getWord, word)
                .eq(CommonWord::getCreateBy, userId)
                .eq(CommonWord::getDelFlag,CommonWord.DEL_FLAG_NORMAL);
        return commonWordMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        commonWordMapper.deleteById(id);
    }

 }
