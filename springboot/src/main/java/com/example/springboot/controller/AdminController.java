package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.AdminPageRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    /*
     * 通过使用@Autowired注解，Spring框架会自动查找并将符合类型要求的实现类注入到adminService字段中。
     * */
    @Autowired
    IAdminService adminService;


    @PostMapping("/login")
    /*
     *
     * @RequestBody LoginRequest request表示该方法接受一个LoginRequest对象作为参数，并且该对象的数据将从HTTP请求的主体中提取出来。
     *
     * 通过定义LoginRequest类，我们可以在登录功能中使用它来接收用户的登录请求，将用户名和密码等信息封装到一个LoginRequest对象中，方便在代码中进行处理和验证。
     * */
    public Result login(@RequestBody LoginRequest request) {
        LoginDTO login = adminService.login(request);

        return Result.success(login);
    }

    ;

//

//    public Result login(@RequestBody LoginRequest request) {
//        LoginDTO login = adminService.login(request);
//        return Result.success(login);
//    }

    @PutMapping("/password")
    public Result password(@RequestBody PasswordRequest request) {
        adminService.changePass(request);
        return Result.success();
    }

    @PostMapping("/save")
    public Result save(@RequestBody Admin obj) {
        adminService.save(obj);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Admin obj) {
        adminService.update(obj);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        adminService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Admin obj = adminService.getById(id);
        return Result.success(obj);
    }

    @GetMapping("/list")
    public Result list() {
        List<Admin> list = adminService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(AdminPageRequest pageRequest) {
        return Result.success(adminService.page(pageRequest));
    }

}
