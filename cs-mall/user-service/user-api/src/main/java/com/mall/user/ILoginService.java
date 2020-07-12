package com.mall.user;

import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;

/* *
@author  Walker-胡
@create 2020-07-11 16:36
*/
public interface ILoginService {
    CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest);
}
