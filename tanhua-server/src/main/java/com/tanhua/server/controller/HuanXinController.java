package com.tanhua.server.controller;

import com.tanhua.commons.vo.HuanXinUser;
import com.tanhua.server.UserHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huanxin")
public class HuanXinController {
    @GetMapping("/user")
    public ResponseEntity getLoginHuanXin(){
        HuanXinUser user = new HuanXinUser(UserHolder.getUserId().toString(), "123456","1");
        System.out.println(UserHolder.getUserId());
        return ResponseEntity.ok(user);
    }
}
