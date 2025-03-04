package com.glface.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础查询类，其中包括分页以及排序
 *
 * @author maowei
 */
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int pageNo = 1; // 当前页码
    private int pageSize = 10;
    private int count;// 总记录数，设置为“-1”表示不查询总数

    private int start =0;

    private List<T> list = new ArrayList<T>();
    private String orderBy = ""; // 标准查询有效， 实例： updatedate desc, name asc

    private T condition;//查询条件

    public Page(){
    }

    /**
     * 构造方法
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     */
    public Page(int pageNo, int pageSize) {
        this(pageNo, pageSize, true);
    }

    /**
     * 构造方法
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @param isCount 是否计算总条数
     */
    public Page(int pageNo, int pageSize, boolean isCount) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        if(isCount){
            this.count = 0;
        }else{
            this.count = -1;
        }
        start = (pageNo-1)*pageSize;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        start = (pageNo-1)*pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        start = (pageNo-1)*pageSize;
    }

    /**
     * 是否不进行总数统计
     * @return this.count==-1
     */
    @JsonIgnore
    public boolean isNotCount() {
        return this.count==-1;
    }

    public String toLimit(){
        String limit = "limit";
        limit = limit +" "+start+","+pageSize;
        return limit;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCount() {
        return count;
    }

    public List<T> getList() {
        return list;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }
}
