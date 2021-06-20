package com.tanhua.server.controller;

import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.IMService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/messages")
public class IMController {
    @Autowired
    private IMService imService;
    @PostMapping("/contacts")
    public ResponseEntity addContacts(@RequestBody Map<String,Long> paramMap){
        imService.addContacts(paramMap.get("userId"));
        return ResponseEntity.ok(null);
    }
    /**
     * 联系人列表
     * @return
     */
    @GetMapping("/contacts")
    public ResponseEntity queryContactsList(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer pagesize,
                                            @RequestParam(required = false) String keyword){
        return imService.findPage(page,pagesize,keyword);
    }
    /**
     * 查询点赞列表
     */
    @GetMapping("likes")
    public ResponseEntity<PageResult> queryLikeList(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize){
        return imService.findPageByUserId(page,pageSize,1);
    }

    /**
     * 查询评论列表
     */
    @GetMapping("comments")
    public ResponseEntity<PageResult> queryCommentList(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10")Integer pageSize) {
        return imService.findPageByUserId(page,pageSize,2);
    }

    /**
     * 查询喜欢列表
     */
    @GetMapping("loves")
    public ResponseEntity<PageResult> queryLoveList(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return imService.findPageByUserId(page,pageSize,3);
    }
}
