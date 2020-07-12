package com.cskaoyan.gateway.controller.Test;

import com.mall.user.IMemberService;
import com.mall.user.annotation.Anoymous;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: jia.xue
 * @Email: xuejia@cskaoyan.onaliyun.com
 * @Description
 **/
@RestController
@Anoymous
public class TestController {

    @GetMapping("hello")
    public String hello(){
        return "hello";
    }

    @Reference(timeout = 3000, check = false)
    IMemberService menberService;

    @Value("${server.port}")
    private String serverPort;

    /**
     * 可自行设置密码注入，也可修改字符串变更修改其他账号的密码注入
     *
     * @return
     */
    @RequestMapping(value = "/password")
    public String paymentConsul() {
        String username = "cskaoyan01";
        String password = "cskaoyan01";
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        Integer update = menberService.updatePasswordByUsername(username, md5Password);
        System.out.println("~~~~~~~~********" + update + "********~~~~~~~~");
        return "PasswordController password serverPort: " + serverPort + "\t" + md5Password;
    }

}
