package com.mall.user.services;/**
 * Created by ciggar on 2019/7/30.
 */

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.google.gson.Gson;
import com.mall.user.IMemberService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.converter.MemberConverter;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.*;
import com.mall.user.utils.ExceptionProcessorUtils;
import com.mall.user.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  ciggar
 * create-date: 2019/7/30-下午11:51
 */
@Slf4j
@Component
@Service
public class MemberServiceImpl implements IMemberService {

    @Autowired
    MemberMapper memberMapper;

//    @Autowired
//    IUserService userLoginService;

    @Autowired
    MemberConverter memberConverter;

    /**
     * 根据用户id查询用户会员信息
     * @param request
     * @return
     */
    @Override
    public QueryMemberResponse queryMemberById(QueryMemberRequest request) {
        QueryMemberResponse queryMemberResponse=new QueryMemberResponse();
        try{
            request.requestCheck();
            Member member=memberMapper.selectByPrimaryKey(request.getUserId());
            if(member==null){
                queryMemberResponse.setCode(SysRetCodeConstants.DATA_NOT_EXIST.getCode());
                queryMemberResponse.setMsg(SysRetCodeConstants.DATA_NOT_EXIST.getMessage());
            }
            queryMemberResponse=memberConverter.member2Res(member);
            queryMemberResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
            queryMemberResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("MemberServiceImpl.queryMemberById Occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(queryMemberResponse,e);
        }
        return queryMemberResponse;
    }

    @Override
    public HeadImageResponse updateHeadImage(HeadImageRequest request) {
        HeadImageResponse response=new HeadImageResponse();
        //TODO
        return response;
    }

    @Override
    public UpdateMemberResponse updateMember(UpdateMemberRequest request) {
        return null;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest loginRequest) {
        UserLoginResponse loginResponse = new UserLoginResponse();
        try {
            //校验用户是和否存在和激活
            Member member = checkMember(loginRequest);
            //生成token
            String token = getToken(member);
            BeanUtils.copyProperties(member, loginResponse);
            loginResponse.setToken(token);
            loginResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
            loginResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("MemberServiceImpl.UserLoginResponse Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(loginResponse, e);
        }
        return loginResponse;
    }

    private String getToken(Member member) {
        Map map = new HashMap<>();
        map.put("UserId",member.getId());
        map.put("UserName",member.getUsername());
        String msg = JSON.toJSONString(map);
        return JwtTokenUtils.builder().msg(msg).build().creatJwtToken();
    }

    private Member checkMember(UserLoginRequest loginRequest) {
        Example example = new Example(Member.class);
        //密码转换为加密形式
        String pwd = DigestUtils.md5DigestAsHex(loginRequest.getUserPwd().getBytes());
        example.createCriteria().andEqualTo("username",loginRequest.getUserName()).andEqualTo("password",pwd);
        List<Member> members = memberMapper.selectByExample(example);

        //没有匹配的信息
        if(CollectionUtils.isEmpty(members)){
            throw new ValidateException(SysRetCodeConstants.USERORPASSWORD_ERRROR.getCode(), SysRetCodeConstants.USERORPASSWORD_ERRROR.getMessage());
        }

        //验证是否激活
        Member member = members.get(0);
        if(member.getIsVerified().equals("N")){
            throw new ValidateException(SysRetCodeConstants.USER_ISVERFIED_ERROR.getCode(),SysRetCodeConstants.USER_ISVERFIED_ERROR.getMessage());
        }
        return member;
    }
    /*
     * 胡小强 验证用户登录是否合法
     * */
    @Override
    public UserVerifyLoginResponse verifyLoginUser(String token) {
        UserVerifyLoginResponse response = new UserVerifyLoginResponse();
        try {
            CheckAuthRequest request = new CheckAuthRequest();
            request.setToken(token);
            //检验token是否合法
            CheckAuthResponse checkAuthResponse = validToken(request);

            String memberInfo = checkAuthResponse.getUserinfo();
            Gson gson = new Gson();
            //将获得的Gson对象转换为Member对象
            Member member = gson.fromJson(memberInfo, Member.class);
            response.setUid(member.getId());
            response.setFile(member.getFile());
            response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        } catch (Exception e) {
            log.error("MemberServiceImpl.UserVerifyLoginResponse Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }

    @Override
    public Integer updatePasswordByUsername(String username, String md5Password) {
        return memberMapper.updatePasswordByUsername(username, md5Password);
    }



    @Autowired
    UserVerifyMapper userVerifyMapper;

    /**
     * 尚政宇
     * @param request
     * @return 在邮箱中激活账号
     * 首先根据uuid在tb_user_verify表中找到这条记录，判断is_expire是否过期，如果过期,
     * 直接返回状态码：003201；如果没有过期，将这条记录删除之，同时根据userName，在tb_member表中
     * 修改对应的字段isverified为Y
     *
     */
    @Override
    public UserVerifyResponse verifyUser(UserVerifyRequest request) {
        UserVerifyResponse response = new UserVerifyResponse();
        try {
            request.requestCheck();
            UserVerify userVerify = userVerifyMapper.selectByUUID(request.getUuid());
            if (userVerify == null || userVerify.getIsExpire().equals("Y")) {
                response.setCode(SysRetCodeConstants.DATA_NOT_EXIST.getCode());
                response.setMsg(SysRetCodeConstants.DATA_NOT_EXIST.getMessage());
            }else{
                userVerifyMapper.delete(userVerify);
                memberMapper.updateIsVerifiedByUsername(request.getUserName());
                response.setCode(SysRetCodeConstants.SUCCESS.getCode());
                response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
            }
        } catch (Exception exception) {
            log.error("MemberServiceImpl.verifyUser Occur Exception :" + exception);
            ExceptionProcessorUtils.wrapperHandlerException(response, exception);
        }
        return response;
    }


    /**
     * 验证token是否合法
     *
     * @param request
     * @return
     */

    public CheckAuthResponse validToken(CheckAuthRequest request) {
        CheckAuthResponse response = new CheckAuthResponse();
        try {
            request.requestCheck();
            String info = JwtTokenUtils.builder().token(request.getToken()).build().freeJwt();
            response.setUserinfo(info);
            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
            response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("MemberServiceImpl.loginUser Occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }


//    @Override
//    public UpdateMemberResponse updateMember(UpdateMemberRequest request) {
//        UpdateMemberResponse response = new UpdateMemberResponse();
//        try{
//            request.requestCheck();
//            CheckAuthRequest checkAuthRequest = new CheckAuthRequest();
//            checkAuthRequest.setToken(request.getToken());
//            CheckAuthResponse authResponse = userLoginService.validToken(checkAuthRequest);
//            if (!authResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
//                response.setCode(authResponse.getCode());
//                response.setMsg(authResponse.getMsg());
//                return response;
//            }
//            Member member = memberConverter.updateReq2Member(request);
//            int row = memberMapper.updateByPrimaryKeySelective(member);
//            response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
//            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
//            log.info("MemberServiceImpl.updateMember effect row :"+row);
//        }catch (Exception e){
//            log.error("MemberServiceImpl.updateMember Occur Exception :"+e);
//            ExceptionProcessorUtils.wrapperHandlerException(response,e);
//        }
//        return response;
//    }
}
