package com.tanhua.domain.vo;

import com.tanhua.domain.db.Answer;
import lombok.Data;

import java.util.List;

@Data
public class QuestionVo {
    private String  id;
    private String question;
    private List<AnswerVo> options;
}
