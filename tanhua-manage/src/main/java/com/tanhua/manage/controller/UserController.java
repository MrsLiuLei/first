package com.tanhua.manage.controller;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.manage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
public class UserController {
    @Autowired
    private UserService service;
    @GetMapping("/users")
    public ResponseEntity pageFind(@RequestParam(value = "page",defaultValue = "1") Long page,
                                   @RequestParam(value = "pagesize", defaultValue = "10") Long pagesize){
        return service.findPage(page,pagesize);
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity findUserDetail(@PathVariable(value = "userId") long userId){
        UserInfo userInfo = service.findById(userId);
        return ResponseEntity.ok(userInfo);
    }
    @GetMapping("/videos")
    public ResponseEntity findAllVideos(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int pagesize,
                                        @RequestParam(required = false) Long uid,
                                        @RequestParam(required = false) String state){
        return service.findAllVideos(page,pagesize,uid);
    }
    @GetMapping("/messages")
    public ResponseEntity findAllMovements(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pagesize,
                                           @RequestParam(required = false) Long uid,
                                           @RequestParam(required = false) String state) {
        return service.findAllMovements(page,pagesize,uid);
    }
    @GetMapping("/messages/{publishId}")
    public ResponseEntity findMovementById(@PathVariable("publishId") String publishId) {
        return null;
    }
    @GetMapping("/messages/comments")
    public ResponseEntity findAllComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pagesize,
            String messageID) {
        return null;
    }
}
