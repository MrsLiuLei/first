package com.tanhua.server.controller;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserService;
import com.tanhua.server.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;
    @RequestMapping(value = "/findUser",method = RequestMethod.GET)
    public ResponseEntity findUser(String mobile){
        return userService.findByMobile(mobile);
    }

    /**
     * 新增用户
     */
    @RequestMapping(value = "/saveUser",method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestBody Map<String,Object> param){
        String mobile = (String)param.get("mobile");
        String password = (String)param.get("password");
        return userService.saveUser(mobile,password);
    }
    @PostMapping("/login")
    public ResponseEntity sendValidateCode(@RequestBody Map<String,String> param){
        String phone = param.get("phone");
        userService.sendValidateCode(phone);
        return ResponseEntity.ok(null);
    }
    @RequestMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map<String ,String> map){
        String  phone = map.get("phone");
        String verificationCode = map.get("verificationCode");
        Map<String, Object> map1 = userService.loginVerification(phone, verificationCode);
        return ResponseEntity.ok(map1);
    }
    @RequestMapping(value = "/loginReginfo",method = RequestMethod.POST)
    public ResponseEntity loginReginfo(@RequestBody UserInfoVo userInfoVo){
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoVo,userInfo);
        userService.saveUserInfo(userInfo);
        return ResponseEntity.ok(null);
    }
    @RequestMapping("/loginReginfo/head")
    public ResponseEntity upLoadAvatar(MultipartFile headPhoto){
        userService.updateUserAvatar(headPhoto);
        return ResponseEntity.ok(null);
    }
}
