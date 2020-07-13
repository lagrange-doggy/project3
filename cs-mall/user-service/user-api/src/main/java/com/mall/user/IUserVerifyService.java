package com.mall.user;

import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;

/**
 * @author 杨
 * @create 2020-07-12 15:54
 */
public interface IUserVerifyService {

    UserVerifyResponse verify(UserVerifyRequest request);
}
