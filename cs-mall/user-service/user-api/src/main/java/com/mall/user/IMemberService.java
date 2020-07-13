package com.mall.user;
import com.mall.user.dto.*;

/**
 *  ciggar
 * create-date: 2019/7/30-下午11:47
 * 会员服务
 */
public interface IMemberService {

    /**
     * 根据用户id查询用户会员信息
     * @param request
     * @return
     */
    QueryMemberResponse queryMemberById(QueryMemberRequest request);

    /**
     * 修改用户头像
     * @param request
     * @return
     */
    HeadImageResponse updateHeadImage(HeadImageRequest request);

    /**
     * 更新信息
     * @param request
     * @return
     */
    UpdateMemberResponse updateMember(UpdateMemberRequest request);

    /**
     * 登录
     * @param loginRequest
     * @return
     */
    UserLoginResponse login(UserLoginRequest loginRequest);

    UserVerifyLoginResponse verifyLoginUser(String token);

    Integer updatePasswordByUsername(String username, String md5Password);

    /**
     * 在邮箱中激活账号
     * @param request
     * @return
     */
    UserVerifyResponse verifyUser(UserVerifyRequest request);
}
