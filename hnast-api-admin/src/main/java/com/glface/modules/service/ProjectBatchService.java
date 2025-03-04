package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.mapper.ProjectBatchMapper;
import com.glface.modules.model.ProjectBatch;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectBatchService {

    private final static String STATUS_RUNING = "0";//申报中的批次

    @Resource
    private ProjectBatchMapper projectBatchMapper;

    public ProjectBatch get(String id){
        return projectBatchMapper.selectById(id);
    }

    public List<ProjectBatch> all(){
        LambdaQueryWrapper<ProjectBatch> queryWrapper = Wrappers.<ProjectBatch>query().lambda();
        queryWrapper.eq(ProjectBatch::getDelFlag,ProjectPlanType.DEL_FLAG_NORMAL);
        return projectBatchMapper.selectList(queryWrapper);
    }

    /**
     * 最近进行中的申请批次
     */
    public ProjectBatch latest(){
        LambdaQueryWrapper<ProjectBatch> queryWrapper = Wrappers.<ProjectBatch>query().lambda();
        queryWrapper.eq(ProjectBatch::getDelFlag,ProjectBatch.DEL_FLAG_NORMAL)
                .eq(ProjectBatch::getStatus,STATUS_RUNING).orderByDesc(ProjectBatch::getStartTime);
        List<ProjectBatch> projectBatches = projectBatchMapper.selectList(queryWrapper);
        ProjectBatch latest = null;
        if(projectBatches.size()>0){
            latest = projectBatches.get(0);
        }
        return latest;
    }

    public ProjectBatch latestByStartTime(){
        LambdaQueryWrapper<ProjectBatch> queryWrapper = Wrappers.<ProjectBatch>query().lambda();
        queryWrapper.eq(ProjectBatch::getDelFlag,ProjectBatch.DEL_FLAG_NORMAL)
                .orderByDesc(ProjectBatch::getStartTime);
        List<ProjectBatch> projectBatches = projectBatchMapper.selectList(queryWrapper);
        ProjectBatch latest = null;
        if(projectBatches.size()>0){
            latest = projectBatches.get(0);
        }
        return latest;
    }

    public Page<ProjectBatch> pageSearch(Page<ProjectBatch> page, ProjectBatch projectBatch) {
        page.setCount(projectBatchMapper.pageSearchCount(projectBatch));
        page.setList(projectBatchMapper.pageSearch(page,projectBatch));
        return page;
    }

    /**
     * 新增
     * 年份和批次不能重复
     * @param year
     * @param number
     * @param startTime
     * @param endTime
     * @param remark
     */
    @Transactional
    public void create(String year, String number, Date startTime,Date endTime, String remark) {
        year = StringUtils.trim(year);
        number = StringUtils.trim(number);

        if (StringUtils.isBlank(year)) {
            throw new ServiceException(PROJECT_BATCH_YEAR_REQUIRED);
        }
        if (StringUtils.isBlank(number)) {
            throw new ServiceException(PROJECT_BATCH_NUMBER_REQUIRED);
        }
        if (startTime==null) {
            throw new ServiceException(PROJECT_BATCH_STARTTIME_REQUIRED);
        }
        if (endTime==null) {
            throw new ServiceException(PROJECT_BATCH_ENTTIME_REQUIRED);
        }
        if(startTime.after(endTime)){
            throw new ServiceException(PROJECT_BATCH_STARTTIME_ERROR);
        }
        ProjectBatch batch = findByYearAndNumber(year,number);
        if(batch!=null){
            throw new ServiceException(PROJECT_BATCH_EXIST);
        }
        // 创建
        batch = new ProjectBatch();
        batch.setYear(year);
        batch.setNumber(number);
        batch.setStartTime(startTime);
        batch.setEndTime(endTime);
        batch.setRemark(remark);
        batch.setStatus(STATUS_RUNING);

        UserUtils.preAdd(batch);
        projectBatchMapper.insert(batch);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String year, String number, Date startTime,Date endTime, String remark) {
        year = StringUtils.trim(year);
        number = StringUtils.trim(number);
        // 数据验证
        ProjectBatch batch = get(id);
        if (batch == null) {
            throw new ServiceException(PROJECT_BATCH_NOTEXIST);
        }
        if (StringUtils.isBlank(year)) {
            throw new ServiceException(PROJECT_BATCH_YEAR_REQUIRED);
        }
        if (StringUtils.isBlank(number)) {
            throw new ServiceException(PROJECT_BATCH_NUMBER_REQUIRED);
        }
        if (startTime==null) {
            throw new ServiceException(PROJECT_BATCH_STARTTIME_REQUIRED);
        }
        if (endTime==null) {
            throw new ServiceException(PROJECT_BATCH_ENTTIME_REQUIRED);
        }
        if(startTime.after(endTime)){
            throw new ServiceException(PROJECT_BATCH_STARTTIME_ERROR);
        }

        ProjectBatch batch2 = findByYearAndNumber(year,number);
        if(batch2!=null&&!batch.getId().equals(batch2.getId())){
            throw new ServiceException(PROJECT_BATCH_YEAR_NUMBER_ONLY);
        }

        // 修改
        batch.setYear(year);
        batch.setNumber(number);
        batch.setStartTime(startTime);
        batch.setEndTime(endTime);
        batch.setRemark(remark);

        //存储
        UserUtils.preUpdate(batch);
        projectBatchMapper.updateById(batch);
    }

    public ProjectBatch findByYearAndNumber(String year,String number){
        LambdaQueryWrapper<ProjectBatch> queryWrapper = Wrappers.<ProjectBatch>query().lambda()
                .eq(ProjectBatch::getYear, year)
                .eq(ProjectBatch::getNumber, number)
                .eq(ProjectBatch::getDelFlag,ProjectBatch.DEL_FLAG_NORMAL);
        return projectBatchMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void changeStatus(String id) {
        ProjectBatch batch = get(id);
        if (batch == null) {
            throw new ServiceException(PROJECT_BATCH_NOTEXIST);
        }
        if("1".equals(batch.getStatus())){
            batch.setStatus("0");
        }else{
            batch.setStatus("1");
        }
        UserUtils.preUpdate(batch);
        projectBatchMapper.updateById(batch);
    }

    @Transactional
    public void delete(String id) {
        projectBatchMapper.deleteById(id);
    }

 }
