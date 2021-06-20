package com.tanhua.domain.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class Answer implements Serializable {
    private String   id;
    private String option;
    private String score;
}
