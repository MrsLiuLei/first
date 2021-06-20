package com.tanhua.domain.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionTest implements Serializable {
    private String  id;
    private String question;
}
