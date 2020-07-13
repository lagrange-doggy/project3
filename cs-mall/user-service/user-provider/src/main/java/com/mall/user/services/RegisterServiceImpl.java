package com.mall.user.services;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.IRegisterService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 杨
 * @create 2020-07-10 20:15
 */
@Service
@Slf4j
public class RegisterServiceImpl implements IRegisterService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private UserVerifyMapper userVerifyMapper;

    @Autowired
    JavaMailSender mailSender;

    @Override
    public UserRegisterResponse register(UserRegisterRequest registerRequest) {
        UserRegisterResponse response = new UserRegisterResponse();
        //判空验证
        registerRequest.requestCheck();

        //验证用户名是否重复
        volidUserNameRepeat(registerRequest);

        //1.向用户表中插入一条记录
        Member member = new Member();
        member.setUsername(registerRequest.getUserName());
        member.setEmail(registerRequest.getEmail());
        //password加密,采用md5方式
        String userpassword = DigestUtils.md5DigestAsHex(registerRequest.getUserPwd().getBytes());
        member.setPassword(userpassword);

        member.setCreated(new Date());
        member.setUpdated(new Date());
        member.setIsVerified("N");
        member.setState(1);

        //不成功时设置返回值
        int effectRows = memberMapper.insert(member);
        if (effectRows != 1){
            response.setCode(SysRetCodeConstants.USER_REGISTER_FAILED.getCode());
            response.setMsg(SysRetCodeConstants.USER_REGISTER_FAILED.getMessage());
            return response;
        }
        //2.向用户验证表中插入一条记录
        UserVerify userVerify= new UserVerify();
        userVerify.setUsername(member.getUsername());
        //生成注册表的uuid
        String key = member.getUsername() + member.getPassword() + UUID.randomUUID().toString();
        String uuid = DigestUtils.md5DigestAsHex(key.getBytes());
        userVerify.setUuid(uuid);
        userVerify.setRegisterDate(new Date());
        userVerify.setIsExpire("N");
        userVerify.setIsVerify("N");
        int rows = userVerifyMapper.insert(userVerify);

        //用户验证表插入不成功时，同上
        if (rows != 1){
            response.setCode(SysRetCodeConstants.USER_REGISTER_VERIFY_FAILED.getCode());
            response.setMsg(SysRetCodeConstants.USER_REGISTER_VERIFY_FAILED.getMessage());
            return response;
        }

        //3.发用用户激活邮件
        sendEmail(uuid,registerRequest);


        //打印日志
        log.info("用户注册成功，注册参数request:{},{}", JSON.toJSONString(registerRequest),"xxx");
        //用户注册成功，注册参数request:registerRequest,xxx
        response.setCode(SysRetCodeConstants.SUCCESS.getCode());
        response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return response;
    }

    //发送用户激活邮件
    private void sendEmail(String uuid, UserRegisterRequest registerRequest) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("CSMALL用户激活");
        message.setFrom("ciggarnot@163.com");
        message.setTo(registerRequest.getEmail());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://localhost:8080/user/verify?uid=")
                .append(uuid).append("&username=")
                .append(registerRequest.getUserName());

        //http://localhost:8080/user/veryfy?uid=xxx&username=xxx
        message.setText(stringBuilder.toString());
        mailSender.send(message);
    }

    //验证用户名是否重复
    private void volidUserNameRepeat(UserRegisterRequest registerRequest) {

        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo("username",registerRequest.getUserName());
        List<Member> memberList = memberMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(memberList)){
            throw new ValidateException(SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getCode(), SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getMessage());
        }
    }
}
