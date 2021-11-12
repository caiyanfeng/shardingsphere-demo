package com.sffy.business.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sffy.business.entity.TBusiness;
import org.apache.ibatis.annotations.Mapper;

/**
 * @describe: 描述
 * @Author: Cai_YF
 * @Date: 2021/11/9 16:23
 * @Version: v1.0
 */
@DS("sharding")
@Mapper
public interface TBusinessMapper extends BaseMapper<TBusiness> {

}
