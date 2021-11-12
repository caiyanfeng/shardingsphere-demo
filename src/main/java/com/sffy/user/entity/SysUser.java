package com.sffy.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @describe: 描述
 * @Author: Cai_YF
 * @Date: 2021/11/9 16:16
 * @Version: v1.0
 */
@Data
public class SysUser {

    @TableId
    private Long id;

    private String username;

    private String password;

    private String remark;

}
