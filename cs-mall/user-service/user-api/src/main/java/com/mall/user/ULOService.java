package com.mall.user;


import com.mall.user.dto.KaptchaCodeRequest;

/**
 * @author 韩
 * @create 2020-07-10 1:13
 */
public interface ULOService {
    void loginOut(KaptchaCodeRequest request);
}
