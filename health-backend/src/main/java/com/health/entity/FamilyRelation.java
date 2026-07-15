package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("family_relation")
public class FamilyRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerUserId;

    private Long memberUserId;

    private String relationship;

    private String displayName;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
