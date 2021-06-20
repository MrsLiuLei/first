package com.tanhua.server.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${tanhua.secret}")
    private String secret;
    public String createJwt(String phone,Long userId){
        Map<String, Object> map = new HashMap<>();
        map.put("mobile",phone);
        map.put("id",userId);
        long nowMillis = System.currentTimeMillis();
        Date date = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setClaims(map)/*.setIssuedAt(date)*/.signWith(SignatureAlgorithm.HS256, secret);
        return builder.compact();
    }
}
