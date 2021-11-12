package com.sffy.main;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sffy.business.entity.TBusiness;
import com.sffy.business.entity.TOrder;
import com.sffy.business.mapper.TBusinessMapper;
import com.sffy.business.mapper.TOrderMapper;
import com.sffy.user.entity.SysUser;
import com.sffy.user.mapper.SysUserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @describe: 获取数据测试
 * @Author: Cai_YF
 * @Date: 2021/11/9 16:27
 * @Version: v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryDataTest {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private TBusinessMapper tBusinessMapper;
    @Autowired
    private TOrderMapper tOrderMapper;

    @Test
    public void testList(){
        List<SysUser> sysUserList = sysUserMapper.selectList(null);
        System.out.println(sysUserList.toString());
        List<TBusiness> tBusinesses = tBusinessMapper.selectList(null);
        System.out.println(tBusinesses.toString());
        // 查询不配置分片
        List<TOrder> tOrders = tOrderMapper.selectList(null);
        System.out.println(tOrders.toString());
    }

    @Test
    public void addTest(){
        // 测试不添加分片列数据，即type为null
        // 报异常SQLException: Field 'type' doesn't have a default value
        // TBusiness tBusiness = new TBusiness();
        // tBusiness.setCreateBy("cyf");
        // tBusiness.setName("测试type为null");
        // tBusiness.setRemark("测试type为null");
        // tBusinessMapper.insert(tBusiness);

        // 分别添加数据成功
        TBusiness tBusiness = new TBusiness();
        tBusiness.setCreateBy("cyf");
        tBusiness.setName("测试data1");
        tBusiness.setRemark("ces");
        tBusiness.setType(1);
        tBusinessMapper.insert(tBusiness);
        TBusiness tBusiness1 = new TBusiness();
        tBusiness1.setCreateBy("cyf");
        // tBusiness1.setCreateTime(LocalDateTime.now());
        tBusiness1.setName("测试data2");
        tBusiness1.setRemark("ces");
        tBusiness1.setType(2);
        tBusinessMapper.insert(tBusiness1);
    }

    @Test
    public void editTest(){
        QueryWrapper<TBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TBusiness::getId,1L);
        // 如果不指定分片字段，并且存在主键重复情况，会查不到指定的数据（多个分片表数据）
        queryWrapper.lambda().eq(TBusiness::getType,1);
        TBusiness tBusiness = tBusinessMapper.selectOne(queryWrapper);
        tBusiness.setName("修改商品1");
        tBusiness.setRemark("修改数据");
        // 会报异常：ShardingSphereException: Can not update sharding key。
        // tBusinessMapper.updateById(tBusiness);
        // 带上分片列的查询条件后update成功
        UpdateWrapper<TBusiness> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TBusiness::getType,1);
        tBusinessMapper.update(tBusiness,updateWrapper);

    }

    @Test
    public void deleteTest(){
        // 根据id主键进行删除，当存在相同主键时，会同时删除
        // tBusinessMapper.deleteById(3L);
        // 添加指定分片字段进行删除
        QueryWrapper<TBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TBusiness::getId,3L);
        queryWrapper.lambda().eq(TBusiness::getType,1);
        tBusinessMapper.delete(queryWrapper);

    }

    @Test
    public void testQuery1(){
        // 存在相同主键，查询结果为多分片库聚合
        QueryWrapper<TBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TBusiness::getId,1L);
        List<TBusiness> tBusinesses = tBusinessMapper.selectList(queryWrapper);
        System.out.println(tBusinesses.toString());

    }
}
