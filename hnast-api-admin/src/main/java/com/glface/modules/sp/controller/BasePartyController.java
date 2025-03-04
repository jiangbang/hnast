package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.modules.sp.model.BaseMajorCategory;
import com.glface.modules.sp.service.BasePartyService;
import com.glface.modules.sp.model.BaseParty;
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
 * 党派
 */
@Slf4j
@RestController
@RequestMapping("/specialist/party")
public class BasePartyController {
    @Resource
    private BasePartyService basePartyService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        BaseParty bean = basePartyService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("name", bean.getName())
                .setPV("sort", bean.getSort())
                .setPV("remark", bean.getRemark())
                .setPV("createDate", DateUtils.formatDate(bean.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                .build().getObject();
        return R.ok(object);
    }

    @RequestMapping(value = "/allBaseParties")
    public R<Object> allBaseParties() {
        List<BaseParty> list = basePartyService.all();
        //排序
        Collections.sort(list, new Comparator<BaseParty>() {
            @Override
            public int compare(BaseParty o1, BaseParty o2) {
                if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }else if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });
        List<Object> dataList = new ArrayList<>();
        for(BaseParty bean:list){
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
     *
     * @param pageNo 查询分页
     * @param limit  查询数
     * @param order  排序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(
            String name,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "t." + order;
        // 设置查询条件
        Page<BaseParty> page = new Page<>(pageNo, limit);
        page.setOrderBy(order);
        BaseParty condition = new BaseParty();
        condition.setName(name);
        // 查询
        page = basePartyService.pageSearch(page, condition);
        // 构造返回数据
        List<Object> list = new ArrayList<>();
        for (BaseParty baseParty : page.getList()) {
            Object basePartyBean = new DynamicBean.Builder().setPV("id", baseParty.getId())
                    .setPV("name", baseParty.getName())
                    .setPV("sort", baseParty.getSort())
                    .setPV("remark", baseParty.getRemark())
                    .setPV("createDate", DateUtils.formatDate(baseParty.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .build().getObject();
            list.add(basePartyBean);
        }
        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("list", list, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 新增
     *
     * @param name 名称
     * @param sort 排序(升序)
     */
    @PreAuthorize("hasAuthority('specialist:party:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String name,
            String remark,
            @RequestParam(value = "sort", defaultValue = "1000") Integer sort
    ) {
        basePartyService.create(name,remark, sort);
        return R.ok();
    }

    /**
     * 编辑
     *
     * @param name 名称
     * @param sort 排序(升序)
     */
    @PreAuthorize("hasAuthority('specialist:party:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id,
                            String name,
                            String remark,
                            @RequestParam(value = "sort", defaultValue = "1000") Integer sort
    ) {
        basePartyService.update(id, name,remark, sort);
        return R.ok();
    }

    /**
     * 删除basePartyService
     */
    @PreAuthorize("hasAuthority('specialist:party:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        basePartyService.delete(id);
        return R.ok();
    }
}
