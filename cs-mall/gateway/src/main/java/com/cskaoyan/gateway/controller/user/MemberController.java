package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.user.IKaptchaService;
import com.mall.user.IMemberService;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.*;
import com.mall.user.intercepter.TokenIntercepter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @ClassName MemberController
 * @Description 会员中心控制层
 * @Author ciggar
 * @Date 2019-08-07 14:26
 * @Version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/user")
public class MemberController {

    @Reference(timeout = 3000,check = false)
    IMemberService memberService;
    public static String ACCESS_TOKEN="acess_token";

    @Reference(check = false)
    IKaptchaService iKaptchaService;

    /**
     * 根据ID查询单条会员信息
     * @param id 编号
     * @return
     */
    @GetMapping("/member/{id}")
    public ResponseData searchMemberById(@PathVariable(name = "id")long id) {
        QueryMemberRequest request=new QueryMemberRequest();
        request.setUserId(id);
        QueryMemberResponse queryMemberResponse = memberService.queryMemberById(request);
        if (!queryMemberResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil<>().setErrorMsg(queryMemberResponse.getMsg());
        }
        return new ResponseUtil<>().setData(queryMemberResponse);
    }

    /**
     * 会员信息更新
     * @return
     */
    @PutMapping("member")
    public ResponseData updateUser(@RequestBody UpdateMemberRequest request) {
        UpdateMemberResponse response = memberService.updateMember(request);
        if(response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil().setData(null);
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 登录
     * @param loginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    @Anoymous
    public ResponseData login(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response){
        //1.校验验证码是否正确
        KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
        String uuid = CookieUtil.getCookieValue(request, "kaptcha_uuid");
        kaptchaCodeRequest.setUuid(uuid);
        kaptchaCodeRequest.setCode(loginRequest.getCaptcha());
        KaptchaCodeResponse kaptchaCodeResponse = iKaptchaService.validateKaptchaCode(kaptchaCodeRequest);
        String code = kaptchaCodeResponse.getCode();
        if(!code.equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil().setErrorMsg(kaptchaCodeResponse.getMsg());
        }
        //2.登录
        UserLoginResponse loginResponse = memberService.login(loginRequest);
        if(!loginResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(loginResponse.getMsg());
        }
        //3.set-cookie
        Cookie access_token = CookieUtil.genCookie(ACCESS_TOKEN, loginResponse.getToken(), "/", 86400);
        response.addCookie(access_token);
        return new ResponseUtil<>().setData(loginResponse);
    }

    /*
     * 验证用户是否登录
     * 胡小强
     *
     * */
    @GetMapping("login")
    public ResponseData verifyLoginUser(HttpServletRequest request) {
        // 从cookie里面去取token
        String token = CookieUtil.getCookieValue(request, ACCESS_TOKEN);
        UserVerifyLoginResponse response = memberService.verifyLoginUser(token);
        if (!response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil().setData(response);
    }


    /**
     * 韩
     * <p>
     * ⽤户退出接⼝
     * <p>
     * 返回参数示例：
     * {"success":true,"message":"success","code":200,"result":null,"timestamp":1587710244802}
     * 出参 类型 含义
     * success boolean 成功标记
     * message String 具体信息
     * code Integer 状态码
     * result String(JSON) 具体数据
     * timestamp Long 时间戳
     */
    @Anoymous
    @GetMapping("/loginOut")
    public ResponseData loginOut(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TokenIntercepter.ACCESS_TOKEN)) {
                    cookie.setValue(null);
                    //让该cookie立即过期
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    //覆盖原来的cookie
                    response.addCookie(cookie);
                }
            }
        }else {
            log.info("退出登录接口异常访问！时间"+new Date());
            return new ResponseUtil<>().setErrorMsg("错误，未登陆！");
        }
        log.info("用户正常退出登陆");
        return new ResponseUtil<>().setData(null);
    }

    @Anoymous
    @GetMapping("verify")
    public ResponseData verify(UserVerifyRequest request) {
        UserVerifyResponse response = memberService.verifyUser(request);
        if (!response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }
        return new ResponseUtil<>().setData(response);
    }
}

