package com.example.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.UserPageRequest;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IUserService;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service //注解  代表一个组件 供其他地方使用 标注在userservice  而不是在iuserservice
public class UserService implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public LoginDTO login(LoginRequest request) {

        User user = null;
        try {
            user = userMapper.getByUsername(request.getUsername());
        } catch (Exception e) {
//
            throw new ServiceException("账号错误");
        }
        ;

        if (user == null) {
            throw new ServiceException("账号错误");
        }

        String password = user.getPassword();
        if (!password.equals(request.getPassword())) {
            throw new ServiceException("密码错误");
        }

        LoginDTO loginDTO = new LoginDTO();
        BeanUtils.copyProperties(user, loginDTO);

        String token = TokenUtils.genToken(String.valueOf(user.getId()), user.getPassword());
        loginDTO.setToken(token);
        return loginDTO;
    }

    //    使用 ^+i 快速实现方法
    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override

    public PageInfo page(UserPageRequest userPageRequest) {
        // listByCondition 条件查询  需要传递两个参数 页码 大小
        PageHelper.startPage(userPageRequest.getPageNum(), userPageRequest.getPageSize());
        List<User> users = userMapper.listByCondition(userPageRequest);
        return new PageInfo<>(users);
    }

    ;

    @Override
    public void save(User user) {
        Date date = new Date();
        // 当做卡号来处理
        user.setUsername(DateUtil.format(date, "yyyyMMdd") + Math.abs(IdUtil.fastSimpleUUID().hashCode()));
        userMapper.save(user);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public void update(User user) {
        user.setUpdatetime(new Date());
        userMapper.updateById(user);
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public void handleAccount(User user) {
        Integer score = user.getScore();
        if (score == null) {
            return;
        }
        Integer id = user.getId();
        User dbUser = userMapper.getById(id);
        dbUser.setAccount(dbUser.getAccount() + score);
        userMapper.updateById(dbUser);
    }

}
