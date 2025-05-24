package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupicturebackend.model.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hccmac
 * 用户管理
 * 用户管理功能具体可以拆分为：
 * - 【管理员】创建用户
 * - 【管理员】根据id 删除用户
 * - 【管理员】更新用户
 * - 【管理员】分页获取用户列表（需要脱敏）
 * - 【管理员】根据 id 获取用户（未脱敏）
 * - 根据id 获取用户 （脱敏）
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
     *
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 获取脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);


    /**
     * 获取脱敏后的用户列表
     *
     * @param userList 用户列表
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 密码加密
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);


    /**
     * 获取查询条件
     * @param userQueryRequest 用户查询请求
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
