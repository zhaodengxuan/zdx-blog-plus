package com.zdx.entity.zdx;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zdx.entity.BaseTimeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @TableName zdx_tag
 */
@TableName(value ="zdx_tag")
@Data
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseTimeEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;



}