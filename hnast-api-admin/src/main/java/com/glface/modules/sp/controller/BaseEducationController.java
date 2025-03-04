package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.modules.sp.model.BaseDegree;
import com.glface.modules.sp.model.BaseEducation;
import com.glface.modules.sp.service.BaseEducationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 学历信息
 */
@Slf4j
@RestController
@RequestMapping("/specialist/education")
public class BaseEducationController {

    @Resource
    private BaseEducationService baseService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        BaseEducation bean = baseService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("name", bean.getName())
                .setPV("sort", bean.getSort())
                .setPV("remark", bean.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    @RequestMapping(value = "/allBaseEducations")
    public R<Object> allBaseEducations() {
        List<BaseEducation> list = baseService.all();
        //排序
        Collections.sort(list, new Comparator<BaseEducation>() {
            @Override
            public int compare(BaseEducation o1, BaseEducation o2) {
                if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }else if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });
        List<Object> dataList = new ArrayList<>();
        for(BaseEducation bean:list){
            Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
                    .setPV("sort", bean.getSort())
                    .setPV("remark", bean.getRemark())
                    .build().getObject();
            dataList.add(object);
        }
        return R.ok(dataList);
    }

    /**
     * 分页查询
     * @param name 名称
     * @param pageNo 查询分页
     * @param limit  查询数
     * @param order  排序 默认sort升序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String name,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "s." + order;
        // 设置查询条件
        Page<BaseEducation> page = new Page<>(pageNo, limit);
        page.setOrderBy(order);
        BaseEducation condition = new BaseEducation();
        condition.setName(name);

        // 查询
        page = baseService.pageSearch(page, condition);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (BaseEducation bean : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
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
    @PreAuthorize("hasAuthority('specialist:education:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        baseService.create(name, sort, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasAuthority('specialist:education:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        baseService.update(id, name, sort, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('specialist:education:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        baseService.delete(id);
        return R.ok();
    }


}
