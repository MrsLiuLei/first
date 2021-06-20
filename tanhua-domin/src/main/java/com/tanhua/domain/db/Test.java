package com.tanhua.domain.db;

import lombok.Data;

import java.io.Serializable;
@Data
public class Test implements Serializable {
    private int id;
    private String name;
    private String cover;
    private String level;
    private int star;
    private int isLock;
    private int reportId;
}
