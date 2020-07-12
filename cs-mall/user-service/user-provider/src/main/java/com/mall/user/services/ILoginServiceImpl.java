package com.mall.user.services;

import com.mall.user.ILoginService;
import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;
import com.mall.user.utils.JwtTokenUtils;

/* *
@author  Walker-胡
@create 2020-07-11 16:38
*/
public class ILoginServiceImpl  implements ILoginService {
    @Override
    public CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest) {
        JwtTokenUtils jwtTokenUtils=JwtTokenUtils.builder ().token (checkAuthRequest.getToken ()).build ();
        String info=jwtTokenUtils.freeJwt ();
        CheckAuthResponse response=new CheckAuthResponse ();
        response.setUserinfo (info);
        return response;
    }
}
