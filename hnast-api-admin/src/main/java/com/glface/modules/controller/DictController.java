package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.model.SysDict;
import com.glface.modules.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/system/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @RequestMapping(value = "/get")
    public R<SysDict> get(String id) {
        SysDict dict = dictService.get(id);
        return R.ok(dict);
    }

    @PreAuthorize("hasAuthority('permission:dict:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "order", defaultValue = "sort desc") String order) {
        // 设置查询条件
        Page<SysDict> page = new Page<>(pageNo, limit);
        page.setOrderBy("type asc," + NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order)));
        SysDict dict = new SysDict();
        dict.setRemark(remark);
        dict.setType(type);
        // 查询
        page = dictService.find(page, dict);
        // 构造返回数据
        List<Object> dictList = new ArrayList<>();
        for (SysDict r : page.getList()) {
            Object userBean = new DynamicBean.Builder()
                    .setPV("id", r.getId())
                    .setPV("type", r.getType())
                    .setPV("label", r.getLabel())
                    .setPV("value", r.getValue())
                    .setPV("sort", r.getSort())
                    .setPV("remark", r.getRemark())
                    .setPV("createDate", DateUtils.formatDate(r.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            dictList.add(userBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("dicts", dictList, List.class)
                .build().getObject();
        return R.ok(data);
    }

    @PreAuthorize("hasAuthority('permission:dict:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String type,
            String label,
            String value,
            Integer sort,
            String remark) {
        dictService.create(type, label, value, sort, remark);
        return R.ok();
    }

    @PreAuthorize("hasAuthority('permission:dict:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String id,
            String type,
            String label,
            String value,
            Integer sort,
            String remark) {
        dictService.update(id,type, label, value, sort,remark);
        return R.ok();
    }

    /**
     * 获取字典类型
     *
     * @return
     */
    @PreAuthorize("hasAuthority('permission:dict:view')")
    @RequestMapping(value = "/allTypes")
    public R<Object> allTypes() {
        Set<String> list = dictService.allTypes();
        return R.ok(list);
    }


    @PreAuthorize("hasAuthority('permission:dict:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        dictService.delete(id);
        return R.ok();
    }

}
