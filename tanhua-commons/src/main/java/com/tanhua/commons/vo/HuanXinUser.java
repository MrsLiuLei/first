package com.tanhua.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuanXinUser implements Serializable {

    private String username;
    private String password;
    private String nickname;
}
