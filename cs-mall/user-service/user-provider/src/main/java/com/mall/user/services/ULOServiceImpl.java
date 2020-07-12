package com.mall.user.services;

import com.mall.user.ULOService;
import com.mall.user.dto.KaptchaCodeRequest;
import com.mall.user.dto.KaptchaCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 韩
 * @create 2020-07-10 1:16
 */
@Slf4j
@Component
@Service
public class ULOServiceImpl implements ULOService {

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void loginOut(KaptchaCodeRequest request) {
        KaptchaCodeResponse response = new KaptchaCodeResponse();
        request.requestCheck();
        String ULO_UUID = "loginOut";
        String redisKey = ULO_UUID + request.getUuid();
        RBucket<String> rBucket = redissonClient.getBucket(redisKey);
        //输出日志
        log.info("请求登出的 redisKey = {}", redisKey);
    }
}
