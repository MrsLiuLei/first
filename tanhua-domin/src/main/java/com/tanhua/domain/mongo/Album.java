package com.tanhua.domain.mongo;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class Album implements Serializable {
    private ObjectId id;
    private ObjectId publishId;
    private Long created;
}
