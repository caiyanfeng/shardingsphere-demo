package com.sffy.config;

import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @describe: mybatis自定义拦截器
 * @Author: Cai_YF
 * @Date: 2021/11/12 15:32
 * @Version: v1.0
 */
@Slf4j
// @Component
// @Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
// 拦截StatementHandler类中参数类型为Statement的prepare方法（prepare=在预编译SQL前加入修改的逻辑）
// @Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //
        // // 通过MetaObject优雅访问对象的属性，这里是访问statementHandler的属性;：MetaObject是Mybatis提供的一个用于方便、
        // // 优雅访问对象属性的对象，通过它可以简化代码、不需要try/catch各种reflect异常，同时它支持对JavaBean、Collection、Map三种类型对象的操作。
        // //实际执行的sql是经过层层封装，无法利用简单的一层反射获取到需要使用提供的快捷方法或者对获取到关键数据进行拼装
        // MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
        //         new DefaultReflectorFactory());
        //
        // // 先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        // MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        // id为执行的mapper方法的全路径名，如com.cq.UserMapper.insertUser， 便于后续使用反射
        String sqlId = mappedStatement.getId();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        log.info("------sqlCommandType----类型：{}--" , sqlCommandType);
        // 获取sql
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String sql = boundSql.getSql();
        if (StringUtils.isBlank(sql)) {
            return invocation.proceed();
        }

        sql = sql.substring(1);
        if(sql.indexOf("(") == -1 || sql.indexOf(")") == -1){
            throw new RuntimeException("Expression need set ResultMapId.");
        }
        String resultMapId = sql.substring(sql.indexOf("(")+1,sql.indexOf(")"));
        String tableName = sql.substring(0,sql.indexOf("("));

        if(resultMapId.indexOf(".") == -1){
            resultMapId = mappedStatement.getId().substring(0,mappedStatement.getId().lastIndexOf(".")+1)+resultMapId;
        }

        ResultMap resultMap = mappedStatement.getConfiguration().getResultMap(resultMapId);

        if(resultMap == null){
            throw new RuntimeException("Can not find ResultMap by id: "+resultMapId);
        }


        Set<ResultMapping> mappings = new HashSet<ResultMapping>();
        if(parameter instanceof Map){
            for(ResultMapping mapping : resultMap.getResultMappings()){
                for(String property : ((Map<String,Object>)parameter).keySet()){
                    if(mapping.getProperty().toUpperCase(Locale.US).equals(property.toUpperCase(Locale.US))){
                        mappings.add(mapping);
                    }
                }
            }
        }else{
            mappings.addAll(resultMap.getResultMappings());
        }

        Iterator<ResultMapping> mappingsIterator  = mappings.iterator();
        while (mappingsIterator.hasNext()) {
            ResultMapping mapping =  mappingsIterator.next();
            if(mapping.getNestedQueryId() != null || mapping.getNestedResultMapId() !=null){
                mappingsIterator.remove();
            }
        }

        if(mappedStatement.getSqlCommandType().equals(SqlCommandType.INSERT)) {
            sql = insert(tableName,mappings);
        }else if(mappedStatement.getSqlCommandType().equals(SqlCommandType.UPDATE)){
            sql = update(tableName, mappings);
        }else if(mappedStatement.getSqlCommandType().equals(SqlCommandType.DELETE)){
            sql = delete(tableName, resultMap);
        }

        TextSqlNode sqlNode = new TextSqlNode(sql);
        DynamicSqlSource sqlSource = new DynamicSqlSource(mappedStatement.getConfiguration(),sqlNode);

        invocation.getArgs()[0] = copyFromMappedStatement(mappedStatement, sqlSource);
        return invocation.proceed();
    }

    public void addColumn(StringBuffer sql, ResultMapping resultMapping){
        sql.append(resultMapping.getColumn().toUpperCase(Locale.US));
    }

    public void addProperty(StringBuffer sql, ResultMapping resultMapping){
        sql.append("#{").append(resultMapping.getProperty()).append(",");
        if(resultMapping.getJdbcType()!= null){
            sql.append("jdbcType=").append(resultMapping.getJdbcType().name()).append(",");
        }
        if(resultMapping.getTypeHandler()!=null){
            sql.append("typeHandler=").append(resultMapping.getTypeHandler().getClass().getName()).append(",");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append("}");
    }


    private String insert(String tableName,Set<ResultMapping> mappings){
        StringBuffer insertSql = new StringBuffer("INSERT INTO ").append(tableName).append(" (");
        StringBuffer columnSql = new StringBuffer();
        StringBuffer propertySql = new StringBuffer();
        for(ResultMapping mapping : mappings){
            addColumn(columnSql,mapping);
            columnSql.append(",");

            addProperty(propertySql,mapping);
            propertySql.append(",");
        }
        if(!mappings.isEmpty()){
            columnSql.deleteCharAt(columnSql.length()-1);
            propertySql.deleteCharAt(propertySql.length()-1);
        }
        insertSql.append(columnSql).append(") VALUES (").append(propertySql).append(")");
        return insertSql.toString();
    }

    private String update(String tableName,Set<ResultMapping> mappings){
        StringBuffer updateSql = new StringBuffer("UPDATE ").append(tableName).append(" SET ");
        StringBuffer idSql = new StringBuffer(" WHERE ");
        for(ResultMapping mapping : mappings){
            if(!mapping.getFlags().isEmpty() && mapping.getFlags().contains(ResultFlag.ID)){
                addColumn(idSql,mapping);
                idSql.append("=");
                addProperty(idSql,mapping);
                continue;
            }
            addColumn(updateSql, mapping);
            updateSql.append("=");
            addProperty(updateSql,mapping);
            updateSql.append(",");
        }
        if(!mappings.isEmpty()){
            updateSql.deleteCharAt(updateSql.length()-1);
        }
        updateSql.append(idSql);
        return updateSql.toString();
    }

    private String delete(String tableName, ResultMap resultMap){
        StringBuffer deleteSql = new StringBuffer("DELETE FROM ").append(tableName).append(" WHERE ");
        for(ResultMapping mapping : resultMap.getResultMappings()){
            if(!mapping.getFlags().isEmpty() && mapping.getFlags().contains(ResultFlag.ID)){
                addColumn(deleteSql,mapping);
                deleteSql.append("=");
                addProperty(deleteSql,mapping);
                continue;
            }
        }
        return deleteSql.toString();
    }


    private MappedStatement copyFromMappedStatement(MappedStatement ms,SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),ms.getId(),newSqlSource,ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if(ms.getKeyProperties() != null && ms.getKeyProperties().length !=0){
            StringBuffer keyProperties = new StringBuffer();
            for(String keyProperty : ms.getKeyProperties()){
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length()-1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    /**
     * 获取类的所有属性，包括父类
     *
     * @param object
     * @return
     */
    public static Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }
}
