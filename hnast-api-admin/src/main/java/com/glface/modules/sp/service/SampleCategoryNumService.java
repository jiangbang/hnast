package com.glface.modules.sp.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.sp.mapper.BaseCategoryMapper;
import com.glface.modules.sp.mapper.SampleCategoryNumMapper;
import com.glface.modules.sp.mapper.SampleMapper;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.model.Sample;
import com.glface.modules.sp.model.SampleCategoryNum;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import static com.glface.common.web.ApiCode.*;
/**
 * 专家抽取类别数量设置
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SampleCategoryNumService {
    @Resource
    private SampleCategoryNumMapper sampleCategoryNumMapper;
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private BaseCategoryMapper baseCategoryMapper;
    public SampleCategoryNum get(String id){
        return sampleCategoryNumMapper.selectById(id);
    }
    public List<SampleCategoryNum> all(){
        LambdaQueryWrapper<SampleCategoryNum> queryWrapper = Wrappers.<SampleCategoryNum>query().lambda();
        queryWrapper.eq(SampleCategoryNum::getDelFlag,SampleCategoryNum.DEL_FLAG_NORMAL).orderByAsc(SampleCategoryNum::getCreateDate);
        return sampleCategoryNumMapper.selectList(queryWrapper);
    }

    public List<SampleCategoryNum> findBySampleId(String sampleId){
        LambdaQueryWrapper<SampleCategoryNum> queryWrapper = Wrappers.<SampleCategoryNum>query().lambda();
        queryWrapper.eq(SampleCategoryNum::getSampleId, sampleId)
                .eq(SampleCategoryNum::getDelFlag,SampleCategoryNum.DEL_FLAG_NORMAL);
        return sampleCategoryNumMapper.selectList(queryWrapper);
    }

    public SampleCategoryNum getBySampleIdAndBaseCategoryId(String sampleId,String baseCategoryId){
        LambdaQueryWrapper<SampleCategoryNum> queryWrapper = Wrappers.<SampleCategoryNum>query().lambda()
                .eq(SampleCategoryNum::getSampleId, sampleId)
                .eq(SampleCategoryNum::getBaseCategoryId, baseCategoryId)
                .eq(SampleCategoryNum::getDelFlag,SampleCategoryNum.DEL_FLAG_NORMAL);
        return sampleCategoryNumMapper.selectOne(queryWrapper);
    }

   /**
     * 新增
     * @param sampleId            
     * @param baseCategoryId            
     * @param num            抽取专家数
     */
    @Transactional
    public void create( String sampleId, String baseCategoryId, Integer num) {
        //参数验证
        if(StringUtils.isBlank(sampleId)) {
                throw new ServiceException(SAMPLECATEGORYNUM_SAMPLEID_REQUIRED);
        }
		    
        Sample sample = sampleMapper.selectById(sampleId);
        if(sample==null){
            throw new ServiceException(SAMPLECATEGORYNUM_SAMPLE_NOTEXIST);
        }
		         
        if(StringUtils.isBlank(baseCategoryId)) {
                throw new ServiceException(SAMPLECATEGORYNUM_BASECATEGORYID_REQUIRED);
        }
		    
        BaseCategory baseCategory = baseCategoryMapper.selectById(baseCategoryId);
        if(baseCategory==null){
            throw new ServiceException(SAMPLECATEGORYNUM_BASECATEGORY_NOTEXIST);
        }
		         
        if(num==null||num<=0) {
                throw new ServiceException(SAMPLECATEGORYNUM_NUM_REQUIRED);
        }
		    
        // 验证baseCategoryId是否唯一
        SampleCategoryNum model = getBySampleIdAndBaseCategoryId(sampleId,baseCategoryId);
        if(model!=null){
                throw new ServiceException(SAMPLECATEGORYNUM_BASECATEGORYID_EXIST);
        }
        // 创建
        SampleCategoryNum sampleCategoryNum = new SampleCategoryNum();
        sampleCategoryNum.setSampleId(sampleId);
        sampleCategoryNum.setBaseCategoryId(baseCategoryId);
        sampleCategoryNum.setNum(num);
        UserUtils.preAdd(sampleCategoryNum);
        sampleCategoryNumMapper.insert(sampleCategoryNum);
    }
   /**
     * 编辑
     * @param sampleId            
     * @param baseCategoryId            
     * @param num            抽取专家数
     */
    @Transactional
    public void update(String id, String sampleId, String baseCategoryId, Integer num) {
        //参数验证
        SampleCategoryNum sampleCategoryNum = get(id);
        if (sampleCategoryNum == null) {
            throw new ServiceException(SAMPLECATEGORYNUM_NOTEXIST);
        }
        if(StringUtils.isBlank(sampleId)) {
            throw new ServiceException(SAMPLECATEGORYNUM_SAMPLEID_REQUIRED);
        }
		         
        Sample sample = sampleMapper.selectById(sampleId);
        if(sample==null){
            throw new ServiceException(SAMPLECATEGORYNUM_SAMPLE_NOTEXIST);
        }
		         
        if(StringUtils.isBlank(baseCategoryId)) {
                throw new ServiceException(SAMPLECATEGORYNUM_BASECATEGORYID_REQUIRED);
        }
		         
        BaseCategory baseCategory = baseCategoryMapper.selectById(baseCategoryId);
        if(baseCategory==null){
            throw new ServiceException(SAMPLECATEGORYNUM_BASECATEGORY_NOTEXIST);
        }

        if(num==null||num<=0) {
                throw new ServiceException(SAMPLECATEGORYNUM_NUM_REQUIRED);
        }
		         
        // 验证baseCategoryId是否唯一
        SampleCategoryNum model2 = getBySampleIdAndBaseCategoryId(sampleId,baseCategoryId);
        if(model2!=null&&!sampleCategoryNum.getId().equals(model2.getId())){
                throw new ServiceException(SAMPLECATEGORYNUM_BASECATEGORYID_EXIST);
        }
        // 修改
        sampleCategoryNum.setSampleId(sampleId);
        sampleCategoryNum.setBaseCategoryId(baseCategoryId);
        sampleCategoryNum.setNum(num);
        UserUtils.preUpdate(sampleCategoryNum);
        sampleCategoryNumMapper.updateById(sampleCategoryNum);
    }
    @Transactional
    public void delete(String id) {
        sampleCategoryNumMapper.deleteById(id);
    }
 }
