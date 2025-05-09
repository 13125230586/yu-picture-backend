package com.yupi.yupicturebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 * 方便在网络中传输，有时需要序列化的 实现Serializable接口
 * 每开发一个接口，都要像用户注册一样定义一个请求类  为什么不在接口那，把需要的参数直接列出来？
 * 每个接口需要的参数不一样，给每个请求都定义一个这样的类，更加清晰
 * 不需要的字段就不定义 更加安全
 */
@Data

public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 8735650154179439661L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;


}
