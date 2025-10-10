package com.yupi.yupicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yupi.yupicturebackend.config.CosClientConfig;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FileManager {

    @Resource
    CosClientConfig cosClientConfig;

    @Resource
    CosManager cosManager;

    private final static long ONE_M = 1024 * 1024L;

    private final static List<String> PICTURE_ALLOW_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");


    /**
     * 上传图片
     *
     * @param multipartFile    文件
     * @param uploadPathPrefix 上传路径前缀 eg:public/%s 这里可以用用户id表示下级目录
     * @return uploadPictureResult
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验图片 检验什么：图片大小不超过2M 图片后缀只能在规定后缀范围内
        validPicture(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), RandomUtil.randomString(16), FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            //1.cosManager.putPictureObject方法需要传参：文件路径和文件 文件路径以上两行代码就行，文件需要固定代码
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            //2.获取图片信息，来封装结果
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //3.封装图片信息，返回自己定义的结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            String format = imageInfo.getFormat();
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            double picScale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);//加 "/"  防止没有前缀
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(format);
            return uploadPictureResult;
        } catch (IOException e) {
            log.error("文件上传图片到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }


    /**
     * 校验文件
     *
     * @param multipartFile multipart 文件
     */
    private void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(Objects.isNull(multipartFile), ErrorCode.PARAMS_ERROR, "上传文件为空");
        ThrowUtils.throwIf(multipartFile.getSize() > ONE_M * 2, ErrorCode.PARAMS_ERROR, "图片文件不能大于2M");
        //获取上传图片的后缀
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!PICTURE_ALLOW_LIST.contains(suffix), ErrorCode.PARAMS_ERROR, "文件类型错误，只支持jpeg、jpg、png、webp");
    }


    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean delete = file.delete();
        if (!delete) {
            log.error("文件删除失败, filepath : {}", file.getAbsolutePath());
        }
    }

}
