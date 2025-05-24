package com.yupi.yupicturebackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class PictureVO implements Serializable {
  
    /**  
     * id  
     */  
    private Long id;  
  
    /**  
     * 图片 url  
     */  
    private String url;  
  
    /**  
     * 图片名称  
     */  
    private String name;  
  
    /**  
     * 简介  
     */  
    private String introduction;  
  
    /**  
     * 标签  
     */  
    private List<String> tags;
  
    /**  
     * 分类  
     */  
    private String category;  
  
    /**  
     * 文件体积  
     */  
    private Long picSize;  
  
    /**  
     * 图片宽度  
     */  
    private Integer picWidth;  
  
    /**  
     * 图片高度  
     */  
    private Integer picHeight;  
  
    /**  
     * 图片比例  
     */  
    private Double picScale;  
  
    /**  
     * 图片格式  
     */  
    private String picFormat;  
  
    /**  
     * 用户 id  
     */  
    private Long userId;  
  
    /**  
     * 创建时间  
     */  
    private Date createTime;
  
    /**  
     * 编辑时间  
     */  
    private Date editTime;  
  
    /**  
     * 更新时间  
     */  
    private Date updateTime;  
  
    /**  
     * 创建用户信息  
     */  
    private UserVO user;  
  
    private static final long serialVersionUID = 1L;

    /**
     * 对象转封装类
     */

    public  static Picture voToObj(PictureVO pictureVO){
        if(Objects.isNull(pictureVO)){
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVO,picture);
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }

    /**
     * 封装类转对象实体类
     */

    public static PictureVO objToVo(Picture picture){
        if(Objects.isNull(picture)){
            return  null;
        }
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture,pictureVO);
        pictureVO.setTags(JSONUtil.toList(picture.getTags(),String.class));
        return pictureVO;
    }

}
