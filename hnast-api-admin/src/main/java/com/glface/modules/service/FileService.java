package com.glface.modules.service;

import com.glface.base.utils.DateUtils;
import com.glface.modules.mapper.FileInfoMapper;
import com.glface.modules.model.FileInfo;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;
import java.util.Random;


@Slf4j
@Service
public class FileService {

    @Value("${myself.fileUploadDir}")
    private String fileUploadDir;

    @Resource
    private FileInfoMapper fileInfoMapper;

    public FileInfo get(String id){
        return fileInfoMapper.selectById(id);
    }

    public String getNameById(String id){
        FileInfo fileInfo = get(id);
        String fileName = "";
        if(fileInfo!=null){
            fileName = fileInfo.getName();
        }
        return fileName;
    }

    public FileInfo uploadFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        // 文件上传时，Chrome和IE/Edge对于originalFilename处理不同
        // Chrome 会获取到该文件的直接文件名称，IE/Edge会获取到文件上传时完整路径/文件名
        // Check for Unix-style path
        int unixSep = originalFilename.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = originalFilename.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1)  {
            // Any sort of path separator found...
            originalFilename = originalFilename.substring(pos + 1);
        }
        //获取文件的后缀名.jpg
        String suffix = "";
        int lastIndexOf = originalFilename.lastIndexOf(".");
        if(lastIndexOf>0){
            suffix = originalFilename.substring(lastIndexOf);
        }

        String newFileName = System.currentTimeMillis()+String.format("%04d",new Random().nextInt(9999)) + suffix;
        File dest = new File(Paths.get(fileUploadDir, DateUtils.getDate("yyyyMM"), newFileName).toString());
        File parent = dest.getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        file.transferTo(dest);
        //存储数据库
        FileInfo fileInfo = create(dest.getAbsolutePath(),originalFilename,suffix);
        return fileInfo;
    }

    /**
     * 新增
     */
    @Transactional
    public FileInfo create(String absoluteAddress,String name,String suffix) {

        // 创建
        FileInfo fileInfo = new FileInfo();
        fileInfo.setAbsoluteAddress(absoluteAddress);
        fileInfo.setName(name);
        fileInfo.setSuffix(suffix);

        UserUtils.preAdd(fileInfo);
        fileInfoMapper.insert(fileInfo);
        return fileInfo;
    }

}
