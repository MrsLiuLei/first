package com.tanhua.server.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testJwt(){
        HashMap<String, Object> map = new HashMap<>();
        String secret="itcast";
        map.put("mobile","123456789");
        map.put("id","2");
        String jwt = Jwts.builder().setClaims(map).signWith(SignatureAlgorithm.HS256, secret).compact();
        System.out.println(jwt);
        Map<String,Object> body=Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
        System.out.println(body);
    }
}
