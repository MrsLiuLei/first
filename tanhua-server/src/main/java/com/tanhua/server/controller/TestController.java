package com.tanhua.server.controller;

import com.tanhua.domain.db.Answer;
import com.tanhua.domain.vo.AnswerVo1;
import com.tanhua.domain.vo.ResultVo;
import com.tanhua.domain.vo.TestVo;
import com.tanhua.server.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testSoul")
public class TestController {
    @Autowired
    private TestService testService;
    @GetMapping
    public ResponseEntity find(){
        List<TestVo> testVos = testService.find();
        return ResponseEntity.ok(testVos);
    }
    @PostMapping
    public ResponseEntity submit(@RequestBody Map<String ,List<AnswerVo1>> answers){
        List<AnswerVo1> list = answers.get("answers");
        String s = testService.testResult(list);
        return ResponseEntity.ok(s);
    }
    @GetMapping("/report/{id}")
    public ResponseEntity report(@PathVariable("id") String id){
        ResultVo resultVo = testService.findById(id);
        return ResponseEntity.ok(resultVo);
    }
}
