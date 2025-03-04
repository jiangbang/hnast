package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.modules.sp.model.BaseMajor;
import com.glface.modules.sp.model.BaseMajorCategory;
import com.glface.modules.sp.service.BaseMajorCategoryService;
import com.glface.modules.sp.service.BaseMajorService;
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
 * 专业信息
 */
@Slf4j
@RestController
@RequestMapping("/specialist/major")
public class BaseMajorController {

    @Resource
    private BaseMajorService baseService;

    @Resource
    private BaseMajorCategoryService majorCategoryService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        BaseMajor bean = baseService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("name", bean.getName())
                .setPV("majorCategoryId", bean.getMajorCategoryId())
                .setPV("sort", bean.getSort())
                .setPV("remark", bean.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 所有行业
     */
    @RequestMapping(value = "/allMajorCategories")
    public R<Object> allMajorCategories() {
        List<BaseMajorCategory> categories = majorCategoryService.all();
        //排序
        Collections.sort(categories, new Comparator<BaseMajorCategory>() {
            @Override
            public int compare(BaseMajorCategory o1, BaseMajorCategory o2) {
                if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }else if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });
        List<Object> dataList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("sort", null)
                .build().copyList(categories);
        return R.ok(dataList);
    }

    /**
     * 查询指定行业的所有专业
     */
    @RequestMapping(value = "/findByMajorCategoryId")
    public R<Object> findByMajorCategoryId(String majorCategoryId) {
        List<BaseMajor> list = baseService.findByMajorCategoryId(majorCategoryId);
        List<Object> result = new ArrayList<>();
        for (BaseMajor bean : list) {
            BaseMajorCategory majorCategory = majorCategoryService.get(bean.getMajorCategoryId());
            String majorCategoryName = "";
            if (majorCategory != null) {
                majorCategoryName = majorCategory.getName();
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
                    .setPV("majorCategoryId", bean.getMajorCategoryId())
                    .setPV("majorCategoryName", majorCategoryName)
                    .setPV("sort", bean.getSort())
                    .setPV("remark", bean.getRemark())
                    .setPV("createDate", DateUtils.formatDate(bean.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            result.add(batchBean);
        }
        return R.ok(result);
    }

    /**
     * 分页查询
     * @param name 名称
     * @param majorCategoryId 行业id
     * @param pageNo 查询分页
     * @param limit  查询数
     * @param order  排序 默认sort升序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String name,
                            String majorCategoryId,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "s." + order;
        // 设置查询条件
        Page<BaseMajor> page = new Page<>(pageNo, limit);
        page.setOrderBy(order);
        BaseMajor condition = new BaseMajor();
        condition.setName(name);
        condition.setMajorCategoryId(majorCategoryId);

        // 查询
        page = baseService.pageSearch(page, condition);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (BaseMajor bean : page.getList()) {
            BaseMajorCategory majorCategory = majorCategoryService.get(bean.getMajorCategoryId());
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
                    .setPV("majorCategoryId", bean.getMajorCategoryId())
                    .setPV("majorCategoryName", majorCategory.getName())
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
     * @param majorCategoryId 行业id
     * @param sort     排序 升序
     * @param remark   说明
     * @return
     */
    @PreAuthorize("hasAuthority('specialist:major:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name, String majorCategoryId,@RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        baseService.create(name, majorCategoryId,sort, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasAuthority('specialist:major:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name,String majorCategoryId, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        baseService.update(id, name,majorCategoryId, sort, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('specialist:major:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        baseService.delete(id);
        return R.ok();
    }


}
