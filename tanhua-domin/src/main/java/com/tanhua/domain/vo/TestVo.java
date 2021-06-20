package com.tanhua.domain.vo;

import com.tanhua.domain.db.QuestionTest;
import lombok.Data;

import java.util.List;

@Data
public class TestVo {
    private String  id;
    private String name;
    private String cover;
    private String level;
    private int star;
    private int isLock;
    private String  reportId;
    private List<QuestionVo> questions;
}
