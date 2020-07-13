package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.user.IKaptchaService;
import com.mall.user.IRegisterService;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.KaptchaCodeRequest;
import com.mall.user.dto.KaptchaCodeResponse;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 杨
 * @create 2020-07-10 16:16
 */
@RestController
@RequestMapping("/user")
public class RegisterController {

    @Reference
    private IKaptchaService iKaptchaService;

    @Reference
    private IRegisterService registerService;

    @PostMapping("register")
    @Anoymous
    public ResponseData register(@RequestBody Map<String,String> map, HttpServletRequest request){
        String username = map.get("userName");
        String userpwd = map.get("userPwd");
        String captcha = map.get("captcha");
        String email = map.get("email");

        //验证验证码
        KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
        String uuid = CookieUtil.getCookieValue(request,"kaptcha_uuid");
        kaptchaCodeRequest.setUuid(uuid);
        kaptchaCodeRequest.setCode(captcha);
        KaptchaCodeResponse response = iKaptchaService.validateKaptchaCode(kaptchaCodeRequest);
        String code = response.getImageCode();
        if (code.equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }

        //去向用户表插入记录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setUserName(username);
        registerRequest.setUserPwd(userpwd);
        UserRegisterResponse registerResponse = registerService.register(registerRequest);
        if (registerResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(null);
        }
        return new ResponseUtil<>().setErrorMsg(registerResponse.getMsg());
    }
}
