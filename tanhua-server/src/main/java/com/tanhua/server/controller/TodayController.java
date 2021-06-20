package com.tanhua.server.controller;

import com.tanhua.domain.mongo.NearUserVo;
import com.tanhua.domain.vo.RecommendQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tanhua")
public class TodayController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserLikeService userLikeService;
    @Autowired
    private IMService imService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private TodayBestService todayBestService;
    @GetMapping("/todayBest")
    public ResponseEntity findBest(){
        return todayBestService.findTodayBest();
    }
    @GetMapping("/recommendation")
    public ResponseEntity RecommendList(RecommendQueryParam queryParam){
        return todayBestService.RecommendUser(queryParam);
    }
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity<TodayBestVo> queryUserDetail(@PathVariable("id") Long userId){
       return todayBestService.getTodayUserInfo(userId);
    }
    @GetMapping("/strangerQuestions")
    public ResponseEntity<String> strangerQuestions(Long userId){
       return todayBestService.getQuestion(userId);
    }
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyStrangerQuestions(@RequestBody Map<String,Object> paramMap){
        imService.replyQuestion(paramMap);
        return ResponseEntity.ok(null);
    }
    /**
     * 搜附近
     * @param gender
     * @param distance
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity searchNearBy(@RequestParam(required=false) String gender,
                                       @RequestParam(defaultValue = "2000") String distance){
        List<NearUserVo> list = locationService.searchNearBy(gender,distance);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{id}/love")
    public void addLike(@PathVariable("id") Long likeUserId){
        userLikeService.add(likeUserId);
    }
    @GetMapping("/{id}/unlove")
    public void deleteLike(@PathVariable("id") Long likeUserId){
        userLikeService.delete(likeUserId);
    }
    @GetMapping("/cards")
    public ResponseEntity find(){
        return userService.findPage();
    }
}
