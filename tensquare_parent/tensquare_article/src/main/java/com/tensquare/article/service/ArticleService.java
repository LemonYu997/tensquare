package com.tensquare.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {
    //注入Dao，可以忽视报错
    @Autowired
    private ArticleDao articleDao;

    //注入IdWorker
    @Autowired
    private IdWorker idWorker;

    //查询所有文章
    public List<Article> findAll() {
        //调用BaseMapper的查询所有方法
        return articleDao.selectList(null);
    }

    //根据id查询文章
    public Article findById(String articleId) {
        return articleDao.selectById(articleId);
    }

    //增加文章
    public void save(Article article) {
        //使用分布式ID生成器，这里要把Long类型拼接成字符串
        String id = idWorker.nextId() + "";
        article.setId(id);

        //初始化数据
        article.setVisits(0);   //设置浏览量
        article.setThumbup(0);  //设置点赞数
        article.setComment(0);  //设置评论数

        //新增
        articleDao.insert(article);
    }

    //修改文章
    public void updateById(Article article) {
        //根据主键id修改
        articleDao.updateById(article);

        /*
        //根据条件修改
        //创建条件对象
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        //设置条件
        wrapper.eq("id", article.getId());
        articleDao.update(article, wrapper);
        */
    }

    //根据id删除文章
    public void deleteById(String articleId) {
        articleDao.deleteById(articleId);
    }

    //分页查询
    public Page<Article> findByPage(Map<String, Object> map, Integer page, Integer size) {
        //设置查询条件
        EntityWrapper<Article> wrapper = new EntityWrapper<>();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
//            //判断是否有查询条件
//            if (map.get(key) != null) {
//                //设置查询条件
//                wrapper.eq(key, map.get(key));
//            }

            //等同于上面的if逻辑
            //参数1表示是否把后面的条件加入到查询条件中
            wrapper.eq(map.get(key) != null, key, map.get(key));
        }
        //设置分页参数
        Page<Article> pageData = new Page<>(page, size);
        //执行查询
        //参数1是分页参数，参数2是查询条件
        List<Article> list = articleDao.selectPage(pageData, wrapper);

        //封装结果
        pageData.setRecords(list);
        //返回
        return pageData;
    }
}
