package com.tanhua.domain.db;

import lombok.Data;

@Data
public class BlackList extends BasePojo{
    private Long id;
    private Long user_id;
    private Long black_user_id;
}
