package com.tanhua.server.controller;

import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.FriendVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.UserHolder;
import com.tanhua.server.service.SettingService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserInfoController {
    @Autowired
    private UserService userService;
    @Autowired
    private SettingService settingService;
    @GetMapping
    public ResponseEntity findUserInfo(Long userID,Long huanxinID){
        Long userId=huanxinID;
        if (userId==null){
            userId=userID;
        }
        if (userId==null){
            User user = UserHolder.getUser();
            if (user==null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error("000006","请重新登陆后再操作"));
            }
            userId=user.getId();
        }
        UserInfoVo info = userService.findUserInfoById(userId);
        return ResponseEntity.ok(info);
    }
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfoVo userInfoVo){
        userService.uodateUserInfo(userInfoVo);
        return ResponseEntity.ok(null);
    }
    @PostMapping("/header")
    public ResponseEntity updatePhoto(MultipartFile headPhoto){
        userService.updateUserAvatar(headPhoto);
        return ResponseEntity.ok(null);
    }
    @GetMapping("/settings")
    public ResponseEntity findSetting(){
        return settingService.findSetting();
    }
    @PostMapping("/notifications/setting")
    public ResponseEntity updateNotification(@RequestBody Map map) {
        //1、获取输入内容
        boolean like = (Boolean) map.get("likeNotification");
        boolean pinglun = (Boolean) map.get("pinglunNotification");
        boolean gonggao = (Boolean) map.get("gonggaoNotification");
        //2、调用service保存或者更新
        return settingService.updateNotification(like,pinglun,gonggao);
    }
    @PostMapping("/questions")
    public ResponseEntity saveQuestions(@RequestBody Map map) {
        //1、获取输入内容
        String content = (String) map.get("content");
        //2、调用service保存或者更新
        return settingService.saveQuestions(content);
    }
    @PostMapping("/phone/sendVerificationCode")
    public void sendVerificationCode(){
       userService.sendValidateCode(UserHolder.getUser().getMobile());
    }
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkValidateCode(@RequestBody Map<String,String> param){
        String  phone = UserHolder.getUser().getMobile();
        String verificationCode = param.get("verificationCode");
        Map<String, Boolean> map1 = userService.loginVerification2(phone, verificationCode);
        return ResponseEntity.ok(map1);
    }
    @PostMapping("/phone")
    public ResponseEntity changPhone(@RequestBody Map<String,String> param,@RequestHeader("Authorization") String token){
        userService.updateUser(param.get("phone"),token);
        return ResponseEntity.ok(null);
    }
    @GetMapping("/blacklist")
    public ResponseEntity findBlackList(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10")  int pagesize){
        return settingService.findBlackList(page,pagesize);
    }
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity delBlacklist(@PathVariable("uid") long deleteUserId) {
        return settingService.deleteBlack(deleteUserId);
    }
    @GetMapping("/counts")
    public ResponseEntity counts(){
        return userService.count();
    }
    @GetMapping("/friends/{type}")
    public ResponseEntity queryUserLikeList(@PathVariable int type,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pagesize){
        page=page<1?1:page;
        return userService.findUserLike(page,pagesize,type);
    }
    @PostMapping("/fans/{id}")
    public ResponseEntity fansLike(@PathVariable("id") Long likeUserId) {
        return userService.fansLike(likeUserId);
    }
}
