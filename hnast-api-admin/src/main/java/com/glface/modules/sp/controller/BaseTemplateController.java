package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.modules.sp.model.BaseTemplate;
import com.glface.modules.sp.service.BaseTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板信息
 */
@Slf4j
@RestController
@RequestMapping("/specialist/template")
public class BaseTemplateController {

    @Resource
    private BaseTemplateService baseService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        BaseTemplate bean = baseService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("name", bean.getName())
                .setPV("fileId", bean.getFileId())
                .setPV("sort", bean.getSort())
                .setPV("remark", bean.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 分页查询
     * @param name 名称
     * @param pageNo 查询分页
     * @param limit  查询数
     * @param order  排序 默认创建时间降序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String name,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "s." + order;
        // 设置查询条件
        Page<BaseTemplate> page = new Page<>(pageNo, limit);
        page.setOrderBy(order);
        BaseTemplate condition = new BaseTemplate();
        condition.setName(name);

        // 查询
        page = baseService.pageSearch(page, condition);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (BaseTemplate bean : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
                    .setPV("fileId", bean.getFileId())
                    .setPV("sort", bean.getSort())
                    .setPV("remark", bean.getRemark())
                    .setPV("createDate", DateUtils.formatDate(bean.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("list", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @param name     名称
     * @param sort     排序 升序
     * @param remark   说明
     * @return
     */
    @PreAuthorize("hasAuthority('specialist:template:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,String fileId, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        baseService.create(name, fileId,sort, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasAuthority('specialist:template:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name,String fileId, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        baseService.update(id, name, fileId,sort, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('specialist:template:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        baseService.delete(id);
        return R.ok();
    }


}
