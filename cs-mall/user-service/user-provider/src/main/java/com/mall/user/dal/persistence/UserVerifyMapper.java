package com.mall.user.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.user.dal.entitys.UserVerify;
import org.apache.ibatis.annotations.Param;

public interface UserVerifyMapper extends TkMapper<UserVerify> {

    UserVerify selectByUUID(@Param("uuid") String uuid);
}