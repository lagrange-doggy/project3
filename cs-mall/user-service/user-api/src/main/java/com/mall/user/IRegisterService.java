package com.mall.user;

import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;

/**
 * @author 杨
 * @create 2020-07-10 20:01
 */
public interface IRegisterService {

    /*
    * 用户注册接口
    * */
    UserRegisterResponse register(UserRegisterRequest registerRequest);
}
