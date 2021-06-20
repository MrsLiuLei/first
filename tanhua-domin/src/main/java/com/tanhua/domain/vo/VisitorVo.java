package com.tanhua.domain.vo;

import lombok.Data;

@Data
public class VisitorVo {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private Integer fateValue;
}
