package com.yupi.yupicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.mapper.UserMapper;
import com.yupi.yupicturebackend.model.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.UserRoleEnum;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yupi.yupicturebackend.constant.UserConstant.USER_LOGIN_STATE;
import static com.yupi.yupicturebackend.exception.ThrowUtils.throwIf;

/**
 * @author hccmac
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-05-08 17:14:19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {


        //1.校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //throwIf(StrUtil.hasBlank(userAccount,userPassword,checkPassword),ErrorCode.PARAMS_ERROR,"参数异常");

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户两次密码输入不一致");
        }

        //2.验证用户账号在数据库中是否存在 baseMapper.selectCount() 检查输入userAccount和数据库中userAccount是否相同，相同返回长度大于0

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        //3.密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        //4.保存到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库内部异常");
        }

        //主键回填
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }

        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        //2.对密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3.查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = baseMapper.selectOne(queryWrapper);

//        LambdaQueryWrapper<User> qw = new QueryWrapper<User>().lambda()
//                .eq(User::getUserAccount,userAccount)
//                .eq(User::getUserPassword,userPassword);
//        User user1 = baseMapper.selectOne(qw);

        if(ObjectUtil.isNull(user)){
            log.info("user login failed userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或密码错误");
        }

        //4.保存用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return getLoginUserVO(user);
    }

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return 加密后密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        //加盐
        final String SALT = "hccHcc";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 获取当前登录用户
     * @param userQueryRequest 用户查询请求
     * @return 用户查询结果
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if(Objects.isNull(userQueryRequest)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id),"id",id);
        queryWrapper.eq(ObjUtil.isNotNull(userRole),"userRole",userRole);
        queryWrapper.like(StrUtil.isNotBlank(userName),"userName",userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount),"userAccount",userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userProfile),"userProfile",userProfile);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField),sortOrder.equals("ascend"),sortField);
        return queryWrapper;
    }

    /**
     * 获取脱敏类用户信息
     * @param user 用户
     * @return 脱敏类用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user){
        if(Objects.isNull(user)){
            return null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取脱敏类用户信息
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if(Objects.isNull(user)){
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        return userVO;
    }

    /**
     * 获取脱敏类用户信息
     * @param userList 用户列表
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream()
                //.map(user -> getUserVO(user))
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户登录信息
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(Objects.isNull(currentUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //从数据库中查询用户信息
        currentUser = this.getById(currentUser.getId());
        if (Objects.isNull(currentUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;

    }

    /**
     * 用户注销
     * @param request 请求
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }



}




