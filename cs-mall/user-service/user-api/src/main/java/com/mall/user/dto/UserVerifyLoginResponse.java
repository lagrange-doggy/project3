package com.mall.user.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

/* *
@author  Walker-胡
@create 2020-07-11 17:25
*/
@Data
public class UserVerifyLoginResponse extends AbstractResponse {
       private Long uid;
       private String file;
}
