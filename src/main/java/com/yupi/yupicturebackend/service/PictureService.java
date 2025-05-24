package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.model.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.dto.picture.PictureUploadRequest;
import com.yupi.yupicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hccmac
 * <p>
 * 图片管理
 * 图片管理功能具体可以拆分为：
 * - 【管理员】根据 id 删除图片
 * - 【管理员】更新图片
 * - 【管理员】分页获取图片列表 （不需要脱敏和限制条数）
 * - 【管理员】根据id 获取图片（不需要脱敏）
 * - 分页获取图片列表（需要脱敏和限制条数）
 * - 根据 id 获取图片（需要脱敏）
 * - 修改图片
 */

public interface PictureService extends IService<Picture> {


    /**
     * 校验图片
     *
     * @param picture
     * @throws
     */
    void validPicture(Picture picture);

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    /**
     * 获取图片封装类（单条）
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);


    /**
     * 分页获取图片封装
     *
     * @param
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest 查询条件
     * @return 查询条件
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

}
