package com.sffy.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @describe: 描述
 * @Author: Cai_YF
 * @Date: 2021/11/9 16:18
 * @Version: v1.0
 */
@Data
@TableName(value = "t_business")
public class TBusiness {

    @TableId
    private Long id;

    private String name;

    private Integer type;

    private String createBy;

    private Timestamp createTime;

    private String remark;
}
