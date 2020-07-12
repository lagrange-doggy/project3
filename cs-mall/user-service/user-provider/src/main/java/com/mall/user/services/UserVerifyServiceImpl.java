package com.mall.user.services;

import com.mall.user.IUserVerifyService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 杨
 * @create 2020-07-12 15:56
 */
@Service
@Component
public class UserVerifyServiceImpl implements IUserVerifyService {

    @Autowired
    private UserVerifyMapper userVerifyMapper;

    @Autowired
    MemberMapper memberMapper;

    @Override
    @Transactional
    public UserVerifyResponse verify(UserVerifyRequest request) {

        UserVerifyResponse userVerifyResponse = new UserVerifyResponse();

        request.requestCheck();
        //根据uuid去查询userVerify表
        Example example = new Example(UserVerify.class);
        example.createCriteria().andEqualTo("uuid",request.getUuid());
        List<UserVerify> userVerifyList = userVerifyMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(userVerifyList)){
            userVerifyResponse.setCode(SysRetCodeConstants.USER_INFOR_INVALID.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USER_INFOR_INVALID.getMessage());
            return userVerifyResponse;
        }
        //把两个username进行对比
        UserVerify userVerify = userVerifyList.get(0);
        String username = request.getUserName();
        if (!username.equals(userVerify.getUsername())){
            userVerifyResponse.setCode(SysRetCodeConstants.USER_INFOR_INVALID.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USER_INFOR_INVALID.getMessage());
            return userVerifyResponse;
        }
        userVerify.setIsVerify("Y");

        //如果example被修改需要先清空
//        example.clear();
//        example.createCriteria().andEqualTo("uuid",request.getUuid());

        //对比完成后，修改userverify激活字段
        int effctedRows = userVerifyMapper.updateByExample(userVerify,example);
        if (effctedRows < 1){
            userVerifyResponse.setCode(SysRetCodeConstants.USER_INFOR_UPDATE_FAIL.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USER_INFOR_UPDATE_FAIL.getMessage());
            return userVerifyResponse;
        }

        //再通过username查询member 改member表的激活字段
        Example exampleMember = new Example(Member.class);
        exampleMember.createCriteria().andEqualTo("username",request.getUserName());
        List<Member> members = memberMapper.selectByExample(exampleMember);
        if (CollectionUtils.isEmpty(members)){
            userVerifyResponse.setCode(SysRetCodeConstants.USER_INFOR_UPDATE_FAIL.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USER_INFOR_UPDATE_FAIL.getMessage());
            return userVerifyResponse;
        }
        Member member = members.get(0);

        member.setIsVerified("Y");
        int effecteRows2 = memberMapper.updateByExample(member,exampleMember);
        if (effecteRows2 < 1){
            userVerifyResponse.setCode(SysRetCodeConstants.USER_INFOR_UPDATE_FAIL.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.USER_INFOR_UPDATE_FAIL.getMessage());
            return userVerifyResponse;
        }
        userVerifyResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userVerifyResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());

        return userVerifyResponse;
    }
}
