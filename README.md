# shardingsphere-demo

#### 介绍
使用数据库中间件shardingsphere-jdbc测试多数据源分库可行性

#### 软件架构
Springboot + Mybatis-plus + sharding-jdbc + dynamic-datasource

#### 使用说明
前言:现有三个DB，一个是用户DB，另外两个是业务数据DB（以前按业务分开的），以前做法是两个工程项目分开访问
，考虑通过一个工程访问上述三个DB，用户登录后同时能看到两个业务DB数据。
___
准备三个测试DB
主库：sharding_user
业务库：sharding_data1和sharding_data2
___
只是分库
不考虑分表、不考虑分表、不考虑分表、不考虑分表。
重要事情说多几遍！
___
懒得写controller和service。直接写个测试类进行CORD测试。请看QueryDataTest测试类。

___

通过测试类可以看到update和delete语句都要添加分片列字段进行查询。不然会报错之类的。
待完善。。。
这里考虑通过mybatis自定义拦截器进行拦截语句，手动添加type查询。
#### 参考

* [ShardingSphere-JDBC官网](https://shardingsphere.apache.org/document/current/cn/quick-start/shardingsphere-jdbc-quick-start/)
* [Shardingsphere与dynamic-datasource配合实现多数据源切换](https://www.jianshu.com/p/0bf72d112522)
* [ShardingSphere 数据分片](https://blog.csdn.net/weixin_38003389/article/details/90518112)
* [Sharding-JDBC分库不分表、分库分表，主从分库分表](https://blog.csdn.net/u022812849/article/details/111768885)
* [ShardingSphere入门-使用springboot集成JDBC实现数据分片(分库、分表)](https://blog.csdn.net/zjh19961213/article/details/108515357)