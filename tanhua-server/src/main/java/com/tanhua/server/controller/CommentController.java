package com.tanhua.server.controller;

import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentsService commentService;
    @GetMapping
    public ResponseEntity findPage(String movementId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        page = page > 0 ? page : 1;
        return  commentService.findComment(movementId, page, pagesize);
    }
    @PostMapping
    public ResponseEntity add(@RequestBody Map<String,String> paramMap){
        commentService.addComment(paramMap);
        return ResponseEntity.ok(null);
    }
    /**
     * 点赞
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity<Long> like(@PathVariable("id") String commentId){
        Long total = commentService.likeComment(commentId);
        return ResponseEntity.ok(total);
    }

    /**
     * 取消点赞
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Long> unlike(@PathVariable("id") String commentId){
        Long total = commentService.unlikeComment(commentId);
        return ResponseEntity.ok(total);
    }
}
