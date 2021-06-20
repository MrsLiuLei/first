package com.tanhua.manage.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.exception.BusinessException;
import com.tanhua.manage.mapper.AdminMapper;
import com.tanhua.manage.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    private static final String CACHE_KEY_CAP_PREFIX = "MANAGE_CAP_";
    public static final String CACHE_KEY_TOKEN_PREFIX="MANAGE_TOKEN_";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 保存生成的验证码
     * @param uuid
     * @param code
     */
    public void saveCode(String uuid, String code) {
        String key = CACHE_KEY_CAP_PREFIX + uuid;
        // 缓存验证码，10分钟后失效
        redisTemplate.opsForValue().set(key,code, Duration.ofMinutes(10));
    }

    /**
     * 获取登陆用户信息
     * @return
     */
    public Admin getByToken(String authorization) {
        String token = authorization.replaceFirst("Bearer ","");
        String tokenKey = CACHE_KEY_TOKEN_PREFIX + token;
        String adminString = (String) redisTemplate.opsForValue().get(tokenKey);
        Admin admin = null;
        if(StringUtils.isNotEmpty(adminString)) {
            admin = JSON.parseObject(adminString, Admin.class);
            // 延长有效期 30分钟
            redisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        }
        return admin;
    }
    public ResponseEntity login(Map<String,String> map){
        String username = map.get("username");
        String password = map.get("password");
        String verificationCode= map.get("verificationCode");
        String uuid= map.get("uuid");
        if (StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
            throw  new BusinessException("用户名或者密码为空");
        }

        if(StringUtils.isEmpty(verificationCode) || StringUtils.isEmpty(uuid)) {
            throw  new BusinessException("验证码为空");
        }
        String key = CACHE_KEY_CAP_PREFIX + uuid;
        String o = (String)redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(o)){
            throw  new BusinessException("验证码校验失败");
        }
        redisTemplate.delete(key);
        Admin admin = query().eq("username", username).one();
        if (admin==null){
            throw  new BusinessException("用户名错误");
        }
        if(!admin.getPassword().equals(SecureUtil.md5(password))) {
            throw  new BusinessException("密码错误");
        }
        String jwt = jwtUtils.createJWT(admin.getUsername(), admin.getId());
        String s = JSON.toJSONString(admin);
        redisTemplate.opsForValue().set(CACHE_KEY_TOKEN_PREFIX+jwt,s,Duration.ofHours(1));
        Map result = new HashMap();
        result.put("token",jwt);
        return ResponseEntity.ok(result);
    }
    public void logout(String token){
        String key=CACHE_KEY_TOKEN_PREFIX+token;
        redisTemplate.delete(key);
    }
}
