package com.tanhua.manage.controller;

import com.tanhua.manage.service.AnalysisService;
import com.tanhua.manage.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {
    @Autowired
    private AnalysisService analysisService;
    @GetMapping("/dashboard/summary")
    public ResponseEntity getSummary() {
    return ResponseEntity.ok(analysisService.summary());
    }
}
