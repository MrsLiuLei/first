package com.tanhua.domain.vo;

import lombok.Data;

@Data
public class CardsVo {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
}
