package com.tensquare.article.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tensquare.article.pojo.Article;
import org.springframework.stereotype.Repository;

//使用mybatis-plus内置方法
@Repository
public interface ArticleDao extends BaseMapper<Article> {
}
