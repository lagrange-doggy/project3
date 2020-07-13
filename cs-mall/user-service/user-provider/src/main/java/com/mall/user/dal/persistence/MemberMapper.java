package com.mall.user.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.user.dal.entitys.Member;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface MemberMapper extends TkMapper<Member> {

    @Update("update tb_member set password = #{md5Password} where username = #{username}")
    Integer updatePasswordByUsername(@Param("username") String username, @Param("md5Password") String md5Password);

    void updateIsVerifiedByUsername(@Param("userName") String userName);
}
