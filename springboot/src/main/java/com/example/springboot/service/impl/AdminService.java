package com.example.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.AdminMapper;
import com.example.springboot.service.IAdminService;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
/*
    implements关键字表示AdminService类实现了IAdminService接口，这意味着AdminService类需要提供接口中定义的所有方法的具体实现。
 *  以下是具体的实现  IAdminService是接口
 */ public class AdminService implements IAdminService {

    @Autowired
    AdminMapper adminMapper;

    private static final String DEFAULT_PASS = "123";
    private static final String PASS_SALT = "qingge";

    @Override

    public List<Admin> list() {
        return adminMapper.list();
    }

    @Override
    public PageInfo<Admin> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(), baseRequest.getPageSize());
        List<Admin> users = adminMapper.listByCondition(baseRequest);
        return new PageInfo<>(users);
    }

    @Override
    public void save(Admin obj) {
        // 默认密码 123
        if (StrUtil.isBlank(obj.getPassword())) {
            obj.setPassword(DEFAULT_PASS);
        }
        obj.setPassword(securePass(obj.getPassword()));  // 设置md5加密，加盐
        try {
            adminMapper.save(obj);
        } catch (DuplicateKeyException e) {
            log.error("数据插入失败， username:{}", obj.getUsername(), e);
            throw new ServiceException("用户名重复");
        }
    }


    @Override
    public Admin getById(Integer id) {
        return adminMapper.getById(id);
    }

    @Override
    public void update(Admin user) {
        user.setUpdatetime(new Date());
        adminMapper.updateById(user);
    }

    @Override
    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    @Override
    public LoginDTO login(LoginRequest request) {
        Admin admin = null;
        try {
        //  接口 查询
            admin = adminMapper.getByUsername(request.getUsername());

        } catch (Exception e) {
            log.error("根据用户名{} 查询出错", request.getUsername());
            throw new ServiceException("用户名错误");
        }
        if (admin == null) {
            throw new ServiceException("用户名或密码错误");
        }
        //    校验密码是否正确 把当前的密码明文使用md5 加密之后 得到的加密 于admin 里面的密码进行比较
        //    @Override是Java中的一个注解，用于标识一个方法是覆盖（或重写）父类中的方法。 只能用于子类中
        //    当一个子类继承自父类并且定义了一个与父类中的方法具有相同名称、参数列表和返回类型的方法时，我们称子类中的方法覆盖了父类中的方法。为了确保子类中的方法确实是对父类方法的覆盖，我们可以在子类方法上添加@Override注解。
        String securePass = securePass(request.getPassword());
        String user = admin.getPassword();
        if (!user.equals(securePass)) {
            throw new ServiceException("密码错误");
        }
        LoginDTO loginDTO = new LoginDTO();
        //  把admin 的属性结构到  loginDTO
        BeanUtils.copyProperties(admin, loginDTO);
        // 生成token
        String token = TokenUtils.genToken(String.valueOf(admin.getId()), admin.getPassword());
        loginDTO.setToken(token);
        return loginDTO;
    }

    ;

    @Override
    public void changePass(PasswordRequest request) {
        // 注意 你要对新的密码进行加密
        request.setNewPass(securePass(request.getNewPass()));
        int count = adminMapper.updatePassword(request);
        if (count <= 0) {
            throw new ServiceException("修改密码失败");
        }
    }

    private String securePass(String password) {
        return SecureUtil.md5(password + PASS_SALT);
    }

}
