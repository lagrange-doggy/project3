package com.mall.user.services;

import com.alibaba.druid.util.StringUtils;
import com.mall.user.ILoginService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;
import com.mall.user.utils.JwtTokenUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/* *
@author  Walker-胡
@create 2020-07-11 16:38
*/
@Service
@Component
public class ILoginServiceImpl  implements ILoginService {
    @Override
    public CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest) {
        CheckAuthResponse response=new CheckAuthResponse ();
        JwtTokenUtils jwtTokenUtils=JwtTokenUtils.builder ().token (checkAuthRequest.getToken ()).build ();
        String info=jwtTokenUtils.freeJwt ();
        if(StringUtils.isEmpty (info)){
            response.setCode (SysRetCodeConstants.TOKEN_VALID_FAILED.getCode ());
            response.setMsg (SysRetCodeConstants.TOKEN_VALID_FAILED.getMessage ());
            return response;
        }
        response.setCode (SysRetCodeConstants.SUCCESS.getCode ());
        response.setMsg (SysRetCodeConstants.SUCCESS.getMessage ());
        response.setUserinfo (info);
        response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        return response;
    }
}
