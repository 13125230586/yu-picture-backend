package com.yupi.yupicturebackend.service;

import com.yupi.yupicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author hccmac
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-05-08 17:14:19
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
    //long userRegister(UserRegisterRequest userRegisterRequest);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 脱敏后登录用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);



    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);


}
