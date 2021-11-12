package com.sffy.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @describe: 描述
 * @Author: Cai_YF
 * @Date: 2021/11/12 12:28
 * @Version: v1.0
 */
@Data
@TableName(value = "t_order")
public class TOrder {

    @TableId
    private Long id;

    private Long userId;

    private String name;

    private String value;

    private Integer type;

    private String remark;
}
