package com.tanhua.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerVo1 implements Serializable {
    private String questionId;
    private String optionId;
}
