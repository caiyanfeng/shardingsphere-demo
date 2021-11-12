package com.sffy.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @describe: 自定义数据库分片规则，貌似没有用到。已经通过application.yml配置实现了
 * @Author: Cai_YF
 * @Date: 2021/11/11 11:41
 * @Version: v1.0
 */
@Slf4j
public class MyShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {

    /**
     *
     * @param databaseNames 数据源名或表名，配置项中的data0、data1
     * @param shardingValue SQL 分片列 对应的实际值
     * @return
     */
    @Override
    public String doSharding(final Collection<String> databaseNames, final PreciseShardingValue<Integer> shardingValue) {
        log.info("Sharding input:" + shardingValue.getValue());
        for (String each : databaseNames) {
            log.info("表名？:{}" + each);
            //data0.endsWith("0") -> true
            if (each.endsWith(shardingValue.getValue() + "")) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }
}
