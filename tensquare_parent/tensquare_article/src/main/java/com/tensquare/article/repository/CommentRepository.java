package com.tensquare.article.repository;

import com.tensquare.article.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

//使用SpringDataMongoDB，继承MongoRepository接口即可，内置了常用方法
//<操作对象，主键类型>
public interface CommentRepository extends MongoRepository<Comment, String> {
    //SpringDataMongoDB，支持通过查询方法名进行查询定义的方式

    //根据文章id查询文章评论数据
    List<Comment> findByArticleid(String articleId);

    //根据发布时间和点赞数查询
    //List<Comment> findByPublishdateAndThumbup(Date date, Integer thumbup);

    //根据用户id查询，并且根据发布时间倒序排序
    //List<Comment> findByUseridOrderByPublishdateDesc(String userid);
}
