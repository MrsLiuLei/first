package com.tanhua.domain.mongo;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;
@Data
public class TimeLine implements Serializable {
    private ObjectId id;

    private Long userId; // 好友id
    private ObjectId publishId; //发布id

    private Long created; //发布的时间
}
