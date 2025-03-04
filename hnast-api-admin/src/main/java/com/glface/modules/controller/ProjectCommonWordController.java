package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysOffice;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.service.ProjectCommonWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 常用语
 */
@Slf4j
@RestController
@RequestMapping("/project/commonWord")
public class ProjectCommonWordController {

    @Resource
    private ProjectCommonWordService projectCommonWordService;

    @PreAuthorize("hasAuthority('project:commonWord:view')")
    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectCommonWord projectCommonWord = projectCommonWordService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", projectCommonWord.getId())
                .setPV("word", projectCommonWord.getWord())
                .setPV("sort", projectCommonWord.getSort())
                .build().getObject();
        return R.ok(object);
    }
    /**
     * 查询当前用户常用语
     * @return
     */
    @PreAuthorize("hasAuthority('project:commonWord:view')")
    @RequestMapping(value = "/all")
    public R<Object> all() {
        List<ProjectCommonWord> commonWords = projectCommonWordService.all();
        return R.ok(commonWords);
    }

    /**
     * 查询当前用户常用语
     * @return
     */
    @PreAuthorize("hasAuthority('project:commonWord:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                         @RequestParam(value = "limit", defaultValue = "10") int limit,
                         @RequestParam(value = "order", defaultValue = "createDate desc") String order) {

        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "p." + order;
        // 设置查询条件
        Page<ProjectCommonWord> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectCommonWord commonWord = new ProjectCommonWord();
        commonWord.setCreateBy(com.glface.modules.sys.utils.UserUtils.getUserId());
        // 查询
        page = projectCommonWordService.pageSearch(page,commonWord);
        // 构造返回数据
        List<Object> commonWordsList = new ArrayList<>();
        for (ProjectCommonWord p : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", p.getId())
                    .setPV("word", p.getWord())
                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            commonWordsList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("commonWords", commonWordsList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @param word
     * @param sort         排序 升序
     * @return
     */
    @LoggerMonitor(value = "常用语-新增")
    @PreAuthorize("hasAuthority('project:commonWord:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String word,  @RequestParam(value = "sort", defaultValue = "1000") int sort) {
        projectCommonWordService.create(word,sort);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "常用语-编辑")
    @PreAuthorize("hasAuthority('project:commonWord:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id,String word,  @RequestParam(value = "sort", defaultValue = "1000") int sort) {
        projectCommonWordService.update(id, word, sort);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "常用语-删除")
    @PreAuthorize("hasAuthority('project:commonWord:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectCommonWordService.delete(id);
        return R.ok();
    }


}
