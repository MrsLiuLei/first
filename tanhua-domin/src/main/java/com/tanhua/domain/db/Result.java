package com.tanhua.domain.db;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    private String id;
    private String conclusion;
    private String cover;
}
