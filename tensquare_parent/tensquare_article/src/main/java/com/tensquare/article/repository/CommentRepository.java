package com.tensquare.article.repository;

import com.tensquare.article.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

//使用SpringDataMongoDB，继承MongoRepository接口即可，内置了常用方法
//<操作对象，主键类型>
public interface CommentRepository extends MongoRepository<Comment, String> {
}
